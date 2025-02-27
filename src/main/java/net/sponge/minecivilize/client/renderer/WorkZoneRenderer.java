package net.sponge.minecivilize.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class WorkZoneRenderer {
    public static void renderWorkZone(PoseStack poseStack, BlockPos start, BlockPos end, float partialTick) {
        if (start == null || end == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();

        int minX = Math.min(start.getX(), end.getX());
        int minY = Math.min(start.getY(), end.getY());
        int minZ = Math.min(start.getZ(), end.getZ());
        int maxX = Math.max(start.getX(), end.getX()) + 1;
        int maxY = Math.max(start.getY(), end.getY()) + 1;
        int maxZ = Math.max(start.getZ(), end.getZ()) + 1;

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        
        VertexConsumer builder = minecraft.renderBuffers().bufferSource()
            .getBuffer(RenderType.lines());

        // Рендерим границы зоны
        LevelRenderer.renderLineBox(poseStack, builder, box, 0.9f, 0.9f, 0.2f, 1.0f);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        
        poseStack.popPose();
    }
} 