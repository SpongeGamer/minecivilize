
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.sponge.minecivilize.init;

import net.sponge.minecivilize.client.renderer.BuilderBasicRenderer;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MinecivilizeModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MinecivilizeModEntities.BUILDER_BASIC.get(), BuilderBasicRenderer::new);
	}
}
