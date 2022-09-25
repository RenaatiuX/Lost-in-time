package com.rena.lost.client.model.entity;

import com.rena.lost.LostInTime;
import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ApertotemporalisModel extends AnimatedGeoModel<ApertotemporalisEntity> {
    @Override
    public ResourceLocation getModelLocation(ApertotemporalisEntity object) {
        return new ResourceLocation(LostInTime.MOD_ID, "geo/apertotemporalis.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ApertotemporalisEntity object) {
        return new ResourceLocation(LostInTime.MOD_ID, "textures/entity/apertotemporalis.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ApertotemporalisEntity animatable) {
        return new ResourceLocation(LostInTime.MOD_ID, "animations/apertotemporalis.animation.json");
    }
}
