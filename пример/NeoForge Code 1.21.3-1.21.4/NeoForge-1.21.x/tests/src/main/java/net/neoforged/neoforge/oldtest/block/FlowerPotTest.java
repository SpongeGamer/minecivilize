/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.oldtest.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(FlowerPotTest.MODID)
@EventBusSubscriber(modid = FlowerPotTest.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FlowerPotTest {
    static final String MODID = "flower_pot_test";
    static final String BLOCK_ID = "test_flower_pot";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredBlock<FlowerPotBlock> EMPTY_FLOWER_POT = BLOCKS.registerBlock(BLOCK_ID, props -> new FlowerPotBlock(null, () -> Blocks.AIR, props), Block.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> OAK_FLOWER_POT = BLOCKS.registerBlock(BLOCK_ID + "_oak", props -> new FlowerPotBlock(EMPTY_FLOWER_POT, () -> Blocks.OAK_SAPLING, props), Block.Properties.ofFullCopy(Blocks.FLOWER_POT));

    static {
        ITEMS.register(BLOCK_ID, () -> new BlockItem(EMPTY_FLOWER_POT.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, EMPTY_FLOWER_POT.getId())).useBlockDescriptionPrefix()));
    }

    @SubscribeEvent
    public static void onItemRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            EMPTY_FLOWER_POT.get().addPlant(BuiltInRegistries.BLOCK.getKey(Blocks.OAK_SAPLING), OAK_FLOWER_POT);
        }
    }

    public FlowerPotTest(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }
}
