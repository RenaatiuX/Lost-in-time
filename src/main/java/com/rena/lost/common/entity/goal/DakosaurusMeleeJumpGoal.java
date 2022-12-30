package com.rena.lost.common.entity.goal;

import com.rena.lost.common.entity.aquatic.DakosaurusEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.JumpGoal;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class DakosaurusMeleeJumpGoal extends JumpGoal {

    private final DakosaurusEntity dakosaurus;
    private int attackCooldown = 0;
    private boolean inWater;

    public DakosaurusMeleeJumpGoal(DakosaurusEntity dakosaurus) {
        this.dakosaurus = dakosaurus;
    }

    @Override
    public boolean shouldExecute() {
        if (this.dakosaurus.getAttackTarget() == null || dakosaurus.isInWater() || !dakosaurus.isBeached() || !dakosaurus.isOnGround()){
            return false;
        } else {
            BlockPos blockpos = this.dakosaurus.getPosition();
            return true;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        double d0 = this.dakosaurus.getMotion().y;
        return dakosaurus.getAttackTarget() != null && (!(d0 * d0 < (double) 0.03F) || this.dakosaurus.rotationPitch == 0.0F || !(Math.abs(this.dakosaurus.rotationPitch) < 10.0F) || !this.dakosaurus.isInWater()) && !this.dakosaurus.isOnGround();
    }

    @Override
    public boolean isPreemptible() {
        return false;
    }

    @Override
    public void startExecuting() {
        LivingEntity target = this.dakosaurus.getAttackTarget();
        if(target != null){
            double distanceXZ = dakosaurus.getDistanceSq(target.getPosX(), dakosaurus.getPosY(), target.getPosZ());
            if(distanceXZ < 150){
                dakosaurus.faceEntity(target, 260, 30);
                double smoothX = MathHelper.clamp(Math.abs(target.getPosX() - dakosaurus.getPosX()), 0, 1);
                double smoothY = MathHelper.clamp(Math.abs(target.getPosY() - dakosaurus.getPosY()), 0, 1);
                double smoothZ = MathHelper.clamp(Math.abs(target.getPosZ() - dakosaurus.getPosZ()), 0, 1);
                double d0 = (target.getPosX() - this.dakosaurus.getPosX()) * 0.3 * smoothX;
                double d1 = Math.signum(target.getPosY() - this.dakosaurus.getPosY());
                double d2 = (target.getPosZ() - this.dakosaurus.getPosZ()) * 0.3 * smoothZ;
                float up = 1F + dakosaurus.getRNG().nextFloat() * 0.8F;
                this.dakosaurus.setMotion(this.dakosaurus.getMotion().add(d0 * 0.3D, up, d2 * 0.3D));
                this.dakosaurus.getNavigator().clearPath();
            }else{
                dakosaurus.getNavigator().tryMoveToEntityLiving(target, 1.0F);
            }
        }
    }

    @Override
    public void resetTask() {
        this.dakosaurus.rotationPitch = 0.0F;
        this.attackCooldown = 0;
    }

    @Override
    public void tick() {
        boolean flag = this.inWater;
        if (!flag) {
            FluidState fluidstate = this.dakosaurus.world.getFluidState(this.dakosaurus.getPosition());
            this.inWater = fluidstate.isTagged(FluidTags.WATER);
        }
        if(attackCooldown > 0){
            attackCooldown--;
        }
        if (this.inWater && !flag) {
            this.dakosaurus.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        }
        LivingEntity target = this.dakosaurus.getAttackTarget();
        if(target != null){
            if(this.dakosaurus.getDistance(target) < 3F && attackCooldown <= 0){
                this.dakosaurus.attackEntityAsMob(target);
                attackCooldown = 20;
            }
        }

        Vector3d vector3d = this.dakosaurus.getMotion();
        if (vector3d.y * vector3d.y < (double) 0.1F && this.dakosaurus.rotationPitch != 0.0F) {
            this.dakosaurus.rotationPitch = MathHelper.rotLerp(this.dakosaurus.rotationPitch, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(Entity.horizontalMag(vector3d));
            double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double) (180F / (float) Math.PI);
            this.dakosaurus.rotationPitch = (float) d1;
        }
    }
}
