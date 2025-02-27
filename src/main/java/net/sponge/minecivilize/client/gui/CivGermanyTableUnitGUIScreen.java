package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.CivGermanyTableUnitGUIMenu;
import net.sponge.minecivilize.network.CivGermanyTableUnitGUIButtonMessage;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class CivGermanyTableUnitGUIScreen extends AbstractContainerScreen<CivGermanyTableUnitGUIMenu> {
	private final static HashMap<String, Object> guistate = CivGermanyTableUnitGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_stroitiel;
	Button button_voin;
	Button button_prashchnik;
	Button button_nazad;

	public CivGermanyTableUnitGUIScreen(CivGermanyTableUnitGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 340;
		this.imageHeight = 166;
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	private static final ResourceLocation texture = ResourceLocation.parse("minecivilize:textures/screens/civ_germany_table_unit_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(ResourceLocation.parse("minecivilize:textures/screens/breadgui.png"), this.leftPos + 175, this.topPos + 44, 0, 0, 16, 16, 16, 16);

		guiGraphics.blit(ResourceLocation.parse("minecivilize:textures/screens/woodgui.png"), this.leftPos + 217, this.topPos + 44, 0, 0, 16, 16, 16, 16);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.label_mieniu_sozdaniia_iunitov"), 115, 14, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.label_mieniu_sozdaniia_iunitov1"), 114, 14, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.label_usloviie_5_5_5"), 108, 48, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.label_usloviie_5_5"), 107, 48, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_stroitiel = Button.builder(Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.button_stroitiel"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivGermanyTableUnitGUIButtonMessage(0, x, y, z));
				CivGermanyTableUnitGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 25, this.topPos + 43, 75, 20).build();
		guistate.put("button:button_stroitiel", button_stroitiel);
		this.addRenderableWidget(button_stroitiel);
		button_voin = Button.builder(Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.button_voin"), e -> {
		}).bounds(this.leftPos + 25, this.topPos + 70, 46, 20).build();
		guistate.put("button:button_voin", button_voin);
		this.addRenderableWidget(button_voin);
		button_prashchnik = Button.builder(Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.button_prashchnik"), e -> {
		}).bounds(this.leftPos + 25, this.topPos + 97, 63, 20).build();
		guistate.put("button:button_prashchnik", button_prashchnik);
		this.addRenderableWidget(button_prashchnik);
		button_nazad = Button.builder(Component.translatable("gui.minecivilize.civ_germany_table_unit_gui.button_nazad"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivGermanyTableUnitGUIButtonMessage(3, x, y, z));
				CivGermanyTableUnitGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		}).bounds(this.leftPos + 25, this.topPos + 133, 53, 20).build();
		guistate.put("button:button_nazad", button_nazad);
		this.addRenderableWidget(button_nazad);
	}
}
