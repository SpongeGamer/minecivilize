package net.sponge.minecivilize.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.BuiltInRegistries;

public class DeepslateTinOreBlock extends Block {
    private static final Logger LOGGER = LogManager.getLogger();

    public DeepslateTinOreBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DEEPSLATE)
            .requiresCorrectToolForDrops()
            .strength(4.5f)
            .sound(SoundType.DEEPSLATE));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, 
                            net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        LOGGER.info("DeepslateTinOre destroyed at " + pos);
        LOGGER.info("Tool used: " + tool);
        LOGGER.info("Tool tier: " + tool.getTier());
        LOGGER.info("Has Silk Touch: " + EnchantmentHelper.hasSilkTouch(tool));
        LOGGER.info("Fortune level: " + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FORTUNE, tool));
        
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
} 