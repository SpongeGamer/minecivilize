package net.sponge.minecivilize.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.sponge.minecivilize.MinecivilizeMod;
import net.sponge.minecivilize.worldgen.ModBiomeModifiers;
import net.sponge.minecivilize.worldgen.ModConfiguredFeatures;
import net.sponge.minecivilize.worldgen.ModPlacedFeatures;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@EventBusSubscriber(modid = MinecivilizeMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModDatapackProvider(packOutput, lookupProvider));
        
        ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        
        Function<HolderLookup.Provider, LootTableSubProvider> subProvider = provider -> new ModBlockLootTableProvider(provider);
        Set<ResourceKey<LootTable>> emptySet = Collections.emptySet();
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, emptySet,
                List.of(new LootTableProvider.SubProviderEntry(subProvider, LootContextParamSets.BLOCK)), lookupProvider));
    }
}