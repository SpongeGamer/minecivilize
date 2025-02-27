package net.sponge.minecivilize.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sponge.minecivilize.MinecivilizeMod;
import net.sponge.minecivilize.init.MinecivilizeModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MinecivilizeMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Добавляем теги для олова
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(MinecivilizeModBlocks.TIN_ORE.get())
                .add(MinecivilizeModBlocks.DEEPSLATE_TIN_ORE.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(MinecivilizeModBlocks.TIN_ORE.get())
                .add(MinecivilizeModBlocks.DEEPSLATE_TIN_ORE.get());
    }
} 