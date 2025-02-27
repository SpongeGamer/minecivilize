package net.sponge.minecivilize.block;

import java.util.function.Supplier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sponge.minecivilize.MinecivilizeMod;
import net.sponge.minecivilize.init.MinecivilizeModItems;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = 
            DeferredRegister.createBlocks(MinecivilizeMod.MODID);

    public static final DeferredBlock<Block> TIN_ORE = registerBlock("tin_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(3f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 6),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .requiresCorrectToolForDrops()
                            .strength(4.5f)
                            .sound(SoundType.DEEPSLATE)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        MinecivilizeModItems.REGISTRY.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
} 