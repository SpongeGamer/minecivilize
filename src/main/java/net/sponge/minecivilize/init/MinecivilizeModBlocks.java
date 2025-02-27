/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.sponge.minecivilize.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sponge.minecivilize.MinecivilizeMod;
import net.sponge.minecivilize.block.TinOreBlock;
import net.sponge.minecivilize.block.DeepslateTinOreBlock;
import net.sponge.minecivilize.block.RomeTableBlockBlock;
import net.sponge.minecivilize.block.GermanyTableBlockBlock;
import net.sponge.minecivilize.block.RussianTableBlockBlock;

public class MinecivilizeModBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MinecivilizeMod.MODID);

	public static final DeferredBlock<Block> TIN_ORE = registerBlock("tin_ore", TinOreBlock::new);
	public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore", DeepslateTinOreBlock::new);
	public static final DeferredBlock<Block> ROME_TABLE_BLOCK = registerBlock("rome_table_block", RomeTableBlockBlock::new);
	public static final DeferredBlock<Block> GERMANY_TABLE_BLOCK = registerBlock("germany_table_block", GermanyTableBlockBlock::new);
	public static final DeferredBlock<Block> RUSSIAN_TABLE_BLOCK = registerBlock("russian_table_block", RussianTableBlockBlock::new);

	private static <T extends Block> DeferredBlock<T> registerBlock(String name, java.util.function.Supplier<T> block) {
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
		MinecivilizeModItems.REGISTRY.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
