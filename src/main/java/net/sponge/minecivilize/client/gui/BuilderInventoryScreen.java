package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.BuilderInventoryMenu;
import net.sponge.minecivilize.network.BuilderInventoryButtonMessage;

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

public class BuilderInventoryScreen extends AbstractContainerScreen<BuilderInventoryMenu> {
	private final static HashMap<String, Object> guistate = BuilderInventoryMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_zakryt;

	private static final ResourceLocation TEXTURE = ResourceLocation.parse("minecivilize:textures/screens/builder_inventory.png");

	public BuilderInventoryScreen(BuilderInventoryMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.world = menu.world;
		this.x = menu.x;
		this.y = menu.y;
		this.z = menu.z;
		this.entity = menu.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
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
	}

	@Override
	public void init() {
		super.init();
		button_zakryt = Button.builder(Component.translatable("gui.minecivilize.builder_inventory.button_zakryt"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new BuilderInventoryButtonMessage(0, x, y, z));
				BuilderInventoryButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 18, this.topPos + 52, 65, 20).build();
		guistate.put("button:button_zakryt", button_zakryt);
		this.addRenderableWidget(button_zakryt);
	}
}
