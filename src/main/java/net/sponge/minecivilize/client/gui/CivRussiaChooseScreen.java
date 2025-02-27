package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.CivRussiaChooseMenu;
import net.sponge.minecivilize.network.CivRussiaChooseButtonMessage;

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

public class CivRussiaChooseScreen extends AbstractContainerScreen<CivRussiaChooseMenu> {
	private final static HashMap<String, Object> guistate = CivRussiaChooseMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_nazad;
	Button button_za_rodinu;

	public CivRussiaChooseScreen(CivRussiaChooseMenu container, Inventory inventory, Component text) {
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

	private static final ResourceLocation texture = ResourceLocation.parse("minecivilize:textures/screens/civ_russia_choose.png");

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

		guiGraphics.blit(ResourceLocation.parse("minecivilize:textures/screens/russianflag.png"), this.leftPos + 207, this.topPos + 12, 0, 0, 16, 16, 16, 16);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_rossiia"), 162, 16, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_rossiia1"), 161, 16, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_za_vieru_otchiznu_i_tsaria"), 18, 25, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_za_vieru_otchiznu_i_tsaria1"), 17, 25, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_unikalnyi_iunit_strieltsy"), 18, 43, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_unikalnyi_iunit_strieltsy1"), 17, 43, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_unikalnoie_zdaniie_impieratorskii"), 18, 61, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_russia_choose.label_unikalnoie_zdaniie_impieratorskii1"), 17, 61, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_nazad = Button.builder(Component.translatable("gui.minecivilize.civ_russia_choose.button_nazad"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivRussiaChooseButtonMessage(0, x, y, z));
				CivRussiaChooseButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 16, this.topPos + 133, 53, 20).build();
		guistate.put("button:button_nazad", button_nazad);
		this.addRenderableWidget(button_nazad);
		button_za_rodinu = Button.builder(Component.translatable("gui.minecivilize.civ_russia_choose.button_za_rodinu"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivRussiaChooseButtonMessage(1, x, y, z));
				CivRussiaChooseButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 241, this.topPos + 133, 73, 20).build();
		guistate.put("button:button_za_rodinu", button_za_rodinu);
		this.addRenderableWidget(button_za_rodinu);
	}
}
