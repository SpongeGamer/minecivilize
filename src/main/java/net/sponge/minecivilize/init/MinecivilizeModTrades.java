/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.sponge.minecivilize.init;

import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber
public class MinecivilizeModTrades {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void registerTrades(VillagerTradesEvent event) {
		if (event.getType() == MinecivilizeModVillagerProfessions.HISTORIAN.get()) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(Items.BOOK), new ItemStack(MinecivilizeModItems.CIV_BOOK.get()), 10, 10, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.EMERALD), ItemStack.EMPTY, new ItemStack(MinecivilizeModItems.WOODEN_CLUB.get()), 10, 10, 0.05f));
		}
	}
}
