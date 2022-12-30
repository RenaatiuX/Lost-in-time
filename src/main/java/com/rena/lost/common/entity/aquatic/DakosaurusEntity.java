package com.rena.lost.common.entity.aquatic;

import com.rena.lost.common.entity.ia.AnimalSwimMoveControllerSink;
import com.rena.lost.core.init.ItemInit;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class DakosaurusEntity extends WaterMobEntity implements IAnimatable, IAnimationTickable {
    private static final DataParameter<Boolean> BEACHED = EntityDataManager.createKey(DakosaurusEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DESPAWN_BEACH = EntityDataManager.createKey(DakosaurusEntity.class, DataSerializers.BOOLEAN);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int despawnDelay = 47999;

    public DakosaurusEntity(EntityType<DakosaurusEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.moveController = new AnimalSwimMoveControllerSink(this, 1, 1, 3);
        this.lookController = new DolphinLookController(this, 4);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new SwimmerPathNavigator(this, worldIn);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isDespawnBeach();
    }

    private boolean canDespawn() {
        return isDespawnBeach();
    }

    private void tryDespawn() {
        if (this.canDespawn()) {
            this.despawnDelay = this.despawnDelay - 1;
            if (this.despawnDelay <= 0) {
                this.clearLeashed(true, false);
                this.remove();
            }
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FindWaterGoal(this));
        //this.goalSelector.addGoal(1, new DakosaurusMeleeJumpGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.9F, 10) {
            @Override
            public boolean shouldExecute() {
                return !DakosaurusEntity.this.isBeached() && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this) {
            @Override
            public boolean shouldExecute() {
                return !DakosaurusEntity.this.isBeached() && !DakosaurusEntity.this.isOnGround() && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1F, true){
            @Override
            public boolean shouldExecute() {
                return !DakosaurusEntity.this.isBeached() && !DakosaurusEntity.this.isOnGround() && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        /*this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, true){
            @Override
            public boolean shouldExecute() {
                return !DakosaurusEntity.this.isBeached() && !DakosaurusEntity.this.isOnGround() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractFishEntity.class, true));*/
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 70.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Beached", this.isBeached());
        compound.putBoolean("BeachedDespawnFlag", this.isDespawnBeach());
        compound.putInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setBeached(compound.getBoolean("Beached"));
        this.setDespawnBeach(compound.getBoolean("BeachedDespawnFlag"));
        if (compound.contains("DespawnDelay", 99)) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BEACHED, Boolean.FALSE);
        this.dataManager.register(DESPAWN_BEACH, Boolean.FALSE);
    }

    public boolean isBeached() {
        return this.dataManager.get(BEACHED);
    }

    public void setBeached(boolean charging) {
        this.dataManager.set(BEACHED, charging);
    }

    public boolean isDespawnBeach() {
        return this.dataManager.get(DESPAWN_BEACH);
    }

    public void setDespawnBeach(boolean despawn) {
        this.dataManager.set(DESPAWN_BEACH, despawn);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity prey = this.getAttackTarget();
        if (prey != null) {
            double dist = this.getDistance(prey);
            this.attackEntityAsMob(this.getAttackTarget());
            if (prey instanceof ParrotEntity) {
                this.faceEntity(this.getAttackTarget(), 30, 30);
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                    /*float f2 = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
                    this.rotationPitch = f2;*/
            }

            if (dist < 2D) {
                this.attackEntityAsMob(prey);
                if (this.rand.nextFloat() < 0.3F) {
                    this.entityDropItem(new ItemStack(ItemInit.DAKOSAURUS_TOOTH.get()));
                }
            }
        }
        this.setDespawnBeach(false);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        float rPitch = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        this.rotationPitch = MathHelper.clamp(rPitch, -90, 90);
        if (this.isOnGround() && !this.isInWaterOrBubbleColumn()) {
            this.setBeached(true);
            this.rotationPitch = 0;
        }
        if (this.isBeached()) {
            this.setMotion(this.getMotion().mul(0.5, 0.5, 0.5));
            if (this.areEyesInFluid(FluidTags.WATER)) {
                this.despawnDelay = 47999;
                this.setBeached(false);
            }
        }
        if (!this.world.isRemote) {
            this.tryDespawn();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (this.isInWaterOrBubbleColumn()) {
            if (event.isMoving()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dakosaurus.swim", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
        } else if (this.isBeached()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dakosaurus.beached", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {

        if (this.isSwingInProgress && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dakosaurus.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            this.isSwingInProgress = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 30, this::predicate));
        data.addAnimationController(new AnimationController<>(this, "attack_controller", 0, this::attackPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public int tickTimer() {
        return this.ticksExisted;
    }
}
