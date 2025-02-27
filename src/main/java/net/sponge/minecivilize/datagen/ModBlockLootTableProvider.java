package net.sponge.minecivilize.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sponge.minecivilize.init.MinecivilizeModBlocks;
import net.sponge.minecivilize.init.MinecivilizeModItems;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Руды дропают сырое олово
        add(MinecivilizeModBlocks.TIN_ORE.get(),
                block -> createMultipleOreDrops(MinecivilizeModBlocks.TIN_ORE.get(), MinecivilizeModItems.RAW_TIN.get(), 1, 3));
        add(MinecivilizeModBlocks.DEEPSLATE_TIN_ORE.get(),
                block -> createMultipleOreDrops(MinecivilizeModBlocks.DEEPSLATE_TIN_ORE.get(), MinecivilizeModItems.RAW_TIN.get(), 2, 5));

        // Столы просто дропают себя
        dropSelf(MinecivilizeModBlocks.ROME_TABLE_BLOCK.get());
        dropSelf(MinecivilizeModBlocks.GERMANY_TABLE_BLOCK.get());
        dropSelf(MinecivilizeModBlocks.RUSSIAN_TABLE_BLOCK.get());
    }

    protected LootTable.Builder createMultipleOreDrops(Block block, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return MinecivilizeModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

    public static ModBlockLootTableProvider create(HolderLookup.Provider lookupProvider) {
        return new ModBlockLootTableProvider(lookupProvider);
    }
} 