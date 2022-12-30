package com.rena.lost.client.render.entities;

import com.rena.lost.client.model.entity.DakosaurusModel;
import com.rena.lost.common.entity.aquatic.DakosaurusEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DakosaurusRender extends GeoEntityRenderer<DakosaurusEntity> {
    public DakosaurusRender(EntityRendererManager renderManager) {
        super(renderManager, new DakosaurusModel());
    }

    @Override
    public ResourceLocation getEntityTexture(DakosaurusEntity entity) {
        return getGeoModelProvider().getTextureLocation(entity);
    }
}
