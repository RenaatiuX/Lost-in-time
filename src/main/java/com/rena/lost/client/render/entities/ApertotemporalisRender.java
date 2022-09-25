package com.rena.lost.client.render.entities;

import com.rena.lost.client.model.entity.ApertotemporalisModel;
import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ApertotemporalisRender extends GeoEntityRenderer<ApertotemporalisEntity> {

    public ApertotemporalisRender(EntityRendererManager renderManager) {
        super(renderManager, new ApertotemporalisModel());
    }

    @Override
    public ResourceLocation getEntityTexture(ApertotemporalisEntity entity) {
        return getGeoModelProvider().getTextureLocation(entity);
    }
}
