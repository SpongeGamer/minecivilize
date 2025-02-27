
package net.sponge.minecivilize.network;

import net.sponge.minecivilize.world.inventory.CivRomeTableUnitGUIMenu;
import net.sponge.minecivilize.procedures.RomeTableBlockGUIButtonProcedure;
import net.sponge.minecivilize.procedures.PlayerRomeSpawnBuilderProcedure;
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
public record CivRomeTableUnitGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final Type<CivRomeTableUnitGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MinecivilizeMod.MODID, "civ_rome_table_unit_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CivRomeTableUnitGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, CivRomeTableUnitGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new CivRomeTableUnitGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
	@Override
	public Type<CivRomeTableUnitGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final CivRomeTableUnitGUIButtonMessage message, final IPayloadContext context) {
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
		HashMap guistate = CivRomeTableUnitGUIMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			PlayerRomeSpawnBuilderProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 3) {

			RomeTableBlockGUIButtonProcedure.execute(world, x, y, z, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MinecivilizeMod.addNetworkMessage(CivRomeTableUnitGUIButtonMessage.TYPE, CivRomeTableUnitGUIButtonMessage.STREAM_CODEC, CivRomeTableUnitGUIButtonMessage::handleData);
	}
}
