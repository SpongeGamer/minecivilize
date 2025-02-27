package net.sponge.minecivilize.client.renderer;

import net.sponge.minecivilize.entity.BuilderBasicEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.LevelRenderer;

public class BuilderBasicRenderer extends HumanoidMobRenderer<BuilderBasicEntity, HumanoidModel<BuilderBasicEntity>> {
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("minecivilize:textures/entities/builder.png");

	public BuilderBasicRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(BuilderBasicEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(BuilderBasicEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, 
					  MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		
		// Рендерим текущую цель, если она есть
		if (entity.getCurrentState() == BuilderBasicEntity.State.SEARCHING || entity.getCurrentState() == BuilderBasicEntity.State.CHOPPING) {
			BlockPos targetTree = entity.getTargetTree();
			if (targetTree != null) {
				AABB box = new AABB(targetTree);
				LevelRenderer.renderLineBox(poseStack, buffer.getBuffer(RenderType.LINES), box, 0.0f, 1.0f, 0.0f, 1.0f);
			}
		}
	}
}
