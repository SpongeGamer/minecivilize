package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.CivRomeTableGUIMenu;
import net.sponge.minecivilize.network.CivRomeTableGUIButtonMessage;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class CivRomeTableGUIScreen extends AbstractContainerScreen<CivRomeTableGUIMenu> {
	private final static HashMap<String, Object> guistate = CivRomeTableGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_stroitielstvo;
	Button button_tiekhnologhii;
	Button button_iunity;
	ImageButton imagebutton_rometabletop;

	public CivRomeTableGUIScreen(CivRomeTableGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 340;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = ResourceLocation.parse("minecivilize:textures/screens/civ_rome_table_gui.png");

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
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_table_gui.label_avie_impierator_slava_rimskoi_im"), 82, 16, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecivilize.civ_rome_table_gui.label_avie_impierator_slava_rimskoi_im1"), 81, 16, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_stroitielstvo = Button.builder(Component.translatable("gui.minecivilize.civ_rome_table_gui.button_stroitielstvo"), e -> {
		}).bounds(this.leftPos + 16, this.topPos + 34, 97, 20).build();
		guistate.put("button:button_stroitielstvo", button_stroitielstvo);
		this.addRenderableWidget(button_stroitielstvo);
		button_tiekhnologhii = Button.builder(Component.translatable("gui.minecivilize.civ_rome_table_gui.button_tiekhnologhii"), e -> {
		}).bounds(this.leftPos + 16, this.topPos + 61, 79, 20).build();
		guistate.put("button:button_tiekhnologhii", button_tiekhnologhii);
		this.addRenderableWidget(button_tiekhnologhii);
		button_iunity = Button.builder(Component.translatable("gui.minecivilize.civ_rome_table_gui.button_iunity"), e -> {
			if (true) {
				PacketDistributor.sendToServer(new CivRomeTableGUIButtonMessage(2, x, y, z));
				CivRomeTableGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}).bounds(this.leftPos + 16, this.topPos + 88, 56, 20).build();
		guistate.put("button:button_iunity", button_iunity);
		this.addRenderableWidget(button_iunity);
		imagebutton_rometabletop = new ImageButton(this.leftPos + 302, this.topPos + 12, 16, 16,
				new WidgetSprites(ResourceLocation.parse("minecivilize:textures/screens/rometabletop.png"), ResourceLocation.parse("minecivilize:textures/screens/rometabletop.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		guistate.put("button:imagebutton_rometabletop", imagebutton_rometabletop);
		this.addRenderableWidget(imagebutton_rometabletop);
	}
}
