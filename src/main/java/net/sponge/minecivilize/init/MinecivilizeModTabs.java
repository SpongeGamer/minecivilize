/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.sponge.minecivilize.init;

import net.sponge.minecivilize.MinecivilizeMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class MinecivilizeModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinecivilizeMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MINE_CIVILIZE = REGISTRY.register("mine_civilize",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.minecivilize.mine_civilize")).icon(() -> new ItemStack(MinecivilizeModItems.CIV_BOOK.get())).displayItems((parameters, tabData) -> {
				// Блоки
				tabData.accept(MinecivilizeModBlocks.ROME_TABLE_BLOCK.get().asItem());
				tabData.accept(MinecivilizeModBlocks.GERMANY_TABLE_BLOCK.get().asItem());
				tabData.accept(MinecivilizeModBlocks.RUSSIAN_TABLE_BLOCK.get().asItem());
				tabData.accept(MinecivilizeModBlocks.TIN_ORE.get().asItem());
				tabData.accept(MinecivilizeModBlocks.DEEPSLATE_TIN_ORE.get().asItem());
						
				// Материалы
				tabData.accept(MinecivilizeModItems.RAW_TIN.get());
				tabData.accept(MinecivilizeModItems.TIN_INGOT.get());
				tabData.accept(MinecivilizeModItems.BRONZE_INGOT.get());
						
				// Примитивные инструменты
				tabData.accept(MinecivilizeModItems.WOODEN_CLUB.get());
				
				// Медные инструменты
				tabData.accept(MinecivilizeModItems.COPPER_BLADE.get());
				tabData.accept(MinecivilizeModItems.COPPER_PICKAXE.get());
				tabData.accept(MinecivilizeModItems.COPPER_AXE.get());
				tabData.accept(MinecivilizeModItems.COPPER_SHOVEL.get());
				tabData.accept(MinecivilizeModItems.COPPER_HOE.get());
				
				// Бронзовые инструменты
				tabData.accept(MinecivilizeModItems.BRONZE_SWORD.get());
				tabData.accept(MinecivilizeModItems.BRONZE_PICKAXE.get());
				tabData.accept(MinecivilizeModItems.BRONZE_AXE.get());
				tabData.accept(MinecivilizeModItems.BRONZE_SHOVEL.get());
				tabData.accept(MinecivilizeModItems.BRONZE_HOE.get());
						
				// Бронзовая броня
				tabData.accept(MinecivilizeModItems.BRONZE_HELMET.get());
				tabData.accept(MinecivilizeModItems.BRONZE_CHESTPLATE.get());
				tabData.accept(MinecivilizeModItems.BRONZE_LEGGINGS.get());
				tabData.accept(MinecivilizeModItems.BRONZE_BOOTS.get());
						
				// Прочее
				tabData.accept(MinecivilizeModItems.CIV_BOOK.get());
				tabData.accept(MinecivilizeModItems.BUILDER_BASIC_SPAWN_EGG.get());
			}).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(MinecivilizeModItems.BUILDER_BASIC_SPAWN_EGG.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			// Медные инструменты
			tabData.accept(MinecivilizeModItems.COPPER_BLADE.get());
			tabData.accept(MinecivilizeModItems.COPPER_AXE.get());
			tabData.accept(MinecivilizeModItems.COPPER_PICKAXE.get());
			tabData.accept(MinecivilizeModItems.COPPER_SHOVEL.get());
			tabData.accept(MinecivilizeModItems.COPPER_HOE.get());
			
			// Бронзовые инструменты
			tabData.accept(MinecivilizeModItems.BRONZE_SWORD.get());
			tabData.accept(MinecivilizeModItems.BRONZE_AXE.get());
			tabData.accept(MinecivilizeModItems.BRONZE_PICKAXE.get());
			tabData.accept(MinecivilizeModItems.BRONZE_SHOVEL.get());
			tabData.accept(MinecivilizeModItems.BRONZE_HOE.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			tabData.accept(MinecivilizeModItems.RAW_TIN.get());
			tabData.accept(MinecivilizeModItems.TIN_INGOT.get());
			tabData.accept(MinecivilizeModItems.BRONZE_INGOT.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.COMBAT) {
			// Медные инструменты
			tabData.accept(MinecivilizeModItems.COPPER_BLADE.get());
			
			// Бронзовые инструменты
			tabData.accept(MinecivilizeModItems.BRONZE_SWORD.get());
			
			// Бронзовая броня
			tabData.accept(MinecivilizeModItems.BRONZE_HELMET.get());
			tabData.accept(MinecivilizeModItems.BRONZE_CHESTPLATE.get());
			tabData.accept(MinecivilizeModItems.BRONZE_LEGGINGS.get());
			tabData.accept(MinecivilizeModItems.BRONZE_BOOTS.get());
		}
	}
}
