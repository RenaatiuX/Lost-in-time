package com.rena.lost.client.model.entity;

import com.rena.lost.LostInTime;
import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class ApertotemporalisModel extends AnimatedTickingGeoModel<ApertotemporalisEntity> {
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

    @Override
    public void setLivingAnimations(ApertotemporalisEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("Head");
        if (head != null) {
            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
