
package net.sponge.minecivilize.network;

import net.sponge.minecivilize.world.inventory.CivRussiaChooseMenu;
import net.sponge.minecivilize.procedures.PlayerChooseRussiaProcedure;
import net.sponge.minecivilize.procedures.CivBookOpenProcedure;
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

import java.util.HashMap;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record CivRussiaChooseButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final Type<CivRussiaChooseButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MinecivilizeMod.MODID, "civ_russia_choose_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CivRussiaChooseButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, CivRussiaChooseButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new CivRussiaChooseButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
	@Override
	public Type<CivRussiaChooseButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final CivRussiaChooseButtonMessage message, final IPayloadContext context) {
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
		HashMap guistate = CivRussiaChooseMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			CivBookOpenProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {

			PlayerChooseRussiaProcedure.execute(entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MinecivilizeMod.addNetworkMessage(CivRussiaChooseButtonMessage.TYPE, CivRussiaChooseButtonMessage.STREAM_CODEC, CivRussiaChooseButtonMessage::handleData);
	}
}
