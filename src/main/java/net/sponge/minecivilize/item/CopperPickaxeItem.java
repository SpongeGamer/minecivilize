package net.sponge.minecivilize.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tiers;

public class CopperPickaxeItem extends PickaxeItem {
    public CopperPickaxeItem() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).durability(250));
    }
} 