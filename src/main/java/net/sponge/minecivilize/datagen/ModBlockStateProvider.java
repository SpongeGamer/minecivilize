package net.sponge.minecivilize.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sponge.minecivilize.MinecivilizeMod;
import net.sponge.minecivilize.init.MinecivilizeModBlocks;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MinecivilizeMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(MinecivilizeModBlocks.TIN_ORE);
        blockWithItem(MinecivilizeModBlocks.DEEPSLATE_TIN_ORE);
        blockWithItem(MinecivilizeModBlocks.ROME_TABLE_BLOCK);
        blockWithItem(MinecivilizeModBlocks.GERMANY_TABLE_BLOCK);
        blockWithItem(MinecivilizeModBlocks.RUSSIAN_TABLE_BLOCK);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
} 