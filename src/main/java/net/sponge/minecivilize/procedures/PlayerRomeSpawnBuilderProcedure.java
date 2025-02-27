package net.sponge.minecivilize.procedures;

import net.sponge.minecivilize.init.MinecivilizeModEntities;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

public class PlayerRomeSpawnBuilderProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double fireHeight = 0;
		if ((entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(Items.BREAD)) : false)
				&& (entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(Blocks.OAK_LOG)) : false)) {
			if (entity instanceof Player _player) {
				ItemStack _stktoremove = new ItemStack(Items.BREAD);
				_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 5, _player.inventoryMenu.getCraftSlots());
			}
			if (entity instanceof Player _player) {
				ItemStack _stktoremove = new ItemStack(Blocks.OAK_LOG);
				_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 5, _player.inventoryMenu.getCraftSlots());
			}
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = MinecivilizeModEntities.BUILDER_BASIC.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (entity instanceof ServerPlayer _player) {
				AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("minecivilize:builder_spawned"));
				if (_adv != null) {
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						for (String criteria : _ap.getRemainingCriteria())
							_player.getAdvancements().award(_adv, criteria);
					}
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(
						Component.literal(
								"\u0411\u0440\u0430\u0442\u0438\u0448\u043A\u0430, \u0443 \u0442\u0435\u0431\u044F \u043D\u0435\u0442 \u0440\u0435\u0441\u0443\u0440\u0441\u043E\u0432. \u0418\u0434\u0438 \u0434\u043E\u0431\u044B\u0432\u0430\u0439."),
						false);
		}
	}
}
