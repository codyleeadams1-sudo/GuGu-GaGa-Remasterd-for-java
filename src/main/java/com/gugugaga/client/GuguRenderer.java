package com.gugugaga.client;

import com.gugugaga.GuguEntity;
import com.gugugaga.GuguMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class GuguRenderer extends MobRenderer<GuguEntity, LivingEntityRenderState, GuguModel> {
    private static final Identifier TEXTURE = GuguMod.id("textures/entity/gugugaga.png");

    public GuguRenderer(EntityRendererProvider.Context context) {
        super(context, new GuguModel(context.bakeLayer(GuguModel.LAYER)), 0.5f);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
