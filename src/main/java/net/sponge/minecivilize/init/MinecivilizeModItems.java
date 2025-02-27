/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.sponge.minecivilize.init;

import net.sponge.minecivilize.item.WoodenClubItem;
import net.sponge.minecivilize.item.CopperBladeItem;
import net.sponge.minecivilize.item.CopperAxeItem;
import net.sponge.minecivilize.item.CivBookItem;
import net.sponge.minecivilize.item.RawTinItem;
import net.sponge.minecivilize.item.TinIngotItem;
import net.sponge.minecivilize.item.BronzeIngotItem;
import net.sponge.minecivilize.item.CopperPickaxeItem;
import net.sponge.minecivilize.item.CopperShovelItem;
import net.sponge.minecivilize.item.CopperHoeItem;
import net.sponge.minecivilize.item.BronzePickaxeItem;
import net.sponge.minecivilize.item.BronzeShovelItem;
import net.sponge.minecivilize.item.BronzeHoeItem;
import net.sponge.minecivilize.item.BronzeSwordItem;
import net.sponge.minecivilize.item.BronzeAxeItem;
import net.sponge.minecivilize.item.BronzeArmorMaterial;
import net.sponge.minecivilize.MinecivilizeMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.core.Holder;

public class MinecivilizeModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, MinecivilizeMod.MODID);
	public static final DeferredHolder<Item, Item> CIV_BOOK = REGISTRY.register("civ_book", CivBookItem::new);
	public static final DeferredHolder<Item, Item> WOODEN_CLUB = REGISTRY.register("wooden_club", WoodenClubItem::new);
	public static final DeferredHolder<Item, Item> BUILDER_BASIC_SPAWN_EGG = REGISTRY.register("builder_basic_spawn_egg", 
			() -> new DeferredSpawnEggItem(MinecivilizeModEntities.BUILDER_BASIC, -7238516, -1, new Item.Properties()));
	public static final DeferredHolder<Item, Item> COPPER_BLADE = REGISTRY.register("copper_blade", CopperBladeItem::new);
	public static final DeferredHolder<Item, Item> COPPER_AXE = REGISTRY.register("copper_axe", CopperAxeItem::new);
	public static final DeferredHolder<Item, Item> RAW_TIN = REGISTRY.register("raw_tin", RawTinItem::new);
	public static final DeferredHolder<Item, Item> TIN_INGOT = REGISTRY.register("tin_ingot", TinIngotItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_INGOT = REGISTRY.register("bronze_ingot", BronzeIngotItem::new);
	public static final DeferredHolder<Item, Item> COPPER_PICKAXE = REGISTRY.register("copper_pickaxe", CopperPickaxeItem::new);
	public static final DeferredHolder<Item, Item> COPPER_SHOVEL = REGISTRY.register("copper_shovel", CopperShovelItem::new);
	public static final DeferredHolder<Item, Item> COPPER_HOE = REGISTRY.register("copper_hoe", CopperHoeItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_PICKAXE = REGISTRY.register("bronze_pickaxe", BronzePickaxeItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_SHOVEL = REGISTRY.register("bronze_shovel", BronzeShovelItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_HOE = REGISTRY.register("bronze_hoe", BronzeHoeItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_SWORD = REGISTRY.register("bronze_sword", BronzeSwordItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_AXE = REGISTRY.register("bronze_axe", BronzeAxeItem::new);
	public static final DeferredHolder<Item, Item> BRONZE_HELMET = REGISTRY.register("bronze_helmet", 
		() -> new ArmorItem((Holder<ArmorMaterial>)BronzeArmorMaterial.BRONZE, ArmorItem.Type.HELMET, 
			new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(12))));
	public static final DeferredHolder<Item, Item> BRONZE_CHESTPLATE = REGISTRY.register("bronze_chestplate", 
		() -> new ArmorItem((Holder<ArmorMaterial>)BronzeArmorMaterial.BRONZE, ArmorItem.Type.CHESTPLATE, 
			new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(12))));
	public static final DeferredHolder<Item, Item> BRONZE_LEGGINGS = REGISTRY.register("bronze_leggings", 
		() -> new ArmorItem((Holder<ArmorMaterial>)BronzeArmorMaterial.BRONZE, ArmorItem.Type.LEGGINGS, 
			new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(12))));
	public static final DeferredHolder<Item, Item> BRONZE_BOOTS = REGISTRY.register("bronze_boots", 
		() -> new ArmorItem((Holder<ArmorMaterial>)BronzeArmorMaterial.BRONZE, ArmorItem.Type.BOOTS, 
			new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(12))));
}
