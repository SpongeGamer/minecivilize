/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.sponge.minecivilize.init;

import net.sponge.minecivilize.client.gui.CivRussianTableUnitGUIScreen;
import net.sponge.minecivilize.client.gui.CivRussianTableGUIScreen;
import net.sponge.minecivilize.client.gui.CivRussiaChooseScreen;
import net.sponge.minecivilize.client.gui.CivRomeTableUnitGUIScreen;
import net.sponge.minecivilize.client.gui.CivRomeTableGUIScreen;
import net.sponge.minecivilize.client.gui.CivRomeChooseScreen;
import net.sponge.minecivilize.client.gui.CivGermanyTableUnitGUIScreen;
import net.sponge.minecivilize.client.gui.CivGermanyChooseScreen;
import net.sponge.minecivilize.client.gui.CivGermanTableGUIScreen;
import net.sponge.minecivilize.client.gui.BuilderInventoryScreen;
import net.sponge.minecivilize.client.gui.BookOfCivScreen;
import net.sponge.minecivilize.client.gui.BuilderControlScreen;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MinecivilizeModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(MinecivilizeModMenus.BOOK_OF_CIV.get(), BookOfCivScreen::new);
		event.register(MinecivilizeModMenus.CIV_ROME_CHOOSE.get(), CivRomeChooseScreen::new);
		event.register(MinecivilizeModMenus.CIV_GERMANY_CHOOSE.get(), CivGermanyChooseScreen::new);
		event.register(MinecivilizeModMenus.CIV_RUSSIA_CHOOSE.get(), CivRussiaChooseScreen::new);
		event.register(MinecivilizeModMenus.CIV_ROME_TABLE_GUI.get(), CivRomeTableGUIScreen::new);
		event.register(MinecivilizeModMenus.CIV_RUSSIAN_TABLE_GUI.get(), CivRussianTableGUIScreen::new);
		event.register(MinecivilizeModMenus.CIV_GERMAN_TABLE_GUI.get(), CivGermanTableGUIScreen::new);
		event.register(MinecivilizeModMenus.CIV_ROME_TABLE_UNIT_GUI.get(), CivRomeTableUnitGUIScreen::new);
		event.register(MinecivilizeModMenus.CIV_GERMANY_TABLE_UNIT_GUI.get(), CivGermanyTableUnitGUIScreen::new);
		event.register(MinecivilizeModMenus.CIV_RUSSIAN_TABLE_UNIT_GUI.get(), CivRussianTableUnitGUIScreen::new);
		event.register(MinecivilizeModMenus.BUILDER_INVENTORY.get(), BuilderInventoryScreen::new);
		event.register(MinecivilizeModMenus.BUILDER_CONTROL.get(), BuilderControlScreen::new);
		// event.register(MinecivilizeModMenus.BUILDER_GUI.get(), BuilderGUIScreen::new);
	}
}
