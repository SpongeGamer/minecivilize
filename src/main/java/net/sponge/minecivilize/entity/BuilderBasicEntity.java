package net.sponge.minecivilize.entity;

import net.sponge.minecivilize.world.inventory.BuilderInventoryMenu;
import net.sponge.minecivilize.world.inventory.BuilderControlMenu;
import net.sponge.minecivilize.procedures.BuilderOpenGUIProcedure;
import net.sponge.minecivilize.init.MinecivilizeModItems;

import net.neoforged.neoforge.items.wrapper.EntityHandsInvWrapper;
import net.neoforged.neoforge.items.wrapper.EntityArmorInvWrapper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.tags.ItemTags;

public class BuilderBasicEntity extends PathfinderMob implements MenuProvider {
	private BlockPos targetTree;
	private int chopProgress;
	private final ItemStackHandler inventoryHandler = new ItemStackHandler(8) {
		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}
	};
	private final CombinedInvWrapper combined = new CombinedInvWrapper(inventoryHandler, new EntityHandsInvWrapper(this), new EntityArmorInvWrapper(this));
	
	private static final int SEARCH_RADIUS = 10; // Радиус поиска деревьев
	private static final int MAX_TREE_HEIGHT = 30; // Максимальная высота дерева для поиска
	private int expectedLogCount = 0; // Ожидаемое количество блоков дерева
	private int collectedLogCount = 0; // Собранное количество блоков дерева
	private boolean waitingForLogs = false; // Ожидание сбора всех блоков
	private int waitForLogsTimeout = 0; // Таймаут ожидания блоков
	
	// Переменные для отслеживания игрока, с которым взаимодействует строитель
	private UUID interactingPlayerUUID;
	private boolean isInteracting = false;
	public int interactionTimeout = 0;
	
	public enum State {
		IDLE,
		SEARCHING,
		CHOPPING,
		FOLLOWING,
		WAITING,
		FLEEING // Новое состояние для убегания от мобов
	}
	
	private State currentState = State.IDLE;
	private UUID ownerUUID = null;
	private boolean swingingArm = false;
	private LivingEntity fleeTarget = null; // Цель, от которой убегаем

	public BuilderBasicEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_AXE));
		this.chopProgress = 0;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Monster.class, (float) 8, 1.2, 1.5));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
	}

	@Override
	public void tick() {
		super.tick();
		
		// Обработка анимации взмаха топором
		if (swingingArm) {
			if (this.swingTime == 0) {
				this.swingingArm = false;
			}
		}
		
		// Если строитель взаимодействует с игроком, смотрим на него
		if (isInteracting) {
			if (interactionTimeout > 0) {
				interactionTimeout--;
				
				// Ищем игрока по UUID
				if (interactingPlayerUUID != null) {
					Player player = level().getPlayerByUUID(interactingPlayerUUID);
					if (player != null && player.isAlive()) {
						// Смотрим на игрока
						this.getLookControl().setLookAt(player, 30.0F, 30.0F);
						
						// Сбрасываем таймаут, если игрок рядом и у него открыт интерфейс
						if (this.distanceToSqr(player) < 64 && player.containerMenu != null && 
							(player.containerMenu instanceof BuilderControlMenu || player.containerMenu instanceof BuilderInventoryMenu)) {
							interactionTimeout = 20; // Сбрасываем таймаут на 1 секунду
						}
					}
				}
			} else {
				// Время взаимодействия истекло
				isInteracting = false;
				interactingPlayerUUID = null;
				// НЕ сбрасываем состояние при окончании взаимодействия
				// Строитель должен продолжать выполнять свою задачу
			}
			
			// Не выполняем обычную логику, пока взаимодействуем
			return;
		}
		
		// Если ожидаем сбора всех блоков дерева
		if (waitingForLogs) {
			if (waitForLogsTimeout > 0) {
				waitForLogsTimeout--;
				
				// Проверяем предметы рядом
				List<ItemEntity> items = level().getEntitiesOfClass(ItemEntity.class, 
					this.getBoundingBox().inflate(5.0), 
					item -> item.isAlive() && !item.hasPickUpDelay() && 
						(item.getItem().is(ItemTags.LOGS) || item.getItem().is(ItemTags.SAPLINGS))
				);
				
				// Собираем предметы
				for (ItemEntity item : items) {
					// Перемещаем предмет к строителю
					Vec3 motion = new Vec3(
						this.getX() - item.getX(),
						this.getY() - item.getY() + 0.5,
						this.getZ() - item.getZ()
					).normalize().scale(0.3);
					
					item.setDeltaMovement(motion);
					
					// Если предмет достаточно близко, собираем его
					if (this.distanceToSqr(item) < 1.5) {
						ItemStack stack = item.getItem();
						
						// Проверяем, является ли предмет бревном
						if (stack.is(ItemTags.LOGS)) {
							collectedLogCount += stack.getCount();
						}
						
						// Добавляем предмет в инвентарь
						for (int i = 0; i < this.inventoryHandler.getSlots(); i++) {
							if (this.inventoryHandler.getStackInSlot(i).isEmpty()) {
								this.inventoryHandler.setStackInSlot(i, stack.copy());
								item.discard();
								break;
							} else if (ItemStack.matches(this.inventoryHandler.getStackInSlot(i), stack) && 
									   this.inventoryHandler.getStackInSlot(i).getCount() + stack.getCount() <= this.inventoryHandler.getStackInSlot(i).getMaxStackSize()) {
								this.inventoryHandler.getStackInSlot(i).grow(stack.getCount());
								item.discard();
								break;
							}
						}
					}
				}
				
				// Если собрали все блоки или инвентарь полон, продолжаем поиск
				if (collectedLogCount >= expectedLogCount || isInventoryFull()) {
					waitingForLogs = false;
					currentState = State.IDLE;
					
					// Ищем следующее дерево
					findNextTree();
					
					// Если нашли дерево, переходим к нему
					if (targetTree != null) {
						currentState = State.SEARCHING;
						this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
					}
				}
			} else {
				// Время ожидания истекло, продолжаем поиск
				waitingForLogs = false;
				currentState = State.IDLE;
				
				// Ищем следующее дерево
				findNextTree();
				
				// Если нашли дерево, переходим к нему
				if (targetTree != null) {
					currentState = State.SEARCHING;
					this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
				}
			}
			
			return;
		}
		
		// Проверяем наличие монстров рядом
		if (currentState != State.FLEEING && !isInteracting) {
			List<Monster> monsters = level().getEntitiesOfClass(Monster.class, 
				this.getBoundingBox().inflate(8.0), 
				monster -> monster.isAlive() && monster.hasLineOfSight(this)
			);
			
			if (!monsters.isEmpty()) {
				// Убегаем от ближайшего монстра
				Monster nearestMonster = monsters.get(0);
				double nearestDistance = this.distanceToSqr(nearestMonster);
				
				for (Monster monster : monsters) {
					double distance = this.distanceToSqr(monster);
					if (distance < nearestDistance) {
						nearestDistance = distance;
						nearestMonster = monster;
					}
				}
				
				// Меняем состояние на FLEEING
				currentState = State.FLEEING;
				
				// Убегаем в противоположном направлении
				Vec3 direction = this.position().subtract(nearestMonster.position()).normalize();
				BlockPos fleeTarget = this.blockPosition().offset(
					(int)(direction.x * 10), 
					0, 
					(int)(direction.z * 10)
				);
				
				// Находим безопасную позицию для бегства
				BlockPos safePos = findSafeFleePosition(fleeTarget);
				if (safePos != null) {
					this.getNavigation().moveTo(safePos.getX(), safePos.getY(), safePos.getZ(), 1.2);
				}
				
				// Воспроизводим звук испуга
				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
					BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.villager.hurt")), 
					this.getSoundSource(), 1.0F, 1.0F);
				
				return;
			}
		}
		
		// Если убегаем от монстра
		if (currentState == State.FLEEING) {
			// Проверяем, есть ли еще монстры рядом
			List<Monster> monsters = level().getEntitiesOfClass(Monster.class, 
				this.getBoundingBox().inflate(12.0), 
				monster -> monster.isAlive() && monster.hasLineOfSight(this)
			);
			
			if (monsters.isEmpty() || this.getNavigation().isDone()) {
				// Если монстров нет или мы достигли безопасного места, возвращаемся к предыдущему состоянию
				currentState = State.IDLE;
				
				// Ищем ближайшее дерево, если до этого рубили деревья
				if (targetTree != null) {
					findNextTree();
					if (targetTree != null) {
						currentState = State.SEARCHING;
						this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
					}
				}
			}
			
			return;
		}
		
		// Основная логика поведения строителя
			switch (currentState) {
				case IDLE:
				// В режиме ожидания ищем ближайшее дерево
				if (targetTree == null) {
						findNextTree();
					
					// Если нашли дерево, переходим к нему
					if (targetTree != null) {
						currentState = State.SEARCHING;
						this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
					}
					}
					break;
					
				case SEARCHING:
					if (targetTree != null) {
						if (this.distanceToSqr(Vec3.atCenterOf(targetTree)) < 4) {
							currentState = State.CHOPPING;
							this.getNavigation().stop();
						
						// Оцениваем размер дерева
						expectedLogCount = estimateTreeSize(targetTree);
						collectedLogCount = 0;
						}
					} else {
						currentState = State.IDLE;
					}
					break;
					
				case CHOPPING:
					if (targetTree != null) {
					// Смотрим на дерево
					this.getLookControl().setLookAt(
						targetTree.getX() + 0.5, 
						targetTree.getY() + 0.5, 
						targetTree.getZ() + 0.5, 
						30.0F, 30.0F
					);
					
					// Анимация взмаха топором каждые 10 тиков
					if (chopProgress % 10 == 0) {
						// Явно указываем правую руку для взмаха
						this.swing(InteractionHand.MAIN_HAND);
						swingingArm = true;
						
						// Воспроизводим звук рубки дерева
						this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
							BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wood.hit")), 
							this.getSoundSource(), 1.0F, 1.0F);
					}
					
						chopProgress++;
						if (chopProgress >= 40) { // 2 секунды
							BlockState state = level().getBlockState(targetTree);
						
						// Проверяем, что блок всё ещё является деревом
						if (state.is(BlockTags.LOGS)) {
							level().destroyBlock(targetTree, true);
							
							// Ищем следующий блок дерева для рубки
							BlockPos nextLog = findNextLogBlock(targetTree);
							if (nextLog != null) {
								// Если нашли следующий блок, продолжаем рубку
								targetTree = nextLog;
								
								// Если блок слишком высоко или далеко, перемещаемся ближе
								double distanceSq = this.distanceToSqr(Vec3.atCenterOf(targetTree));
								if (distanceSq > 16 || targetTree.getY() - this.getY() > 3) {
									currentState = State.SEARCHING;
									this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
								}
							} else {
								// Если не нашли следующий блок, ждем сбора всех блоков
								waitingForLogs = true;
								waitForLogsTimeout = 100; // 5 секунд ожидания
								targetTree = null;
								
								// Воспроизводим звук завершения
								this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
									BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.villager.yes")), 
									this.getSoundSource(), 1.0F, 1.0F);
							}
						} else {
							// Если блок уже не дерево, ищем следующий
							BlockPos nextLog = findNextLogBlock(targetTree);
							if (nextLog != null) {
								targetTree = nextLog;
								
								// Если блок слишком высоко или далеко, перемещаемся ближе
								double distanceSq = this.distanceToSqr(Vec3.atCenterOf(targetTree));
								if (distanceSq > 16 || targetTree.getY() - this.getY() > 3) {
									currentState = State.SEARCHING;
									this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
								}
							} else {
								// Если не нашли следующий блок, ждем сбора всех блоков
								waitingForLogs = true;
								waitForLogsTimeout = 100; // 5 секунд ожидания
								targetTree = null;
								
								// Воспроизводим звук завершения
								this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
									BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.villager.yes")), 
									this.getSoundSource(), 1.0F, 1.0F);
							}
						}
						
							chopProgress = 0;
					}
				} else {
							currentState = State.IDLE;
				}
				break;
				
			case FOLLOWING:
				if (ownerUUID != null) {
					Player owner = level().getPlayerByUUID(ownerUUID);
					if (owner != null && owner.isAlive()) {
						// Если слишком далеко от владельца, идем к нему
						if (this.distanceToSqr(owner) > 16) {
							this.getNavigation().moveTo(owner, 1.0);
						} else if (!this.getNavigation().isInProgress()) {
							// Если достаточно близко и не движемся, обновляем путь
							this.getNavigation().moveTo(owner, 1.0);
						}
						
						// Смотрим на владельца
						this.getLookControl().setLookAt(owner, 30.0F, 30.0F);
					} else {
						// Если владелец не найден, переходим в режим ожидания
						currentState = State.WAITING;
						}
					} else {
						currentState = State.IDLE;
					}
					break;
				
			case WAITING:
				// В режиме ожидания просто стоим на месте
				this.getNavigation().stop();
				break;
		}
	}

	// Метод для оценки размера дерева
	private int estimateTreeSize(BlockPos startPos) {
		int count = 0;
		List<BlockPos> checked = new ArrayList<>();
		List<BlockPos> toCheck = new ArrayList<>();
		
		toCheck.add(startPos);
		
		while (!toCheck.isEmpty() && count < 100) { // Ограничиваем поиск 100 блоками
			BlockPos pos = toCheck.remove(0);
			
			if (checked.contains(pos)) continue;
			checked.add(pos);
			
			if (level().getBlockState(pos).is(BlockTags.LOGS)) {
				count++;
				
				// Добавляем соседние блоки для проверки
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						for (int z = -1; z <= 1; z++) {
							if (x == 0 && y == 0 && z == 0) continue;
							
							BlockPos neighbor = pos.offset(x, y, z);
							if (!checked.contains(neighbor)) {
								toCheck.add(neighbor);
							}
						}
					}
				}
				
				// Проверяем блоки выше (для высоких деревьев)
				for (int y = 2; y <= 5; y++) {
					BlockPos above = pos.above(y);
					if (!checked.contains(above)) {
						toCheck.add(above);
					}
				}
			}
		}
		
		// Добавляем небольшой запас, так как могли не учесть все блоки
		return Math.max(count, 1) + 2;
	}

	private boolean findNextTree() {
		if (level().isClientSide) return false;
		
		// Ищем ближайшее дерево в радиусе SEARCH_RADIUS блоков
		BlockPos nearestTree = null;
		double nearestDistance = Double.MAX_VALUE;
		
		// Получаем текущую позицию строителя
		BlockPos builderPos = this.blockPosition();
		
		// Ищем дерево в радиусе SEARCH_RADIUS блоков
		for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
			for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
				// Проверяем расстояние по горизонтали
				if (x * x + z * z > SEARCH_RADIUS * SEARCH_RADIUS) continue;
				
				// Проверяем блоки на разных высотах
				for (int y = -3; y <= 5; y++) {
					BlockPos pos = builderPos.offset(x, y, z);
					
					// Проверяем, является ли блок деревом
					if (isTreeLog(pos)) {
						// Проверяем, стоит ли блок на земле или на другом блоке дерева
						if (isValidTreeBase(pos)) {
					double distance = this.distanceToSqr(Vec3.atCenterOf(pos));
							if (distance < nearestDistance) {
								nearestDistance = distance;
								nearestTree = pos;
							}
							// Прерываем поиск по высоте, если нашли дерево
							break;
						}
					}
				}
			}
		}
		
		// Если нашли дерево, устанавливаем его как цель
		if (nearestTree != null) {
			targetTree = nearestTree;
			expectedLogCount = estimateTreeSize(nearestTree);
			collectedLogCount = 0;
			return true;
		}
		
		return false;
	}

	// Метод для поиска самого нижнего блока дерева
	private BlockPos findLowestLogBlock(BlockPos startPos) {
		BlockPos currentPos = startPos;
		BlockPos belowPos = currentPos.below();
		
		// Спускаемся вниз, пока находим блоки дерева
		while (level().getBlockState(belowPos).is(BlockTags.LOGS)) {
			currentPos = belowPos;
			belowPos = currentPos.below();
		}
		
		return currentPos;
	}

	// Метод для поиска следующего блока дерева для рубки
	private BlockPos findNextLogBlock(BlockPos currentPos) {
		// Проверяем блок выше (приоритет 1)
		BlockPos abovePos = currentPos.above();
		if (level().getBlockState(abovePos).is(BlockTags.LOGS)) {
			return abovePos;
		}
		
		// Проверяем соседние блоки по горизонтали (приоритет 2)
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0) continue; // Пропускаем текущий блок
				
				BlockPos neighborPos = currentPos.offset(x, 0, z);
				if (level().getBlockState(neighborPos).is(BlockTags.LOGS)) {
					return neighborPos;
				}
			}
		}
		
		// Проверяем блоки по диагонали выше (приоритет 3)
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0) continue; // Пропускаем текущий блок
				
				BlockPos diagonalPos = currentPos.offset(x, 1, z);
				if (level().getBlockState(diagonalPos).is(BlockTags.LOGS)) {
					return diagonalPos;
				}
			}
		}
		
		// Проверяем блоки на 2 блока выше (приоритет 4)
		BlockPos twoAbovePos = currentPos.above(2);
		if (level().getBlockState(twoAbovePos).is(BlockTags.LOGS)) {
			return twoAbovePos;
		}
		
		// Проверяем блоки на 2 блока выше по диагонали (приоритет 5)
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0) continue; // Пропускаем текущий блок
				
				BlockPos highDiagonalPos = currentPos.offset(x, 2, z);
				if (level().getBlockState(highDiagonalPos).is(BlockTags.LOGS)) {
					return highDiagonalPos;
				}
			}
		}
		
		// Проверяем блоки на 3 блока выше (приоритет 6)
		BlockPos threeAbovePos = currentPos.above(3);
		if (level().getBlockState(threeAbovePos).is(BlockTags.LOGS)) {
			return threeAbovePos;
		}
		
		// Расширенный поиск в радиусе 3 блоков (приоритет 7)
		for (int y = -1; y <= 3; y++) {
			for (int x = -3; x <= 3; x++) {
				for (int z = -3; z <= 3; z++) {
					// Пропускаем уже проверенные блоки
					if ((y == 0 && Math.abs(x) <= 1 && Math.abs(z) <= 1) || 
						(y == 1 && Math.abs(x) <= 1 && Math.abs(z) <= 1) ||
						(y == 2 && Math.abs(x) <= 1 && Math.abs(z) <= 1) ||
						(y == 3 && x == 0 && z == 0)) {
						continue;
					}
					
					BlockPos checkPos = currentPos.offset(x, y, z);
					if (level().getBlockState(checkPos).is(BlockTags.LOGS)) {
						return checkPos;
					}
				}
			}
		}
		
		return null; // Не нашли следующий блок
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public Vec3 getPassengerRidingPosition(Entity entity) {
		return super.getPassengerRidingPosition(entity).add(0, -0.35F, 0);
	}

	protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource source, boolean recentlyHitIn) {
		super.dropCustomDeathLoot(serverLevel, source, recentlyHitIn);
		this.spawnAtLocation(new ItemStack(Blocks.OAK_LOG));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.villager.ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.death"));
	}

	public CombinedInvWrapper getCombinedInventory() {
		return combined;
	}

	@Override
	protected void dropEquipment() {
		super.dropEquipment();
		for (int i = 0; i < inventoryHandler.getSlots(); ++i) {
			ItemStack itemstack = inventoryHandler.getStackInSlot(i);
			if (!itemstack.isEmpty() && !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
				this.spawnAtLocation(itemstack);
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.put("InventoryCustom", inventoryHandler.serializeNBT(this.registryAccess()));
		
		// Сохраняем состояние взаимодействия
		if (isInteracting && interactingPlayerUUID != null) {
			compound.putBoolean("IsInteracting", isInteracting);
			compound.putUUID("InteractingPlayer", interactingPlayerUUID);
			compound.putInt("InteractionTimeout", interactionTimeout);
		}
		
		// Сохраняем текущее состояние
		compound.putInt("CurrentState", currentState.ordinal());
		
		// Сохраняем UUID владельца, если есть
		if (ownerUUID != null) {
			compound.putUUID("OwnerUUID", ownerUUID);
		}
		
		// Сохраняем данные о рубке дерева
		if (waitingForLogs) {
			compound.putBoolean("WaitingForLogs", waitingForLogs);
			compound.putInt("ExpectedLogCount", expectedLogCount);
			compound.putInt("CollectedLogCount", collectedLogCount);
			compound.putInt("WaitForLogsTimeout", waitForLogsTimeout);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.get("InventoryCustom") instanceof CompoundTag inventoryTag)
			inventoryHandler.deserializeNBT(this.registryAccess(), inventoryTag);
			
		// Загружаем состояние взаимодействия
		if (compound.contains("IsInteracting")) {
			isInteracting = compound.getBoolean("IsInteracting");
			if (compound.hasUUID("InteractingPlayer")) {
				interactingPlayerUUID = compound.getUUID("InteractingPlayer");
			}
			if (compound.contains("InteractionTimeout")) {
				interactionTimeout = compound.getInt("InteractionTimeout");
			}
		}
		
		// Загружаем текущее состояние
		if (compound.contains("CurrentState")) {
			int stateOrdinal = compound.getInt("CurrentState");
			if (stateOrdinal >= 0 && stateOrdinal < State.values().length) {
				currentState = State.values()[stateOrdinal];
			}
		}
		
		// Загружаем UUID владельца, если есть
		if (compound.hasUUID("OwnerUUID")) {
			ownerUUID = compound.getUUID("OwnerUUID");
		}
		
		// Загружаем данные о рубке дерева
		if (compound.contains("WaitingForLogs")) {
			waitingForLogs = compound.getBoolean("WaitingForLogs");
			expectedLogCount = compound.getInt("ExpectedLogCount");
			collectedLogCount = compound.getInt("CollectedLogCount");
			waitForLogsTimeout = compound.getInt("WaitForLogsTimeout");
		}
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Строитель");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
		packetBuffer.writeBlockPos(this.blockPosition());
		packetBuffer.writeByte(0);
		packetBuffer.writeVarInt(this.getId());
		return new BuilderInventoryMenu(id, inventory, packetBuffer);
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
			// Останавливаем текущие действия только на время взаимодействия
			this.getNavigation().stop();
			// НЕ сбрасываем состояние при начале взаимодействия
			// Сохраняем текущее состояние, чтобы продолжить после взаимодействия
			
			// Устанавливаем состояние взаимодействия
			this.isInteracting = true;
			this.interactingPlayerUUID = player.getUUID();
			this.interactionTimeout = 200; // 10 секунд
			
			// Поворачиваемся к игроку
			this.getLookControl().setLookAt(player, 30.0F, 30.0F);
			
				// Открываем меню управления
				FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
				packetBuffer.writeBlockPos(this.blockPosition());
				packetBuffer.writeByte(0);
				packetBuffer.writeVarInt(this.getId());
				serverPlayer.openMenu(new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("Управление строителем");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new BuilderControlMenu(id, inventory, packetBuffer);
					}
				}, extraData -> {
					extraData.writeBlockPos(this.blockPosition());
					extraData.writeByte(0);
					extraData.writeVarInt(this.getId());
				});
				return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 2);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1);
		return builder;
	}

	public State getCurrentState() {
		return this.currentState;
	}

	public BlockPos getTargetTree() {
		return this.targetTree;
	}
	
	public boolean isWaitingForLogs() {
		return this.waitingForLogs;
	}
	
	public int getExpectedLogCount() {
		return this.expectedLogCount;
	}
	
	public int getCollectedLogCount() {
		return this.collectedLogCount;
	}
	
	// Метод для получения информации о сборе дерева
	public String getTreeHarvestingInfo() {
		if (waitingForLogs) {
			return "Собрано блоков: " + collectedLogCount + "/" + expectedLogCount;
		} else if (targetTree != null) {
			return "Цель: X=" + targetTree.getX() + ", Y=" + targetTree.getY() + ", Z=" + targetTree.getZ();
		}
		return "";
	}
	
	// Метод для проверки, взаимодействует ли строитель с игроком
	public boolean isInteracting() {
		return isInteracting;
	}
	
	// Метод для сброса взаимодействия
	public void stopInteracting() {
		isInteracting = false;
		interactingPlayerUUID = null;
		interactionTimeout = 0;
	}

	// Метод для установки режима следования за игроком
	public void followPlayer(Player player) {
		if (player != null) {
			// Сохраняем UUID только того игрока, который вызвал команду
			this.ownerUUID = player.getUUID();
			this.currentState = State.FOLLOWING;
			this.getNavigation().moveTo(player, 1.0);
		}
	}

	// Метод для установки режима ожидания
	public void waitForPlayer() {
		this.currentState = State.WAITING;
		this.getNavigation().stop();
	}

	// Метод для установки режима добычи деревьев
	public void startHarvestingTrees() {
		// Сбрасываем текущую цель и состояние
		this.targetTree = null;
		this.waitingForLogs = false;
		this.expectedLogCount = 0;
		this.collectedLogCount = 0;
		this.chopProgress = 0;
		
		// Устанавливаем состояние IDLE, чтобы начать поиск дерева
		this.currentState = State.IDLE;
		
		// Останавливаем текущее движение
		this.getNavigation().stop();
		
		// Немедленно ищем дерево
		findNextTree();
		
		// Если дерево найдено, устанавливаем состояние SEARCHING
		if (targetTree != null) {
			this.currentState = State.SEARCHING;
			this.getNavigation().moveTo(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0);
		}
		
		// Воспроизводим звук подтверждения
		if (!level().isClientSide) {
			level().playSound(null, this.getX(), this.getY(), this.getZ(), 
				BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.villager.work_fletcher")), 
				this.getSoundSource(), 1.0F, 1.0F);
		}
	}

	// Проверяет, является ли блок частью дерева
	private boolean isTreeLog(BlockPos pos) {
		BlockState state = level().getBlockState(pos);
		return state.is(BlockTags.LOGS);
	}
	
	// Проверяет, является ли основание дерева допустимым
	private boolean isValidTreeBase(BlockPos pos) {
		// Проверяем блок снизу
		BlockPos below = pos.below();
		BlockState belowState = level().getBlockState(below);
		
		// Допустимые основания: земля, другой блок дерева, или листва
		return belowState.is(BlockTags.DIRT) || 
			   belowState.is(BlockTags.LOGS) || 
			   belowState.is(BlockTags.LEAVES) ||
			   belowState.is(Blocks.GRASS_BLOCK) ||
			   belowState.is(Blocks.PODZOL) ||
			   belowState.is(Blocks.MYCELIUM);
	}
	
	// Находит безопасную позицию для бегства
	private BlockPos findSafeFleePosition(BlockPos target) {
		// Проверяем, безопасна ли целевая позиция
		if (isSafePosition(target)) {
			return target;
		}
		
		// Ищем безопасную позицию рядом с целевой
		for (int r = 1; r <= 10; r++) {
			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					if (Math.abs(x) != r && Math.abs(z) != r) continue; // Проверяем только периметр
					
					BlockPos pos = target.offset(x, 0, z);
					if (isSafePosition(pos)) {
						return pos;
					}
				}
			}
		}
		
		// Если не нашли безопасную позицию, возвращаем null
		return null;
	}
	
	// Проверяет, безопасна ли позиция для перемещения
	private boolean isSafePosition(BlockPos pos) {
		// Проверяем, что блок проходим
		if (!level().getBlockState(pos).isAir()) {
			return false;
		}
		
		// Проверяем, что блок снизу твердый
		if (!level().getBlockState(pos.below()).isSolid()) {
			return false;
		}
		
		// Проверяем, что блок сверху проходим
		if (!level().getBlockState(pos.above()).isAir()) {
			return false;
		}
		
		return true;
	}
	
	// Проверяет, полон ли инвентарь
	private boolean isInventoryFull() {
		for (int i = 0; i < this.inventoryHandler.getSlots(); i++) {
			if (this.inventoryHandler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	// Обработка команд от игрока
	public void handleCommand(int command) {
		switch (command) {
			case 1: // Ждать
				this.currentState = State.WAITING;
				this.getNavigation().stop();
				this.targetTree = null;
				break;
			case 2: // Следовать
				this.currentState = State.FOLLOWING;
				this.targetTree = null;
				break;
			case 3: // Добыча деревьев
				startHarvestingTrees();
				break;
		}
	}
}
