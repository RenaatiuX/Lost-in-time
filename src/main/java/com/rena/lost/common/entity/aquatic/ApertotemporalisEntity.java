package com.rena.lost.common.entity.aquatic;

import com.google.common.collect.Sets;
import com.rena.lost.common.block.ApertotemporalisEggBlock;
import com.rena.lost.core.EntityInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class ApertotemporalisEntity extends AnimalEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_DIGGING = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);

    private int isDigging;
    public static final Predicate<LivingEntity> TARGET_DRY_BABY = (entity) ->
            entity.isChild() && !entity.isInWater();

    public ApertotemporalisEntity(EntityType<ApertotemporalisEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        this.moveController = new ApertotemporalisEntity.MoveHelperController(this);
        this.stepHeight = 1.0F;
    }

    public void setHome(BlockPos position) {
        this.dataManager.set(HOME_POS, position);
    }

    private BlockPos getHome() {
        return this.dataManager.get(HOME_POS);
    }

    private void setTravelPos(BlockPos position) {
        this.dataManager.set(TRAVEL_POS, position);
    }

    private BlockPos getTravelPos() {
        return this.dataManager.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return this.dataManager.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.dataManager.set(HAS_EGG, hasEgg);
    }

    public boolean isDigging() {
        return this.dataManager.get(IS_DIGGING);
    }

    private void setDigging(boolean isDigging) {
        this.isDigging = isDigging ? 1 : 0;
        this.dataManager.set(IS_DIGGING, isDigging);
    }

    private boolean isGoingHome() {
        return this.dataManager.get(GOING_HOME);
    }

    private void setGoingHome(boolean isGoingHome) {
        this.dataManager.set(GOING_HOME, isGoingHome);
    }

    private boolean isTravelling() {
        return this.dataManager.get(TRAVELLING);
    }

    private void setTravelling(boolean isTravelling) {
        this.dataManager.set(TRAVELLING, isTravelling);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HOME_POS, BlockPos.ZERO);
        this.dataManager.register(HAS_EGG, false);
        this.dataManager.register(TRAVEL_POS, BlockPos.ZERO);
        this.dataManager.register(GOING_HOME, false);
        this.dataManager.register(TRAVELLING, false);
        this.dataManager.register(IS_DIGGING, false);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("HomePosX", this.getHome().getX());
        compound.putInt("HomePosY", this.getHome().getY());
        compound.putInt("HomePosZ", this.getHome().getZ());
        compound.putBoolean("HasEgg", this.hasEgg());
        compound.putInt("TravelPosX", this.getTravelPos().getX());
        compound.putInt("TravelPosY", this.getTravelPos().getY());
        compound.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        int i = compound.getInt("HomePosX");
        int j = compound.getInt("HomePosY");
        int k = compound.getInt("HomePosZ");
        this.setHome(new BlockPos(i, j, k));
        super.readAdditional(compound);
        this.setHasEgg(compound.getBoolean("HasEgg"));
        int l = compound.getInt("TravelPosX");
        int i1 = compound.getInt("TravelPosY");
        int j1 = compound.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(l, i1, j1));
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setHome(this.getPosition());
        this.setTravelPos(BlockPos.ZERO);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ApertotemporalisEntity.PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new ApertotemporalisEntity.MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new ApertotemporalisEntity.LayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new ApertotemporalisEntity.PlayerTemptGoal(this, 1.1D, Blocks.SEAGRASS.asItem()));
        this.goalSelector.addGoal(3, new ApertotemporalisEntity.GoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new ApertotemporalisEntity.GoHomeGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new ApertotemporalisEntity.TravelGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new ApertotemporalisEntity.WanderGoal(this, 1.0D, 100));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    @Override
    public int getTalkInterval() {
        return 200;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return !this.isInWater() && this.onGround && !this.isChild() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
    }
    
    @Override
    protected void playSwimSound(float volume) {
        super.playSwimSound(volume * 1.5F);
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_TURTLE_SWIM;
    }
    
    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    @Override
    protected float determineNextStepDistance() {
        return this.distanceWalkedOnStepModified + 0.15F;
    }

    @Override
    public float getRenderScale() {
        return this.isChild() ? 0.3F : 1.0F;
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new ApertotemporalisEntity.Navigator(this, worldIn);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) {
        return EntityInit.APERTOTEMPORALIS_ENTITY.get().create(world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Blocks.SEAGRASS.asItem();
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (!this.isGoingHome() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
            return 10.0F;
        } else {
            return ApertotemporalisEggBlock.hasProperHabitat(worldIn, pos) ? 10.0F : worldIn.getBrightness(pos) - 0.5F; //Change
        }
    }

    @Override
    public void livingTick() {
        if (this.isAlive() && this.isDigging() && this.isDigging >= 1 && this.isDigging % 5 == 0) {
            BlockPos blockpos = this.getPosition();
            if (ApertotemporalisEggBlock.hasProperHabitat(this.world, blockpos)) {
                this.world.playEvent(2001, blockpos, Block.getStateId(Blocks.DIRT.getDefaultState()));
            }
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(0.1F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null && (!this.isGoingHome() || !this.getHome().withinDistance(this.getPositionVec(), 20.0D))) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return false;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if(event.isMoving()){
            if(isInWater()){

                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.swim", true));
                    event.getController().setAnimationSpeed(1.0D);
                    return PlayState.CONTINUE;
                }
            }

        if(isInWater()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.swim_idle", true));
            event.getController().setAnimationSpeed(1.0D);
            return PlayState.CONTINUE;
        }

        if(event.isMoving()) {
            if(isOnGround()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.walk", true));
                event.getController().setAnimationSpeed(1.0D);
                return PlayState.CONTINUE;
            }
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.idle", true));
        event.getController().setAnimationSpeed(1.0D);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    static class GoHomeGoal extends Goal {
        private final ApertotemporalisEntity apertotemporalis;
        private final double speed;
        private boolean field_203129_c;
        private int field_203130_d;

        GoHomeGoal(ApertotemporalisEntity apertotemporalis, double speedIn) {
            this.apertotemporalis = apertotemporalis;
            this.speed = speedIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            if (this.apertotemporalis.isChild()) {
                return false;
            } else if (this.apertotemporalis.hasEgg()) {
                return true;
            } else if (this.apertotemporalis.getRNG().nextInt(700) != 0) {
                return false;
            } else {
                return !this.apertotemporalis.getHome().withinDistance(this.apertotemporalis.getPositionVec(), 64.0D);
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void startExecuting() {
            this.apertotemporalis.setGoingHome(true);
            this.field_203129_c = false;
            this.field_203130_d = 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        @Override
        public void resetTask() {
            this.apertotemporalis.setGoingHome(false);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            return !this.apertotemporalis.getHome().withinDistance(this.apertotemporalis.getPositionVec(), 7.0D) && !this.field_203129_c && this.field_203130_d <= 600;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            BlockPos blockpos = this.apertotemporalis.getHome();
            boolean flag = blockpos.withinDistance(this.apertotemporalis.getPositionVec(), 16.0D);
            if (flag) {
                ++this.field_203130_d;
            }

            if (this.apertotemporalis.getNavigator().noPath()) {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(blockpos);
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.apertotemporalis, 16, 3, vector3d, (double)((float)Math.PI / 10F));
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.apertotemporalis, 8, 7, vector3d);
                }

                if (vector3d1 != null && !flag && !this.apertotemporalis.world.getBlockState(new BlockPos(vector3d1)).matchesBlock(Blocks.WATER)) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.apertotemporalis, 16, 5, vector3d);
                }

                if (vector3d1 == null) {
                    this.field_203129_c = true;
                    return;
                }

                this.apertotemporalis.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }

        }
    }

    static class GoToWaterGoal extends MoveToBlockGoal {
        private final ApertotemporalisEntity apertotemporalis;

        private GoToWaterGoal(ApertotemporalisEntity apertotemporalis, double speedIn) {
            super(apertotemporalis, apertotemporalis.isChild() ? 2.0D : speedIn, 24);
            this.apertotemporalis = apertotemporalis;
            this.field_203112_e = -1;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            return !this.apertotemporalis.isInWater() && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.apertotemporalis.world, this.destinationBlock);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            if (this.apertotemporalis.isChild() && !this.apertotemporalis.isInWater()) {
                return super.shouldExecute();
            } else {
                return !this.apertotemporalis.isGoingHome() && !this.apertotemporalis.isInWater() && !this.apertotemporalis.hasEgg() && super.shouldExecute();
            }
        }

        @Override
        public boolean shouldMove() {
            return this.timeoutCounter % 160 == 0;
        }

        /**
         * Return true to set given position as destination
         */
        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.getBlockState(pos).matchesBlock(Blocks.WATER);
        }
    }

    static class LayEggGoal extends MoveToBlockGoal {
        private final ApertotemporalisEntity apertotemporalis;

        LayEggGoal(ApertotemporalisEntity apertotemporalis, double speedIn) {
            super(apertotemporalis, speedIn, 16);
            this.apertotemporalis = apertotemporalis;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            return this.apertotemporalis.hasEgg() && this.apertotemporalis.getHome().withinDistance(this.apertotemporalis.getPositionVec(), 9.0D) && super.shouldExecute();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.apertotemporalis.hasEgg() && this.apertotemporalis.getHome().withinDistance(this.apertotemporalis.getPositionVec(), 9.0D);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            super.tick();
            BlockPos blockpos = this.apertotemporalis.getPosition();
            if (!this.apertotemporalis.isInWater() && this.getIsAboveDestination()) {
                if (this.apertotemporalis.isDigging < 1) {
                    this.apertotemporalis.setDigging(true);
                } else if (this.apertotemporalis.isDigging > 200) {
                    World world = this.apertotemporalis.world;
                    world.playSound(null, blockpos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.rand.nextFloat() * 0.2F);
                    world.setBlockState(this.destinationBlock.up(), Blocks.TURTLE_EGG.getDefaultState().with(ApertotemporalisEggBlock.EGGS, Integer.valueOf(this.apertotemporalis.rand.nextInt(4) + 1)), 3);
                    this.apertotemporalis.setHasEgg(false);
                    this.apertotemporalis.setDigging(false);
                    this.apertotemporalis.setInLove(600);
                }

                if (this.apertotemporalis.isDigging()) {
                    this.apertotemporalis.isDigging++;
                }
            }

        }

        /**
         * Return true to set given position as destination
         */
        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.isAirBlock(pos.up()) && ApertotemporalisEggBlock.hasProperHabitat(worldIn, pos);
        }
    }

    static class MateGoal extends BreedGoal {
        private final ApertotemporalisEntity apertotemporalis;

        MateGoal(ApertotemporalisEntity apertotemporalis, double speedIn) {
            super(apertotemporalis, speedIn);
            this.apertotemporalis = apertotemporalis;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && !this.apertotemporalis.hasEgg();
        }

        /**
         * Spawns a baby animal of the same type.
         */
        protected void spawnBaby() {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
                serverplayerentity = this.targetMate.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, (AgeableEntity)null);
            }

            this.apertotemporalis.setHasEgg(true);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            Random random = this.animal.getRNG();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), random.nextInt(7) + 1));
            }

        }
    }

    static class MoveHelperController extends MovementController {
        private final ApertotemporalisEntity apertotemporalis;

        MoveHelperController(ApertotemporalisEntity apertotemporalisIn) {
            super(apertotemporalisIn);
            this.apertotemporalis = apertotemporalisIn;
        }

        private void updateSpeed() {
            if (this.apertotemporalis.isInWater()) {
                this.apertotemporalis.setMotion(this.apertotemporalis.getMotion().add(0.0D, 0.005D, 0.0D));
                if (!this.apertotemporalis.getHome().withinDistance(this.apertotemporalis.getPositionVec(), 16.0D)) {
                    this.apertotemporalis.setAIMoveSpeed(Math.max(this.apertotemporalis.getAIMoveSpeed() / 2.0F, 0.08F));
                }

                if (this.apertotemporalis.isChild()) {
                    this.apertotemporalis.setAIMoveSpeed(Math.max(this.apertotemporalis.getAIMoveSpeed() / 3.0F, 0.06F));
                }
            } else if (this.apertotemporalis.onGround) {
                this.apertotemporalis.setAIMoveSpeed(Math.max(this.apertotemporalis.getAIMoveSpeed() / 2.0F, 0.06F));
            }

        }

        @Override
        public void tick() {
            this.updateSpeed();
            if (this.action == MovementController.Action.MOVE_TO && !this.apertotemporalis.getNavigator().noPath()) {
                double d0 = this.posX - this.apertotemporalis.getPosX();
                double d1 = this.posY - this.apertotemporalis.getPosY();
                double d2 = this.posZ - this.apertotemporalis.getPosZ();
                double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 = d1 / d3;
                float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.apertotemporalis.rotationYaw = this.limitAngle(this.apertotemporalis.rotationYaw, f, 90.0F);
                this.apertotemporalis.renderYawOffset = this.apertotemporalis.rotationYaw;
                float f1 = (float)(this.speed * this.apertotemporalis.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.apertotemporalis.setAIMoveSpeed(MathHelper.lerp(0.125F, this.apertotemporalis.getAIMoveSpeed(), f1));
                this.apertotemporalis.setMotion(this.apertotemporalis.getMotion().add(0.0D, (double)this.apertotemporalis.getAIMoveSpeed() * d1 * 0.1D, 0.0D));
            } else {
                this.apertotemporalis.setAIMoveSpeed(0.0F);
            }
        }
    }

    static class Navigator extends SwimmerPathNavigator {
        Navigator(ApertotemporalisEntity apertotemporalis, World worldIn) {
            super(apertotemporalis, worldIn);
        }

        /**
         * If on ground or swimming and can swim
         */
        @Override
        protected boolean canNavigate() {
            return true;
        }

        @Override
        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new WalkAndSwimNodeProcessor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }

        @Override
        public boolean canEntityStandOnPos(BlockPos pos) {
            if (this.entity instanceof ApertotemporalisEntity) {
                ApertotemporalisEntity apertotemporalis = (ApertotemporalisEntity)this.entity;
                if (apertotemporalis.isTravelling()) {
                    return this.world.getBlockState(pos).matchesBlock(Blocks.WATER);
                }
            }

            return !this.world.getBlockState(pos.down()).isAir();
        }
    }

    static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        PanicGoal(ApertotemporalisEntity apertotemporalis, double speedIn) {
            super(apertotemporalis, speedIn);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
                return false;
            } else {
                BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 7, 4);
                if (blockpos != null) {
                    this.randPosX = blockpos.getX();
                    this.randPosY = blockpos.getY();
                    this.randPosZ = blockpos.getZ();
                    return true;
                } else {
                    return this.findRandomPosition();
                }
            }
        }
    }

    static class TravelGoal extends Goal {
        private final ApertotemporalisEntity apertotemporalis;
        private final double speed;
        private boolean field_203139_c;

        TravelGoal(ApertotemporalisEntity apertotemporalis, double speedIn) {
            this.apertotemporalis = apertotemporalis;
            this.speed = speedIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            return !this.apertotemporalis.isGoingHome() && !this.apertotemporalis.hasEgg() && this.apertotemporalis.isInWater();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void startExecuting() {
            int i = 512;
            int j = 4;
            Random random = this.apertotemporalis.rand;
            int k = random.nextInt(1025) - 512;
            int l = random.nextInt(9) - 4;
            int i1 = random.nextInt(1025) - 512;
            if ((double)l + this.apertotemporalis.getPosY() > (double)(this.apertotemporalis.world.getSeaLevel() - 1)) {
                l = 0;
            }

            BlockPos blockpos = new BlockPos((double)k + this.apertotemporalis.getPosX(), (double)l + this.apertotemporalis.getPosY(), (double)i1 + this.apertotemporalis.getPosZ());
            this.apertotemporalis.setTravelPos(blockpos);
            this.apertotemporalis.setTravelling(true);
            this.field_203139_c = false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            if (this.apertotemporalis.getNavigator().noPath()) {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.apertotemporalis.getTravelPos());
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.apertotemporalis, 16, 3, vector3d, (double)((float)Math.PI / 10F));
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.apertotemporalis, 8, 7, vector3d);
                }

                if (vector3d1 != null) {
                    int i = MathHelper.floor(vector3d1.x);
                    int j = MathHelper.floor(vector3d1.z);
                    int k = 34;
                    if (!this.apertotemporalis.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                        vector3d1 = null;
                    }
                }

                if (vector3d1 == null) {
                    this.field_203139_c = true;
                    return;
                }

                this.apertotemporalis.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }

        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            return !this.apertotemporalis.getNavigator().noPath() && !this.field_203139_c && !this.apertotemporalis.isGoingHome() && !this.apertotemporalis.isInLove() && !this.apertotemporalis.hasEgg();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        @Override
        public void resetTask() {
            this.apertotemporalis.setTravelling(false);
            super.resetTask();
        }
    }

    static class WanderGoal extends RandomWalkingGoal {
        private final ApertotemporalisEntity apertotemporalis;

        private WanderGoal(ApertotemporalisEntity apertotemporalis, double speedIn, int chance) {
            super(apertotemporalis, speedIn, chance);
            this.apertotemporalis = apertotemporalis;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */

        @Override
        public boolean shouldExecute() {
            return !this.apertotemporalis.isInWater() && !this.apertotemporalis.isGoingHome() && !this.apertotemporalis.hasEgg() && super.shouldExecute();

        }
    }

    static class PlayerTemptGoal extends Goal {
        private static final EntityPredicate field_220834_a = (new EntityPredicate()).setDistance(10.0D).allowFriendlyFire().allowInvulnerable();
        private final ApertotemporalisEntity apertotemporalis;
        private final double speed;
        private PlayerEntity tempter;
        private int cooldown;
        private final Set<Item> temptItems;

        PlayerTemptGoal(ApertotemporalisEntity apertotemporalis, double speedIn, Item temptItem) {
            this.apertotemporalis = apertotemporalis;
            this.speed = speedIn;
            this.temptItems = Sets.newHashSet(temptItem);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            } else {
                this.tempter = this.apertotemporalis.world.getClosestPlayer(field_220834_a, this.apertotemporalis);
                if (this.tempter == null) {
                    return false;
                } else {
                    return this.isTemptedBy(this.tempter.getHeldItemMainhand()) || this.isTemptedBy(this.tempter.getHeldItemOffhand());
                }
            }
        }

        private boolean isTemptedBy(ItemStack p_203131_1_) {
            return this.temptItems.contains(p_203131_1_.getItem());
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        @Override
        public void resetTask() {
            this.tempter = null;
            this.apertotemporalis.getNavigator().clearPath();
            this.cooldown = 100;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            this.apertotemporalis.getLookController().setLookPositionWithEntity(this.tempter, (float)(this.apertotemporalis.getHorizontalFaceSpeed() + 20), (float)this.apertotemporalis.getVerticalFaceSpeed());
            if (this.apertotemporalis.getDistanceSq(this.tempter) < 6.25D) {
                this.apertotemporalis.getNavigator().clearPath();
            } else {
                this.apertotemporalis.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
            }

        }
    }
}
