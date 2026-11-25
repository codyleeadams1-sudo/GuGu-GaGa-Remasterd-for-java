package com.gugugaga.client;

import com.gugugaga.GuguMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class GuguClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(GuguModel.LAYER, GuguModel::createBodyLayer);
        EntityRenderers.register(GuguMod.GUGU, GuguRenderer::new);
    }
}
