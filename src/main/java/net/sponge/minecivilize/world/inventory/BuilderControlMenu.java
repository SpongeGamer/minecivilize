package net.sponge.minecivilize.world.inventory;

import net.sponge.minecivilize.init.MinecivilizeModMenus;
import net.sponge.minecivilize.entity.BuilderBasicEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sponge.minecivilize.network.BuilderCommandMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BuilderControlMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;
    private Entity boundEntity;
    private IItemHandler builderInventory;

    public BuilderControlMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MinecivilizeModMenus.BUILDER_CONTROL.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();

        if (extraData != null) {
            BlockPos pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            
            // Пропускаем байт, который используется для других целей
            extraData.readByte();
            
            // Читаем ID сущности
            int entityId = extraData.readVarInt();
            
            // Получаем сущность по ID
            if (world != null) {
                Entity loadedEntity = world.getEntity(entityId);
                if (loadedEntity instanceof BuilderBasicEntity builder) {
                    this.boundEntity = loadedEntity;
                    this.bound = true;
                    this.builderInventory = builder.getCombinedInventory();
                    
                    // Добавляем слоты инвентаря строителя (3x3 сетка как в раздатчике)
                    int slotIndex = 0;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (slotIndex < 8) { // У строителя только 8 слотов
                                this.addSlot(new SlotItemHandler(builderInventory, slotIndex, 62 + j * 18, 17 + i * 18) {
                                    @Override
                                    public boolean mayPlace(ItemStack stack) {
                                        return true;
                                    }
                                });
                                slotIndex++;
                            }
                        }
                    }
                }
            }
        }
        
        // Добавляем слоты инвентаря игрока
        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
        
        // Добавляем слоты хотбара игрока
        for (int sk = 0; sk < 9; ++sk)
            this.addSlot(new Slot(inv, sk, 8 + sk * 18, 142));
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.boundEntity != null) {
            return this.boundEntity.isAlive() && player.distanceToSqr(this.boundEntity) < 64;
        }
        return true;
    }

    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }

    public Entity getBoundEntity() {
        return boundEntity;
    }

    public BuilderBasicEntity getBuilder() {
        if (boundEntity instanceof BuilderBasicEntity builder) {
            return builder;
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            
            // Если слот из инвентаря строителя (первые 8 слотов)
            if (index < 8) {
                if (!this.moveItemStackTo(itemstack1, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 8, false)) { // Перемещаем в инвентарь строителя
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return itemstack;
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        
        // При закрытии меню НЕ сбрасываем состояние строителя
        if (boundEntity instanceof BuilderBasicEntity builder) {
            // Устанавливаем небольшую задержку перед сбросом взаимодействия
            // чтобы строитель еще немного посмотрел на игрока
            builder.interactionTimeout = 60; // 3 секунды
            
            // Если игрок отошел слишком далеко, сразу сбрасываем взаимодействие
            if (player.distanceToSqr(boundEntity) > 64) {
                builder.stopInteracting();
            }
            
            // НЕ меняем текущее состояние строителя
            // Он должен продолжать выполнять свою задачу
        }
    }

    // Метод для отправки команды строителю
    public void sendCommand(int command) {
        BuilderBasicEntity builder = getBuilder();
        if (builder != null && entity instanceof net.minecraft.client.player.LocalPlayer) {
            // Создаем сообщение
            BuilderCommandMessage message = new BuilderCommandMessage(builder.getId(), command);
            
            // Отправляем команду на сервер
            net.minecraft.client.Minecraft.getInstance().getConnection().send(
                new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(message)
            );
        }
    }
} 