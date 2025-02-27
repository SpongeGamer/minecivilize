package net.sponge.minecivilize.procedures;

import net.sponge.minecivilize.init.MinecivilizeModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

import java.util.Collections;

public class PlayerChooseRussiaProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player) {
			ItemStack _stktoremove = new ItemStack(MinecivilizeModItems.CIV_BOOK.get());
			_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
		}
		if (entity instanceof ServerPlayer _serverPlayer)
			_serverPlayer.awardRecipesByKey(Collections.singletonList(ResourceLocation.parse("russian_table")));
		if (entity instanceof Player _player)
			_player.closeContainer();
		if (entity instanceof ServerPlayer _player) {
			AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("minecivilize:choose_russia"));
			if (_adv != null) {
				AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
		}
	}
}
