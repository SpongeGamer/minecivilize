package net.sponge.minecivilize.init;

import net.sponge.minecivilize.MinecivilizeMod;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class MinecivilizeModFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, MinecivilizeMod.MODID);
} 