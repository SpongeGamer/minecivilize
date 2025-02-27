/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.sponge.minecivilize.init;

import net.sponge.minecivilize.world.inventory.CivRussianTableUnitGUIMenu;
import net.sponge.minecivilize.world.inventory.CivRussianTableGUIMenu;
import net.sponge.minecivilize.world.inventory.CivRussiaChooseMenu;
import net.sponge.minecivilize.world.inventory.CivRomeTableUnitGUIMenu;
import net.sponge.minecivilize.world.inventory.CivRomeTableGUIMenu;
import net.sponge.minecivilize.world.inventory.CivRomeChooseMenu;
import net.sponge.minecivilize.world.inventory.CivGermanyTableUnitGUIMenu;
import net.sponge.minecivilize.world.inventory.CivGermanyChooseMenu;
import net.sponge.minecivilize.world.inventory.CivGermanTableGUIMenu;
import net.sponge.minecivilize.world.inventory.BuilderInventoryMenu;
import net.sponge.minecivilize.world.inventory.BookOfCivMenu;
import net.sponge.minecivilize.world.inventory.BuilderControlMenu;
import net.sponge.minecivilize.MinecivilizeMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.registries.Registries;

public class MinecivilizeModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, MinecivilizeMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<BookOfCivMenu>> BOOK_OF_CIV = REGISTRY.register("book_of_civ", () -> IMenuTypeExtension.create(BookOfCivMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivRomeChooseMenu>> CIV_ROME_CHOOSE = REGISTRY.register("civ_rome_choose", () -> IMenuTypeExtension.create(CivRomeChooseMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivGermanyChooseMenu>> CIV_GERMANY_CHOOSE = REGISTRY.register("civ_germany_choose", () -> IMenuTypeExtension.create(CivGermanyChooseMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivRussiaChooseMenu>> CIV_RUSSIA_CHOOSE = REGISTRY.register("civ_russia_choose", () -> IMenuTypeExtension.create(CivRussiaChooseMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivRomeTableGUIMenu>> CIV_ROME_TABLE_GUI = REGISTRY.register("civ_rome_table_gui", () -> IMenuTypeExtension.create(CivRomeTableGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivRussianTableGUIMenu>> CIV_RUSSIAN_TABLE_GUI = REGISTRY.register("civ_russian_table_gui", () -> IMenuTypeExtension.create(CivRussianTableGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivGermanTableGUIMenu>> CIV_GERMAN_TABLE_GUI = REGISTRY.register("civ_german_table_gui", () -> IMenuTypeExtension.create(CivGermanTableGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivRomeTableUnitGUIMenu>> CIV_ROME_TABLE_UNIT_GUI = REGISTRY.register("civ_rome_table_unit_gui", () -> IMenuTypeExtension.create(CivRomeTableUnitGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivGermanyTableUnitGUIMenu>> CIV_GERMANY_TABLE_UNIT_GUI = REGISTRY.register("civ_germany_table_unit_gui", () -> IMenuTypeExtension.create(CivGermanyTableUnitGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CivRussianTableUnitGUIMenu>> CIV_RUSSIAN_TABLE_UNIT_GUI = REGISTRY.register("civ_russian_table_unit_gui", () -> IMenuTypeExtension.create(CivRussianTableUnitGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BuilderInventoryMenu>> BUILDER_INVENTORY = REGISTRY.register("builder_inventory", () -> IMenuTypeExtension.create(BuilderInventoryMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BuilderControlMenu>> BUILDER_CONTROL = REGISTRY.register("builder_control",
			() -> IMenuTypeExtension.create((id, inv, data) -> new BuilderControlMenu(id, inv, data)));

	public static void register(IEventBus bus) {
		REGISTRY.register(bus);
	}
}
