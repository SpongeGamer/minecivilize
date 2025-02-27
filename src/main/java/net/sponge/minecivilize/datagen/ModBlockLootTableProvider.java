package net.sponge.minecivilize.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sponge.minecivilize.init.MinecivilizeModBlocks;
import net.sponge.minecivilize.worldgen.ModPlacedFeatures;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(net.minecraft.core.HolderLookup.Provider provider) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        // Не добавляй здесь дроп для tin_ore и deepslate_tin_ore, если у них есть JSON
        // Пример для других блоков, если нужно:
        // dropSelf(MinecivilizeModBlocks.OTHER_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return MinecivilizeModBlocks.BLOCKS.getEntries().stream()
                .map(entry -> entry.get())
                .collect(Collectors.toList());
    }
}