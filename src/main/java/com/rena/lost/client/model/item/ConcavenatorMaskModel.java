package com.rena.lost.client.model.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class ConcavenatorMaskModel extends BipedModel<LivingEntity> {

    private final EquipmentSlotType slot;

    public ConcavenatorMaskModel(float modelSize, EquipmentSlotType slot) {
        super(modelSize, 0.0F, 128, 64);
        this.slot = slot;

        bipedHead.setTextureOffset(94, 13).addBox(-7.0F, -1.0F, -5.5F, 14.0F, 3.0F, 3.0F, 0.0F, false);
        bipedHead.setTextureOffset(100, 35).addBox(-6.0F, -9.0F, -5.0F, 12.0F, 5.0F, 2.0F, 0.0F, false);
        bipedHead.setTextureOffset(90, 56).addBox(-2.0F, -15.0F, -5.0F, 4.0F, 6.0F, 2.0F, 0.0F, false);
        bipedHead.setTextureOffset(100, 26).addBox(-7.0F, -14.0F, -4.0F, 14.0F, 6.0F, 0.0F, 0.0F, false);
        bipedHead.setTextureOffset(104, 59).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 3.0F, 2.0F, 0.05F, false);
        bipedHead.setTextureOffset(100, 49).addBox(-7.0F, -4.0F, -4.0F, 14.0F, 3.0F, 0.0F, 0.0F, false);

    }



    @Override
    public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        if (entityIn instanceof ArmorStandEntity) {
            ArmorStandEntity entityarmorstand = (ArmorStandEntity) entityIn;
            this.bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            this.bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            this.bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            this.bipedHeadwear.copyModelAngles(this.bipedHead);
        } else
            super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
