package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.CivRomeChooseMenu;
import net.sponge.minecivilize.network.CivRomeChooseButtonMessage;

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

public class CivRomeChooseScreen extends AbstractContainerScreen<CivRomeChooseMenu> {
	private final static HashMap<String, Object> guistate = CivRomeChooseMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_nazad;
	Button button_avie;

	public CivRomeChooseScreen(CivRomeChooseMenu container, Inventory inventory, Component text) {
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

	private static final ResourceLocation texture = ResourceLocation.parse("minecivilize:textures/screens/civ_rome_choose.png");

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

		guiGraphics.blit(ResourceLocation.parse("minecivilize:textures/screens/rometabletop.png"), this.leftPos + 192, this.topPos + 5, 0, 0, 16, 16, 16, 16);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_rim"), 163, 9, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_rim1"), 162, 9, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_ogho_rim_da_nu_znachit_avie"), 17, 25, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_ogho_rim_da_nu_znachit_avie1"), 16, 25, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_unikalnyi_iunit_lieghionier"), 17, 43, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_unikalnyi_iunit_lieghionier1"), 16, 43, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_unikalnoie_stroieniie_bani"), 17, 61, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_choose.label_empty"), 16, 61, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_nazad = Button.builder(Component.translatable("gui.minecivilize.civ_rome_choose.button_nazad"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivRomeChooseButtonMessage(0, x, y, z));
				CivRomeChooseButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 16, this.topPos + 133, 53, 20).build();
		guistate.put("button:button_nazad", button_nazad);
		this.addRenderableWidget(button_nazad);
		button_avie = Button.builder(Component.translatable("gui.minecivilize.civ_rome_choose.button_avie"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivRomeChooseButtonMessage(1, x, y, z));
				CivRomeChooseButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 277, this.topPos + 133, 43, 20).build();
		guistate.put("button:button_avie", button_avie);
		this.addRenderableWidget(button_avie);
	}
}
