package net.sponge.minecivilize.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.sponge.minecivilize.entity.BuilderBasicEntity;
import net.minecraft.world.entity.Entity;

public class HarvestTreeProcedure {
    public static void execute(Level world, int x, int y, int z, Entity entity) {
        // Логика поиска ближайшего дерева
        BlockPos treePos = findNearestTree(world, x, y, z);
        if (treePos != null) {
            // Перемещение NPC к дереву
            if (entity instanceof BuilderBasicEntity builderEntity) {
                moveToTree(builderEntity, treePos);
                // Имитация добычи дерева
                harvestTree(world, treePos, builderEntity);
            }
        }
    }

    private static BlockPos findNearestTree(Level world, int x, int y, int z) {
        // Простой поиск ближайшего блока дерева в радиусе 5 блоков
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                    if (world.getBlockState(pos).getBlock() == Blocks.OAK_LOG) {
                        return pos;
                    }
                }
            }
        }
        return null; // Если дерево не найдено
    }

    private static void moveToTree(BuilderBasicEntity entity, BlockPos treePos) {
        // Перемещение NPC к дереву (упрощенная логика)
        entity.moveTo(treePos.getX(), treePos.getY(), treePos.getZ());
    }

    private static void harvestTree(Level world, BlockPos treePos, BuilderBasicEntity entity) {
        // Добыча дерева: удаление блока и добавление в инвентарь моба
        world.removeBlock(treePos, false);
        entity.getCombinedInventory().insertItem(0, new ItemStack(Blocks.OAK_LOG), false);
    }
}