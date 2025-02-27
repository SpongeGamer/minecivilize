package net.sponge.minecivilize.init;

import net.sponge.minecivilize.MinecivilizeMod;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;

public class MinecivilizeModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MinecivilizeMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MinecivilizeMod.MODID);

    static {
        RECIPE_SERIALIZERS.register("crafting_shaped", () -> RecipeSerializer.SHAPED_RECIPE);
        RECIPE_SERIALIZERS.register("crafting_shapeless", () -> RecipeSerializer.SHAPELESS_RECIPE);
    }
} 