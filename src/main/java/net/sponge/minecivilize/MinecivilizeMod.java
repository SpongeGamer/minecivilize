package net.sponge.minecivilize;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.sponge.minecivilize.init.MinecivilizeModVillagerProfessions;
import net.sponge.minecivilize.init.MinecivilizeModTabs;
import net.sponge.minecivilize.init.MinecivilizeModMenus;
import net.sponge.minecivilize.init.MinecivilizeModItems;
import net.sponge.minecivilize.init.MinecivilizeModEntities;
import net.sponge.minecivilize.init.MinecivilizeModBlocks;
import net.sponge.minecivilize.init.MinecivilizeModRecipes;
import net.sponge.minecivilize.network.BuilderCommandMessage;
import net.sponge.minecivilize.datagen.ModBlockLootTableProvider;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.ConnectionProtocol;
import java.util.List;
import java.util.Optional;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.sponge.minecivilize.item.BronzeArmorMaterial;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.sponge.minecivilize.datagen.ModItemModelProvider;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.sponge.minecivilize.worldgen.ModBiomeModifiers;
import net.sponge.minecivilize.worldgen.ModConfiguredFeatures;
import net.sponge.minecivilize.worldgen.ModPlacedFeatures;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.Set;

@Mod(MinecivilizeMod.MODID)
public class MinecivilizeMod {
	public static final Logger LOGGER = LogManager.getLogger(MinecivilizeMod.class);
	public static final String MODID = "minecivilize";

	public MinecivilizeMod(IEventBus modEventBus) {
		MinecivilizeModBlocks.BLOCKS.register(modEventBus);
		MinecivilizeModItems.REGISTRY.register(modEventBus);
		MinecivilizeModEntities.REGISTRY.register(modEventBus);
		MinecivilizeModVillagerProfessions.PROFESSIONS.register(modEventBus);
		MinecivilizeModMenus.REGISTRY.register(modEventBus);
		MinecivilizeModTabs.REGISTRY.register(modEventBus);
		MinecivilizeModRecipes.RECIPE_TYPES.register(modEventBus);
		MinecivilizeModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
		BronzeArmorMaterial.ARMOR_MATERIALS.register(modEventBus);

		modEventBus.addListener(this::registerNetworking);
		modEventBus.addListener(this::addCreative);
		
		NeoForge.EVENT_BUS.register(this);
	}

	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		if (networkingRegistered)
			return;
		networkingRegistered = true;
		
		PayloadRegistrar registrar = event.registrar(MODID);
		
		addNetworkMessage(
			BuilderCommandMessage.TYPE,
			BuilderCommandMessage.STREAM_CODEC,
			(message, context) -> {
				context.enqueueWork(() -> {
					Player player = context.player();
					if (player instanceof ServerPlayer serverPlayer) {
						BuilderCommandMessage.handle(message, serverPlayer);
					}
				});
			}
		);
	}

	private static final Collection<WorkQueueEntry> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		workQueue.add(new WorkQueueEntry(action, tick));
	}

	private static class WorkQueueEntry {
		private final Runnable action;
		private int tick;

		public WorkQueueEntry(Runnable action, int tick) {
			this.action = action;
			this.tick = tick;
		}

		public Runnable getAction() {
			return action;
		}

		public int getTick() {
			return tick;
		}
		
		public void setTick(int tick) {
			this.tick = tick;
		}
	}

	@SubscribeEvent
	public void tick(ServerTickEvent.Post event) {
		if (event.getServer().getTickCount() % 20 == 0) {
			workQueue.forEach(message -> {
				Runnable action = message.getAction();
				int tick = message.getTick();
				if (tick <= 0)
					action.run();
			});
			workQueue.removeIf(message -> message.getTick() <= 0);
			workQueue.forEach(message -> message.setTick(message.getTick() - 1));
		}
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == MinecivilizeModTabs.MINE_CIVILIZE.getKey()) {
			// Предметы уже добавлены в MinecivilizeModTabs.java
		}
	}
}
