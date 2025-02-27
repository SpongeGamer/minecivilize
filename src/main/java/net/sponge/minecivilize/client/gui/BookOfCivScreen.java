package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.BookOfCivMenu;
import net.sponge.minecivilize.network.BookOfCivButtonMessage;

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

public class BookOfCivScreen extends AbstractContainerScreen<BookOfCivMenu> {
	private final static HashMap<String, Object> guistate = BookOfCivMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_rim;
	Button button_giermaniia;
	Button button_rossiia;

	public BookOfCivScreen(BookOfCivMenu container, Inventory inventory, Component text) {
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

	private static final ResourceLocation texture = ResourceLocation.parse("minecivilize:textures/screens/book_of_civ.png");

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
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.book_of_civ.label_dobro_pozhalovat_v_tiekhnodiemku_mo"), 53, 14, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.book_of_civ.label_dobro_pozhalovat_v_tiekhnodiemku_mo1"), 52, 14, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.book_of_civ.label_chut_pozzhie_ia_pridam_etomu_bozhiesk"), 70, 28, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.book_of_civ.label_chut_pozzhie_ia_pridam_etomu_bozhiesk1"), 69, 28, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.book_of_civ.label_a_poka_vybieri_tsivilizatsiiu"), 105, 41, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.book_of_civ.label_a_poka_vybieri_tsivilizatsiiu1"), 104, 41, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_rim = Button.builder(Component.translatable("gui.minecivilize.book_of_civ.button_rim"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new BookOfCivButtonMessage(0, x, y, z));
				BookOfCivButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 52, this.topPos + 124, 41, 20).build();
		guistate.put("button:button_rim", button_rim);
		this.addRenderableWidget(button_rim);
		button_giermaniia = Button.builder(Component.translatable("gui.minecivilize.book_of_civ.button_giermaniia"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new BookOfCivButtonMessage(1, x, y, z));
				BookOfCivButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 133, this.topPos + 124, 68, 20).build();
		guistate.put("button:button_giermaniia", button_giermaniia);
		this.addRenderableWidget(button_giermaniia);
		button_rossiia = Button.builder(Component.translatable("gui.minecivilize.book_of_civ.button_rossiia"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new BookOfCivButtonMessage(2, x, y, z));
				BookOfCivButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}).bounds(this.leftPos + 232, this.topPos + 124, 56, 20).build();
		guistate.put("button:button_rossiia", button_rossiia);
		this.addRenderableWidget(button_rossiia);
	}
}
