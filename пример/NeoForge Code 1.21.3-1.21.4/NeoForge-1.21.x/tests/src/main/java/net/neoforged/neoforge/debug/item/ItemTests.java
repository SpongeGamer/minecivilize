/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.debug.item;

import java.util.EnumMap;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;
import net.neoforged.testframework.registration.RegistrationHelper;

@ForEachTest(groups = ItemTests.GROUP)
public class ItemTests {
    public static final String GROUP = "level.item";

    @GameTest
    @TestHolder(description = {
            "Tests if custom mob buckets work"
    })
    static void customMobBucket(final DynamicTest test, final RegistrationHelper reg) {
        final var cowBucket = reg.items().registerItem("cow_bucket", props -> new MobBucketItem(
                EntityType.COW,
                Fluids.WATER,
                SoundEvents.BUCKET_EMPTY_FISH,
                props.stacksTo(1)))
                .withLang("Cow bucket");
        test.framework().modEventBus().addListener((final FMLCommonSetupEvent event) -> {
            DispenserBlock.registerBehavior(cowBucket, new DefaultDispenseItemBehavior() {
                private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

                @Override
                public ItemStack execute(BlockSource p_302435_, ItemStack p_123562_) {
                    DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) p_123562_.getItem();
                    BlockPos blockpos = p_302435_.pos().relative(p_302435_.state().getValue(DispenserBlock.FACING));
                    Level level = p_302435_.level();
                    if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, p_123562_)) {
                        dispensiblecontaineritem.checkExtraContent(null, level, p_123562_, blockpos);
                        return new ItemStack(Items.BUCKET);
                    } else {
                        return this.defaultDispenseItemBehavior.dispense(p_302435_, p_123562_);
                    }
                }
            });
        });

        test.registerGameTestTemplate(StructureTemplateBuilder.withSize(3, 4, 3)
                .placeWaterConfinement(1, 1, 1, Blocks.IRON_BLOCK.defaultBlockState(), true)
                .placeWaterConfinement(1, 2, 1, Blocks.GOLD_BLOCK.defaultBlockState(), false));

        test.onGameTest(helper -> helper.startSequence()
                .thenExecute(() -> helper.setBlock(1, 1, 1, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.UP)))
                .thenExecute(() -> ((DispenserBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1))).setItem(0, cowBucket.get().getDefaultInstance()))
                .thenExecute(() -> helper.pulseRedstone(new BlockPos(1, 1, 2), 3))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockPresent(Blocks.WATER, new BlockPos(1, 2, 1)))
                .thenExecute(() -> helper.assertEntityPresent(EntityType.COW, 1, 3, 1))
                .thenExecute(() -> helper.killAllEntitiesOfClass(Cow.class))
                .thenSucceed());
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = {
            "Tests if the forge spawn egg works"
    })
    static void forgeSpawnEggTest(final DynamicTest test, final RegistrationHelper reg) {
        final var testEntity = reg.entityTypes().registerEntityType("test_entity", Pig::new, MobCategory.CREATURE, builder -> builder.sized(1, 1))
                .withAttributes(Pig::createAttributes)
                .withRenderer(() -> PigRenderer::new)
                .withLang("Test Pig spawn egg");

        final var egg = reg.items().registerItem("test_spawn_egg", props -> new SpawnEggItem(testEntity.get(), props) {
            @Override
            public InteractionResult useOn(UseOnContext ctx) {
                final var result = super.useOn(ctx);
                if (result.consumesAction()) {
                    test.pass();
                }
                return result;
            }

            @Override
            public InteractionResult use(Level level, Player player, InteractionHand hand) {
                final var sup = super.use(level, player, hand);
                if (sup.consumesAction()) {
                    test.pass();
                }
                return sup;
            }
        })
                .tab(CreativeModeTabs.SPAWN_EGGS);

        reg.addClientProvider(event -> event.addProvider(new ModelProvider(event.getGenerator().getPackOutput(), reg.modId()) {
            @Override
            protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
                itemModels.generateSpawnEgg(egg.value(), 0xFFFFFF, 0xFFFFFF);
            }

            @Override
            protected Stream<? extends Holder<Item>> getKnownItems() {
                return Stream.of(egg);
            }

            @Override
            protected Stream<? extends Holder<Block>> getKnownBlocks() {
                return Stream.empty();
            }

            @Override
            public String getName() {
                return "forge_spawn_egg_test_model_generator";
            }
        }));

        test.onGameTest(helper -> helper.startSequence()
                .thenExecute(() -> helper.setBlock(1, 1, 1, Blocks.IRON_BLOCK))
                .thenExecute(() -> helper.useBlock(new BlockPos(1, 1, 1), helper.makeMockPlayer(), egg.get().getDefaultInstance(), Direction.UP))
                .thenExecuteAfter(10, () -> helper.assertEntityPresent(testEntity.get(), 1, 2, 1))
                .thenExecute(() -> helper.killAllEntitiesOfClass(Pig.class))
                .thenSucceed());
    }

    @SuppressWarnings("unused") // Referenced by enumextender.json
    public static Object getRarityEnumParameter(int idx, Class<?> type) {
        return type.cast(switch (idx) {
            case 0 -> -1;
            case 1 -> "neotests:custom";
            case 2 -> (UnaryOperator<Style>) style -> style.withItalic(true).withColor(ChatFormatting.DARK_AQUA);
            default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
        });
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests if custom rarities (with custom styles) work on items")
    static void itemCustomRarity(final DynamicTest test, final RegistrationHelper reg) {
        final Rarity rarity = Rarity.valueOf("NEOTESTS_CUSTOM");
        final Supplier<Item> item = reg.items().registerSimpleItem("test", new Item.Properties().rarity(rarity))
                .withLang("Custom rarity test");

        test.onGameTest(helper -> helper.startSequence(() -> item.get().getDefaultInstance())
                .thenMap(stack -> stack.getDisplayName().getStyle())
                .thenExecute(style -> helper.assertTrue(
                        style.isItalic(), "custom rarity did not make component italic"))
                .thenExecute(style -> helper.assertTrue(
                        style.getColor().getValue() == ChatFormatting.DARK_AQUA.getColor(), "custom rarity did not make component italic"))
                .thenSucceed());
    }

    @GameTest
    @TestHolder(description = "Adds snow boots that allow a player to walk on powdered snow as long as they have less than half health")
    static void snowBoots(final DynamicTest test, final RegistrationHelper reg) {
        test.registerGameTestTemplate(StructureTemplateBuilder.withSize(3, 5, 3)
                .fill(0, 0, 0, 2, 0, 2, Blocks.IRON_BLOCK)
                .fill(0, 1, 0, 2, 1, 2, Blocks.POWDER_SNOW));

        final var snowBoots = reg.items().registerItem("snow_boots", props -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.BOOTS, props) {
            @Override
            public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
                return wearer.getHealth() < wearer.getMaxHealth() / 2;
            }
        }).withLang("Snow Boots").tab(CreativeModeTabs.TOOLS_AND_UTILITIES);

        test.onGameTest(helper -> helper.startSequence(() -> helper.spawnWithNoFreeWill(EntityType.PIG, 1, 2, 1))
                .thenExecute(pig -> pig.setItemSlot(EquipmentSlot.FEET, snowBoots.get().getDefaultInstance()))
                .thenExecute(pig -> pig.setHealth(pig.getMaxHealth() / 2 - 1))
                // Pig shouldn't have fallen
                .thenExecuteAfter(20, () -> helper.assertEntityPresent(EntityType.PIG, 1, 2, 1))

                // Back to max health so falling time
                .thenExecute(pig -> pig.setHealth(pig.getMaxHealth()))
                .thenWaitUntil(() -> helper.assertEntityPresent(EntityType.PIG, 1, 1, 1))
                .thenSucceed());
    }

    private static final ArmorMaterial NEO_MATERIAL = new ArmorMaterial(5, Util.make(new EnumMap<>(ArmorType.class), map -> {
        map.put(ArmorType.BOOTS, 1);
        map.put(ArmorType.LEGGINGS, 2);
        map.put(ArmorType.CHESTPLATE, 3);
        map.put(ArmorType.HELMET, 1);
        map.put(ArmorType.BODY, 3);
    }), 15, SoundEvents.AMBIENT_CAVE, 0.0F, 0.0F, Tags.Items.BARRELS, EquipmentAssets.LEATHER);

    @TestHolder(description = "Tests that registries can correctly handle named holder set references.")
    static void toolItem(final DynamicTest test, final RegistrationHelper reg) {
        var material = new ToolMaterial(BlockTags.BAMBOO_BLOCKS, 160, 5.0F, 0.5F, 10, ItemTags.BEDS);
        reg.items().registerItem("neo_pickaxe", properties -> new PickaxeItem(material, 1.0F, -2.8F, properties));
        // This is invalid registration, but replicates an error suppression issue found in RegistryManager#applySnapshot
//        reg.items().register("neo_helmet", () -> new ArmorItem(NEO_MATERIAL, ArmorType.HELMET, new Item.Properties()));
    }
}
