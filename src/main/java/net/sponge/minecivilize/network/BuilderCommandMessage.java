package net.sponge.minecivilize.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.sponge.minecivilize.entity.BuilderBasicEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sponge.minecivilize.MinecivilizeMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * Сообщение для отправки команд строителю
 */
public class BuilderCommandMessage implements CustomPacketPayload {
    public static final Type<BuilderCommandMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MinecivilizeMod.MODID, "builder_command"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BuilderCommandMessage> STREAM_CODEC = StreamCodec.of(
        (RegistryFriendlyByteBuf buffer, BuilderCommandMessage message) -> {
            buffer.writeInt(message.entityId);
            buffer.writeInt(message.command);
        }, 
        (RegistryFriendlyByteBuf buffer) -> new BuilderCommandMessage(buffer.readInt(), buffer.readInt())
    );
    
    private final int entityId;
    private final int command;

    /**
     * Конструктор для создания сообщения
     * @param entityId ID сущности строителя
     * @param command ID команды (1 = следовать, 2 = ждать, 3 = добывать деревья)
     */
    public BuilderCommandMessage(int entityId, int command) {
        this.entityId = entityId;
        this.command = command;
    }

    /**
     * Чтение сообщения из буфера
     */
    public static BuilderCommandMessage read(FriendlyByteBuf buffer) {
        return new BuilderCommandMessage(buffer.readInt(), buffer.readInt());
    }

    /**
     * Обработчик сообщения на сервере
     */
    public static void handle(BuilderCommandMessage message, ServerPlayer player) {
        if (player != null) {
            ServerLevel level = player.serverLevel();
            Entity entity = level.getEntity(message.entityId);
            
            if (entity instanceof BuilderBasicEntity builder) {
                // Обрабатываем команду
                builder.handleCommand(message.command);
            }
        }
    }
    
    /**
     * Возвращает тип сообщения (реализация интерфейса CustomPacketPayload)
     */
    @Override
    public Type<BuilderCommandMessage> type() {
        return TYPE;
    }
    
    /**
     * Получить ID сущности
     */
    public int getEntityId() {
        return entityId;
    }
    
    /**
     * Получить команду
     */
    public int getCommand() {
        return command;
    }
} 