/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.debug.entity;

import java.util.function.Consumer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.payload.AdvancedAddEntityPayload;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.registration.RegistrationHelper;

@ForEachTest(groups = EntityTests.GROUP)
public class EntityTests {
    public static final String GROUP = "level.entity";

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests if custom fence gates without wood types work, allowing for the use of the vanilla block for non-wooden gates")
    static void customSpawnLogic(final DynamicTest test, final RegistrationHelper reg) {
        final var usingForgeAdvancedSpawn = reg.entityTypes().registerEntityType("complex_spawn", CustomComplexSpawnEntity::new, MobCategory.AMBIENT, builder -> builder.sized(1, 1))
                .withLang("Custom complex spawn egg").withRenderer(() -> NoopRenderer::new);
        final var usingCustomPayloadsSpawn = reg.entityTypes().registerEntityType("adapted_spawn", AdaptedSpawnEntity::new, MobCategory.AMBIENT, builder -> builder.sized(1, 1))
                .withLang("Adapted complex spawn egg").withRenderer(() -> NoopRenderer::new);
        final var simpleSpawn = reg.entityTypes().registerEntityType("simple_spawn", SimpleEntity::new, MobCategory.AMBIENT, builder -> builder.sized(1, 1))
                .withLang("Simple spawn egg").withRenderer(() -> NoopRenderer::new);

        reg.eventListeners().accept((Consumer<RegisterPayloadHandlersEvent>) event -> event.registrar("1")
                .playToClient(EntityTests.CustomSyncPayload.TYPE, CustomSyncPayload.STREAM_CODEC, (payload, context) -> {}));

        test.onGameTest(helper -> {
            helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
                    .thenExecute(() -> helper.spawn(usingForgeAdvancedSpawn.get(), new BlockPos(1, 1, 1)))

                    // Check if forge payload was sent
                    .thenExecute(player -> helper.assertTrue(
                            player.getOutboundPayloads(AdvancedAddEntityPayload.class)
                                    .findAny().isPresent(),
                            "Advanced payload for custom spawn was not send"))
                    .thenSucceed();
            helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
                    .thenExecute(() -> helper.spawn(usingCustomPayloadsSpawn.get(), new BlockPos(1, 1, 1)))

                    // Check if custom payload was sent
                    .thenExecute(player -> helper.assertTrue(
                            player.getOutboundPayloads(CustomSyncPayload.class)
                                    .findAny().isPresent(),
                            "Custom sync payload for adapted spawn was not send"))
                    .thenSucceed();
            helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
                    .thenExecute(() -> helper.spawn(simpleSpawn.get(), new BlockPos(1, 1, 1)))

                    // Check if custom payload was sent
                    .thenExecute(player -> helper.assertTrue(
                            player.getOutboundPayloads(AdvancedAddEntityPayload.class)
                                    .findAny().isEmpty(),
                            "Advanced payload for custom spawn was send"))
                    .thenExecute(player -> helper.assertTrue(
                            player.getOutboundPayloads(CustomSyncPayload.class)
                                    .findAny().isEmpty(),
                            "Custom sync payload for custom spawn was send"))
                    .thenSucceed();
        });
    }

    public static final class CustomComplexSpawnEntity extends Entity implements IEntityWithComplexSpawn {
        public CustomComplexSpawnEntity(EntityType<?> type, Level level) {
            super(type, level);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        public void writeSpawnData(RegistryFriendlyByteBuf buffer) {}

        @Override
        public void readSpawnData(RegistryFriendlyByteBuf additionalData) {}

        @Override
        public boolean hurtServer(ServerLevel p_376804_, DamageSource p_376155_, float p_376892_) {
            return false;
        }
    }

    public static final class AdaptedSpawnEntity extends Entity {
        public AdaptedSpawnEntity(EntityType<?> type, Level level) {
            super(type, level);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        public void sendPairingData(ServerPlayer serverPlayer, Consumer<CustomPacketPayload> bundleBuilder) {
            bundleBuilder.accept(new CustomSyncPayload());
        }

        @Override
        public boolean hurtServer(ServerLevel p_376804_, DamageSource p_376155_, float p_376892_) {
            return false;
        }
    }

    public static final class SimpleEntity extends Entity {
        public SimpleEntity(EntityType<?> type, Level level) {
            super(type, level);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        public boolean hurtServer(ServerLevel p_376804_, DamageSource p_376155_, float p_376892_) {
            return false;
        }
    }

    public record CustomSyncPayload() implements CustomPacketPayload {
        private static final CustomPacketPayload.Type<CustomSyncPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("test", "custom_sync_payload"));
        private static final StreamCodec<FriendlyByteBuf, CustomSyncPayload> STREAM_CODEC = StreamCodec.unit(new EntityTests.CustomSyncPayload());

        @Override
        public CustomPacketPayload.Type<CustomSyncPayload> type() {
            return TYPE;
        }
    }
}
