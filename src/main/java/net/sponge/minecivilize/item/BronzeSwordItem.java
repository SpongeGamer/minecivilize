package net.sponge.minecivilize.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.sponge.minecivilize.init.MinecivilizeModItems;

public class BronzeSwordItem extends SwordItem {
    private static final Tier TOOL_TIER = new Tier() {
        @Override
        public int getUses() {
            return 200;
        }

        @Override
        public float getSpeed() {
            return 5f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(MinecivilizeModItems.BRONZE_INGOT.get());
        }
    };

    public BronzeSwordItem() {
        super(TOOL_TIER, new Item.Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 5.0f, -2.4f)));
    }
} 