package com.rena.lost.common.entity.flying;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

import javax.annotation.Nullable;
import java.util.List;

public class MirarceEntity extends AnimalEntity implements IFlyingAnimal, IAnimatable, IAnimationTickable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int panicTicks = 0;
    public float wingRotation;
    public float destPos;
    public float oFlapSpeed;
    public float oFlap;
    public float wingRotDelta = 1.0F;
    public float nextFlap = 1.0F;

    public MirarceEntity(EntityType<MirarceEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 14.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, false, Ingredient.fromItems(Items.KELP)));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, MobEntity.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) {
        return null;
    }

    @Override
    protected void updateAITasks() {
        if (this.getMoveHelper().isUpdating()) {
            this.setSprinting(this.getMoveHelper().getSpeed() >= 1.5D);
        } else {
            this.setSprinting(false);
        }
        super.updateAITasks();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean lastHurt = super.attackEntityFrom(source, amount);
        if (lastHurt) {
            int ticks = 100 + this.rand.nextInt(100);
            this.panicTicks = ticks;
            List<? extends MirarceEntity> deers = this.world.getEntitiesWithinAABB(MirarceEntity.class, this.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
            for (MirarceEntity deer : deers) {
                deer.panicTicks = ticks;
            }
        }
        return lastHurt;
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator navigation = new FlyingPathNavigator(this, worldIn);
        navigation.setCanOpenDoors(false);
        navigation.setCanSwim(true);
        navigation.setCanEnterDoors(true);
        return navigation;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            if (panicTicks >= 0) {
                panicTicks--;
            }
            if (panicTicks == 0 && this.getRevengeTarget() != null) {
                this.setRevengeTarget(null);
            }
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos += (float)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta *= 0.9F;
        Vector3d vec3 = this.getMotion();
        if (!this.onGround && vec3.y < 0.0D) {
            this.setMotion(vec3.mul(1.0D, 0.6D, 1.0D));
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    public boolean isFlying() {
        return !this.onGround;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (horizontalMag(this.getMotion()) > 1.0E-6) {
            if (this.isSprinting()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("deer.run", ILoopType.EDefaultLoopTypes.LOOP));
                event.getController().setAnimationSpeed(1.5D);
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("deer.walk", ILoopType.EDefaultLoopTypes.LOOP));
            }
        } else {
            if (this.isFlying()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.setResetSpeedInTicks(10);
        data.addAnimationController(new AnimationController<>(this, "controller", 10, this::predicate));
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
