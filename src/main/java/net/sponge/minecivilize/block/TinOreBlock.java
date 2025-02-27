package net.sponge.minecivilize.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class TinOreBlock extends DropExperienceBlock {
    private static final Logger LOGGER = LogManager.getLogger();

    public TinOreBlock() {
        super(UniformInt.of(3, 6), BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .requiresCorrectToolForDrops()
            .strength(3.0f)
            .sound(SoundType.STONE));
    }

    @Override
    public void playerDestroy(Level level, Player player, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (tool.getItem() instanceof DiggerItem diggerItem) {
            LOGGER.info("Tool tier: " + diggerItem.getTier());
        } else {
            LOGGER.info("Tool tier: Not a digger item");
        }
        LOGGER.info("Has Silk Touch: " + (EnchantmentHelper.getItemEnchantmentLevel(
            level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SILK_TOUCH), tool) > 0));
        LOGGER.info("Fortune level: " + EnchantmentHelper.getItemEnchantmentLevel(
            level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE), tool));
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
}