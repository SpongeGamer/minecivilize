package net.sponge.minecivilize.network;

import net.sponge.minecivilize.procedures.CloseTheTabProcedure;
import net.sponge.minecivilize.procedures.BuilderOpenInventoryForPlayerProcedure;
import net.sponge.minecivilize.procedures.HarvestTreeProcedure;
import net.sponge.minecivilize.MinecivilizeMod;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record BuilderGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final Type<BuilderGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MinecivilizeMod.MODID, "builder_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BuilderGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, BuilderGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new BuilderGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
	@Override
	public Type<BuilderGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final BuilderGUIButtonMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;
				handleButtonAction(entity, buttonID, x, y, z);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			BuilderOpenInventoryForPlayerProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {

			CloseTheTabProcedure.execute(entity);
		}
		if (buttonID == 2) {

			HarvestTreeProcedure.execute(world, x, y, z, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MinecivilizeMod.addNetworkMessage(BuilderGUIButtonMessage.TYPE, BuilderGUIButtonMessage.STREAM_CODEC, BuilderGUIButtonMessage::handleData);
	}
}
