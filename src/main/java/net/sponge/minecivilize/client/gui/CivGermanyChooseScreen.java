package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.CivGermanyChooseMenu;
import net.sponge.minecivilize.network.CivGermanyChooseButtonMessage;

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

public class CivGermanyChooseScreen extends AbstractContainerScreen<CivGermanyChooseMenu> {
	private final static HashMap<String, Object> guistate = CivGermanyChooseMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_nazad;
	Button button_za_fatierliand;

	public CivGermanyChooseScreen(CivGermanyChooseMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 340;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = ResourceLocation.parse("minecivilize:textures/screens/civ_germany_choose.png");

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

		guiGraphics.blit(ResourceLocation.parse("minecivilize:textures/screens/germanflag.png"), this.leftPos + 203, this.topPos + 12, 0, 0, 16, 16, 16, 16);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_giermaniia1"), 151, 16, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_giermaniia"), 150, 16, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_da_zdravstvuiet_barbarossa"), 18, 34, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_da_zdravstvuiet_barbarossa1"), 17, 34, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_unikalnyi_iunit_tievtonskii_rytsa"), 18, 52, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_unikalnyi_iunit_tievtonskii_rytsa1"), 17, 52, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_unikalnoie_zdaniie_ganza"), 18, 70, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_germany_choose.label_unikalnoie_zdaniie_ganza1"), 17, 70, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_nazad = Button.builder(Component.translatable("gui.minecivilize.civ_germany_choose.button_nazad"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivGermanyChooseButtonMessage(0, x, y, z));
				CivGermanyChooseButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 25, this.topPos + 133, 53, 20).build();
		guistate.put("button:button_nazad", button_nazad);
		this.addRenderableWidget(button_nazad);
		button_za_fatierliand = Button.builder(Component.translatable("gui.minecivilize.civ_germany_choose.button_za_fatierliand"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivGermanyChooseButtonMessage(1, x, y, z));
				CivGermanyChooseButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 223, this.topPos + 133, 94, 20).build();
		guistate.put("button:button_za_fatierliand", button_za_fatierliand);
		this.addRenderableWidget(button_za_fatierliand);
	}
}
