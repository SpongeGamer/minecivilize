package net.sponge.minecivilize.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sponge.minecivilize.MinecivilizeMod;

import java.util.EnumMap;
import java.util.List;

public class BronzeArmorMaterial {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = 
        DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, MinecivilizeMod.MODID);

    private static final EnumMap<ArmorItem.Type, Integer> PROTECTION_VALUES = 
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 5);
            map.put(ArmorItem.Type.CHESTPLATE, 6);
            map.put(ArmorItem.Type.HELMET, 2);
            map.put(ArmorItem.Type.BODY, 4);
        });

    public static final ResourceLocation BRONZE_LOCATION = ResourceLocation.tryParse(MinecivilizeMod.MODID + ":bronze");

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BRONZE = ARMOR_MATERIALS.register("bronze", 
        () -> new ArmorMaterial(
            PROTECTION_VALUES,
            12, // enchantability
            SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(Items.COPPER_INGOT),
            List.of(new ArmorMaterial.Layer(BRONZE_LOCATION)),
            0.0F, // toughness
            0.0F  // knockback resistance
        ));
}