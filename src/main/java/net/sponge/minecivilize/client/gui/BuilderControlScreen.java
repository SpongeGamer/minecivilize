package net.sponge.minecivilize.client.gui;

import net.sponge.minecivilize.world.inventory.BuilderControlMenu;
import net.sponge.minecivilize.world.inventory.BuilderInventoryMenu;
import net.sponge.minecivilize.entity.BuilderBasicEntity;
import net.sponge.minecivilize.init.MinecivilizeModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import io.netty.buffer.Unpooled;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.ChatFormatting;
import net.sponge.minecivilize.network.BuilderCommandMessage;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import java.util.HashMap;

public class BuilderControlScreen extends AbstractContainerScreen<BuilderControlMenu> {
    // Используем текстуру раздатчика в качестве запасного варианта
    private static final ResourceLocation texture = ResourceLocation.parse("minecraft:textures/gui/container/dispenser.png");
    private final Level world;
    private final Player entity;
    private int x, y, z;
    private final HashMap<String, Object> guistate = new HashMap<>();
    private Button followButton;
    private Button waitButton;
    private Button harvestTreesButton;
    private String currentState = "Неизвестно";
    private long lastStateUpdateTime = 0;
    private int localState = 0; // 0 - неизвестно, 1 - следует, 2 - ждет, 3 - рубит деревья
    private boolean searchingForTree = false;

    public BuilderControlScreen(BuilderControlMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.entity = container.entity;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Обновляем состояние строителя каждые 200 мс
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStateUpdateTime > 200) {
            updateBuilderState();
            lastStateUpdateTime = currentTime;
        }
        
        // Отображаем текущее состояние строителя
        final int stateColor;
        
        // Выбираем цвет в зависимости от состояния
        if (currentState.contains("Убегает")) {
            stateColor = 0xFF0000; // Красный для опасных состояний
        } else if (currentState.contains("дерева") || currentState.contains("Поиск")) {
            stateColor = 0xFFAA00; // Желтый для состояний, связанных с деревьями
        } else {
            stateColor = 0x00FF00; // Зеленый цвет для обычных состояний
        }
        
        // Отображаем текущее состояние
        guiGraphics.drawString(this.font, Component.literal("Состояние: ").append(Component.literal(currentState).withStyle(style -> style.withColor(stateColor))), 
            this.leftPos + 180, this.topPos + 100, 0x404040, false);
        
        // Отображаем радиус поиска
        guiGraphics.drawString(this.font, "Радиус поиска: 10 блоков", this.leftPos + 180, this.topPos + 110, 0x404040, false);
        
        // Отображаем информацию о цели, если строитель ищет или рубит дерево
        BuilderBasicEntity builder = this.menu.getBuilder();
        if (builder != null) {
            if (currentState.contains("Поиск дерева") || currentState.contains("Рубка дерева")) {
                BlockPos targetTree = builder.getTargetTree();
                if (targetTree != null) {
                    guiGraphics.drawString(this.font, "Цель: X=" + targetTree.getX() + ", Y=" + targetTree.getY() + ", Z=" + targetTree.getZ(), 
                        this.leftPos + 180, this.topPos + 120, 0x404040, false);
                } else if (searchingForTree) {
                    guiGraphics.drawString(this.font, "Поиск ближайшего дерева...", this.leftPos + 180, this.topPos + 120, 0x404040, false);
                }
            }
            
            // Отображаем информацию о собранных блоках, если строитель ждет сбора блоков
            if (builder.isWaitingForLogs()) {
                guiGraphics.drawString(this.font, "Собрано блоков: " + builder.getCollectedLogCount() + "/" + builder.getExpectedLogCount(), 
                    this.leftPos + 180, this.topPos + 130, 0x404040, false);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        RenderSystem.disableBlend();
    }

    @Override
    public void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        // Кнопка "Ждать"
        this.waitButton = this.addRenderableWidget(new Button.Builder(Component.literal("Ждать"), e -> {
            this.menu.sendCommand(1);
            localState = 2;
            updateStateFromLocal();
        }).pos(this.leftPos + 180, this.topPos + 20).size(70, 20).build());
        
        // Кнопка "Следовать"
        this.followButton = this.addRenderableWidget(new Button.Builder(Component.literal("Следовать"), e -> {
            this.menu.sendCommand(2);
            localState = 1;
            updateStateFromLocal();
        }).pos(this.leftPos + 180, this.topPos + 45).size(70, 20).build());
        
        // Кнопка "Добыча деревьев"
        this.harvestTreesButton = this.addRenderableWidget(new Button.Builder(Component.literal("Добыча деревьев"), e -> {
            this.menu.sendCommand(3);
            localState = 3;
            searchingForTree = true;
            updateStateFromLocal();
        }).pos(this.leftPos + 180, this.topPos + 70).size(70, 20).build());
        
        // Инициализируем состояние
        updateBuilderState();
    }
    
    private void updateBuilderState() {
        BuilderBasicEntity builder = this.menu.getBuilder();
        if (builder != null) {
            // Получаем текущее состояние строителя
            BuilderBasicEntity.State state = builder.getCurrentState();
            
            // Устанавливаем текст состояния в зависимости от состояния строителя
            switch (state) {
                case IDLE:
                    if (searchingForTree) {
                        currentState = "Поиск дерева";
                    } else if (localState == 1) {
                        currentState = "Следует за игроком";
                    } else if (localState == 2) {
                        currentState = "Ожидание";
                    } else {
                        currentState = "Ожидание";
                    }
                    break;
                case SEARCHING:
                    currentState = "Поиск дерева";
                    searchingForTree = true;
                    break;
                case CHOPPING:
                    currentState = "Рубка дерева";
                    searchingForTree = true;
                    break;
                case FOLLOWING:
                    currentState = "Следует за игроком";
                    localState = 1;
                    searchingForTree = false;
                    break;
                case WAITING:
                    currentState = "Ожидание";
                    localState = 2;
                    searchingForTree = false;
                    break;
                case FLEEING:
                    currentState = "Убегает от монстра";
                    break;
                default:
                    currentState = "Неизвестно";
                    break;
            }
            
            // Обновляем кнопки в зависимости от состояния
            updateButtonStates();
        }
    }
    
    private void updateButtonStates() {
        if (waitButton != null && followButton != null && harvestTreesButton != null) {
            // Активируем/деактивируем кнопки в зависимости от текущего состояния
            waitButton.active = !currentState.equals("Ожидание") || searchingForTree;
            followButton.active = !currentState.equals("Следует за игроком");
            harvestTreesButton.active = !searchingForTree || !currentState.contains("дерева");
        }
    }
    
    private void updateStateFromLocal() {
        BuilderBasicEntity builder = this.menu.getBuilder();
        if (builder != null) {
            // Обновляем локальное состояние и текст состояния
            switch (localState) {
                case 1: // Следует
                    currentState = "Следует за игроком";
                    searchingForTree = false;
                    break;
                case 2: // Ждет
                    currentState = "Ожидание";
                    searchingForTree = false;
                    break;
                case 3: // Рубит деревья
                    currentState = "Поиск дерева";
                    searchingForTree = true;
                    break;
                default:
                    currentState = "Неизвестно";
                    break;
            }
            
            // Обновляем кнопки
            updateButtonStates();
        }
    }
} 