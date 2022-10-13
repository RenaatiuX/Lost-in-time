package com.rena.lost.common.entity.aquatic;

import com.mojang.authlib.GameProfile;
import com.rena.lost.LostInTime;
import com.rena.lost.common.block.ApertotemporalisEggBlock;
import com.rena.lost.core.BlockInit;
import com.rena.lost.core.EntityInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import java.util.EnumSet;
import java.util.Random;

public class ApertotemporalisEntity extends AnimalEntity implements IAnimatable, IAnimationTickable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_DIGGING = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PUSH_BUTTON = EntityDataManager.createKey(ApertotemporalisEntity.class, DataSerializers.BOOLEAN);
    private int digging;

    public ApertotemporalisEntity(EntityType<ApertotemporalisEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.moveController = new ApertotemporalisEntity.MoveHelperController(this);
        this.stepHeight = 1.0F;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
                .createMutableAttribute(ForgeMod.SWIM_SPEED.get(), 1.0D);
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
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
        this.digging = isDigging ? 1 : 0;
        this.dataManager.set(IS_DIGGING, isDigging);
    }

    public boolean isGoingHome() {
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

    private boolean isPushButton() {
        return this.dataManager.get(PUSH_BUTTON);
    }

    private void setPushButton(boolean isPushButton) {
        this.dataManager.set(PUSH_BUTTON, isPushButton);
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
        this.dataManager.register(PUSH_BUTTON, false);
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

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setHome(this.getPosition());
        this.setTravelPos(BlockPos.ZERO);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ApertotemporalisEntity.PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new ApertotemporalisEntity.MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new ApertotemporalisEntity.LayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new ApertotemporalisEntity.PlayerTemptGoal(this, 1.1D, Ingredient.fromItems(Items.FERN, Items.SEAGRASS)));
        this.goalSelector.addGoal(3, new ApertotemporalisEntity.GoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new ApertotemporalisEntity.GoHomeGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new ApertotemporalisEntity.PushButton(this, 0.6D));
        this.goalSelector.addGoal(7, new ApertotemporalisEntity.TravelGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new ApertotemporalisEntity.WanderGoal(this, 1.0D, 100));
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
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
    public int getTalkInterval() {
        return 200;
    }

    @Override
    protected float determineNextStepDistance() {
        return this.distanceWalkedOnStepModified + 0.15F;
    }

    /*@Override
    public float getRenderScale() {
        return this.isChild() ? 0.3F : 1.0F;
    }*/

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
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
        return stack.getItem() == Blocks.SEAGRASS.asItem() || stack.getItem() == Blocks.FERN.asItem();
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (!this.isGoingHome() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
            return 10.0F;
        } else {
            return ApertotemporalisEggBlock.hasProperHabitat(worldIn, pos) ? 10.0F : worldIn.getBrightness(pos) - 0.5F;
        }
    }

    @Override
    protected void onGrowingAdult() {
        super.onGrowingAdult();
        if (!this.isChild() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.entityDropItem(Items.FERN, 1);
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
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.isAlive() && this.isDigging() && this.digging >= 1 && this.digging % 5 == 0) {
            BlockPos blockpos = this.getPosition();
            if (ApertotemporalisEggBlock.hasProperHabitat(this.world, blockpos)) {
                this.world.playEvent(2001, blockpos, Block.getStateId(Blocks.DIRT.getDefaultState()));
                this.world.playEvent(2001, blockpos, Block.getStateId(BlockInit.MUD.get().getDefaultState()));
            }
        }
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return false;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (event.getController().getCurrentAnimation() != null){
            System.out.println(event.getController().getCurrentAnimation().animationName);
        }

        if (this.isOnGround()) {
            if (horizontalMag(this.getMotion()) > 1.0E-6) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.walk", ILoopType.EDefaultLoopTypes.LOOP));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }

        else if (this.isInWaterOrBubbleColumn()) {
            if (horizontalMag(this.getMotion()) > 1.0E-6) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.swim", ILoopType.EDefaultLoopTypes.LOOP));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.swim_idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        else if (this.isPushButton()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.apertotemporalis.buttonpush", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
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

    @Override
    public int tickTimer() {
        return ticksExisted;
    }

    static class GoHomeGoal extends Goal {
        private final ApertotemporalisEntity turtle;
        private final double speed;
        private boolean field_203129_c;
        private int field_203130_d;

        GoHomeGoal(ApertotemporalisEntity turtle, double speedIn) {
            this.turtle = turtle;
            this.speed = speedIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (this.turtle.isChild()) {
                return false;
            } else if (this.turtle.hasEgg()) {
                return true;
            } else if (this.turtle.getRNG().nextInt(700) != 0) {
                return false;
            } else {
                return !this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 64.0D);
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.turtle.setGoingHome(true);
            this.field_203129_c = false;
            this.field_203130_d = 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            this.turtle.setGoingHome(false);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return !this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 7.0D) && !this.field_203129_c && this.field_203130_d <= 600;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = this.turtle.getHome();
            boolean flag = blockpos.withinDistance(this.turtle.getPositionVec(), 16.0D);
            if (flag) {
                ++this.field_203130_d;
            }

            if (this.turtle.getNavigator().noPath()) {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(blockpos);
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, vector3d, (double) ((float) Math.PI / 10F));
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, vector3d);
                }

                if (vector3d1 != null && !flag && !this.turtle.world.getBlockState(new BlockPos(vector3d1)).matchesBlock(Blocks.WATER)) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 16, 5, vector3d);
                }

                if (vector3d1 == null) {
                    this.field_203129_c = true;
                    return;
                }

                this.turtle.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }

        }
    }

    static class GoToWaterGoal extends MoveToBlockGoal {
        private final ApertotemporalisEntity turtle;

        private GoToWaterGoal(ApertotemporalisEntity turtle, double speedIn) {
            super(turtle, turtle.isChild() ? 2.0D : speedIn, 24);
            this.turtle = turtle;
            this.field_203112_e = -1;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return !this.turtle.isInWater() && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.turtle.world, this.destinationBlock);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (this.turtle.isChild() && !this.turtle.isInWater()) {
                return super.shouldExecute();
            } else {
                return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() && super.shouldExecute();
            }
        }

        public boolean shouldMove() {
            return this.timeoutCounter % 160 == 0;
        }

        /**
         * Return true to set given position as destination
         */
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.getBlockState(pos).matchesBlock(Blocks.WATER);
        }
    }

    static class LayEggGoal extends MoveToBlockGoal {
        private final ApertotemporalisEntity turtle;

        LayEggGoal(ApertotemporalisEntity turtle, double speedIn) {
            super(turtle, speedIn, 16);
            this.turtle = turtle;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return this.turtle.hasEgg() && this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 9.0D) && super.shouldExecute();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.turtle.hasEgg() && this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 9.0D);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            super.tick();
            BlockPos blockpos = this.turtle.getPosition();
            if (!this.turtle.isInWater() && this.getIsAboveDestination()) {
                if (this.turtle.digging < 1) {
                    this.turtle.setDigging(true);
                } else if (this.turtle.digging > 200) {
                    World world = this.turtle.world;
                    world.playSound(null, blockpos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.rand.nextFloat() * 0.2F);
                    world.setBlockState(this.destinationBlock.up(), BlockInit.APERTOTEMPORALIS_EGG.get().getDefaultState().with(ApertotemporalisEggBlock.EGGS, this.turtle.rand.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setDigging(false);
                    this.turtle.setInLove(600);
                }

                if (this.turtle.isDigging()) {
                    this.turtle.digging++;
                }
            }

        }

        /**
         * Return true to set given position as destination
         */
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.isAirBlock(pos.up()) && ApertotemporalisEggBlock.hasProperHabitat(worldIn, pos);
        }
    }

    static class MateGoal extends BreedGoal {
        private final ApertotemporalisEntity turtle;

        MateGoal(ApertotemporalisEntity turtle, double speedIn) {
            super(turtle, speedIn);
            this.turtle = turtle;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return super.shouldExecute() && !this.turtle.hasEgg();
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
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, null);
            }

            this.turtle.setHasEgg(true);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            Random random = this.animal.getRNG();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), random.nextInt(7) + 1));
            }

        }
    }

    static class MoveHelperController extends MovementController {
        private final ApertotemporalisEntity turtle;

        MoveHelperController(ApertotemporalisEntity turtleIn) {
            super(turtleIn);
            this.turtle = turtleIn;
        }

        private void updateSpeed() {
            if (this.turtle.isInWater()) {
                this.turtle.setMotion(this.turtle.getMotion().add(0.0D, 0.005D, 0.0D));
                if (!this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 16.0D)) {
                    this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.08F));
                }

                if (this.turtle.isChild()) {
                    this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 3.0F, 0.06F));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.06F));
            }

        }

        public void tick() {
            this.updateSpeed();
            if (this.action == MovementController.Action.MOVE_TO && !this.turtle.getNavigator().noPath()) {
                double d0 = this.posX - this.turtle.getPosX();
                double d1 = this.posY - this.turtle.getPosY();
                double d2 = this.posZ - this.turtle.getPosZ();
                double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 = d1 / d3;
                float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.turtle.rotationYaw = this.limitAngle(this.turtle.rotationYaw, f, 90.0F);
                this.turtle.renderYawOffset = this.turtle.rotationYaw;
                float f1 = (float) (this.speed * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.turtle.setAIMoveSpeed(MathHelper.lerp(0.125F, this.turtle.getAIMoveSpeed(), f1));
                this.turtle.setMotion(this.turtle.getMotion().add(0.0D, (double) this.turtle.getAIMoveSpeed() * d1 * 0.1D, 0.0D));
            } else {
                this.turtle.setAIMoveSpeed(0.0F);
            }
        }
    }

    static class Navigator extends SwimmerPathNavigator {
        Navigator(ApertotemporalisEntity turtle, World worldIn) {
            super(turtle, worldIn);
        }

        /**
         * If on ground or swimming and can swim
         */
        protected boolean canNavigate() {
            return true;
        }

        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new WalkAndSwimNodeProcessor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }

        public boolean canEntityStandOnPos(BlockPos pos) {
            if (this.entity instanceof ApertotemporalisEntity) {
                ApertotemporalisEntity turtleentity = (ApertotemporalisEntity) this.entity;
                if (turtleentity.isTravelling()) {
                    return this.world.getBlockState(pos).matchesBlock(Blocks.WATER);
                }
            }

            return !this.world.getBlockState(pos.down()).isAir();
        }
    }

    static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        PanicGoal(ApertotemporalisEntity turtle, double speedIn) {
            super(turtle, speedIn);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
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

    static class PlayerTemptGoal extends Goal {
        private static final EntityPredicate field_220834_a = (new EntityPredicate()).setDistance(10.0D).allowFriendlyFire().allowInvulnerable();
        private final ApertotemporalisEntity turtle;
        private final double speed;
        private PlayerEntity tempter;
        private int cooldown;
        private final Ingredient temptItems;

        PlayerTemptGoal(ApertotemporalisEntity turtle, double speedIn, Ingredient temptItem) {
            this.turtle = turtle;
            this.speed = speedIn;
            this.temptItems = temptItem;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            } else {
                this.tempter = this.turtle.world.getClosestPlayer(field_220834_a, this.turtle);
                if (this.tempter == null) {
                    return false;
                } else {
                    return this.isTemptedBy(this.tempter.getHeldItemMainhand()) || this.isTemptedBy(this.tempter.getHeldItemOffhand());
                }
            }
        }

        private boolean isTemptedBy(ItemStack p_203131_1_) {
            return this.temptItems.test(p_203131_1_);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            this.tempter = null;
            this.turtle.getNavigator().clearPath();
            this.cooldown = 100;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            this.turtle.getLookController().setLookPositionWithEntity(this.tempter, (float) (this.turtle.getHorizontalFaceSpeed() + 20), (float) this.turtle.getVerticalFaceSpeed());
            if (this.turtle.getDistanceSq(this.tempter) < 6.25D) {
                this.turtle.getNavigator().clearPath();
            } else {
                this.turtle.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
            }

        }
    }

    static class TravelGoal extends Goal {
        private final ApertotemporalisEntity turtle;
        private final double speed;
        private boolean field_203139_c;

        TravelGoal(ApertotemporalisEntity turtle, double speedIn) {
            this.turtle = turtle;
            this.speed = speedIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            int i = 512;
            int j = 4;
            Random random = this.turtle.rand;
            int k = random.nextInt(1025) - 512;
            int l = random.nextInt(9) - 4;
            int i1 = random.nextInt(1025) - 512;
            if ((double) l + this.turtle.getPosY() > (double) (this.turtle.world.getSeaLevel() - 1)) {
                l = 0;
            }

            BlockPos blockpos = new BlockPos((double) k + this.turtle.getPosX(), (double) l + this.turtle.getPosY(), (double) i1 + this.turtle.getPosZ());
            this.turtle.setTravelPos(blockpos);
            this.turtle.setTravelling(true);
            this.field_203139_c = false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (this.turtle.getNavigator().noPath()) {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.turtle.getTravelPos());
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, vector3d, (double) ((float) Math.PI / 10F));
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, vector3d);
                }

                if (vector3d1 != null) {
                    int i = MathHelper.floor(vector3d1.x);
                    int j = MathHelper.floor(vector3d1.z);
                    int k = 34;
                    if (!this.turtle.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                        vector3d1 = null;
                    }
                }

                if (vector3d1 == null) {
                    this.field_203139_c = true;
                    return;
                }

                this.turtle.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }

        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return !this.turtle.getNavigator().noPath() && !this.field_203139_c && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            this.turtle.setTravelling(false);
            super.resetTask();
        }
    }

    static class WanderGoal extends RandomWalkingGoal {
        private final ApertotemporalisEntity turtle;

        private WanderGoal(ApertotemporalisEntity turtle, double speedIn, int chance) {
            super(turtle, speedIn, chance);
            this.turtle = turtle;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return !this.creature.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() && super.shouldExecute();
        }
    }

    static class PushButton extends MoveToBlockGoal {

        private final ApertotemporalisEntity apertotemporalis;

        public PushButton(ApertotemporalisEntity apertotemporalis, double speedIn) {
            super(apertotemporalis, speedIn, 12, 3);
            this.apertotemporalis = apertotemporalis;
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            this.apertotemporalis.setPushButton(true);
        }

        @Override
        public boolean shouldExecute() {
            if (this.apertotemporalis.isChild() && !this.apertotemporalis.isInWater()) {
                return super.shouldExecute();
            } else {
                return !this.apertotemporalis.isGoingHome() && !this.apertotemporalis.isInWater() && !this.apertotemporalis.hasEgg() && super.shouldExecute();
            }
        }

        @Override
        protected int getRunDelay(CreatureEntity creatureIn) {
            return 360;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && !this.apertotemporalis.isGoingHome() && !this.apertotemporalis.isInWater() && !this.apertotemporalis.hasEgg();
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            BlockState blockstate = worldIn.getBlockState(pos);
            return blockstate.isIn(BlockTags.BUTTONS);
        }

        @Override
        public void tick() {
            super.tick();
            ServerWorld worldIn = (ServerWorld) this.apertotemporalis.world;
            BlockPos pos = this.destinationBlock;
            BlockState state = worldIn.getBlockState(pos);
            this.apertotemporalis.getLookController().setLookPosition(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY(), this.destinationBlock.getZ() + 0.5D, 10.0F, this.apertotemporalis.getVerticalFaceSpeed());
            double targetDistance = getTargetDistanceSq();
            Block destinationBlockType = this.apertotemporalis.world.getBlockState(destinationBlock).getBlock();
            if (destinationBlockType instanceof StoneButtonBlock) targetDistance += 0.2D;
            if (destinationBlockType instanceof WoodButtonBlock) targetDistance += 0.55D;
            if (!this.destinationBlock.withinDistance(this.creature.getPositionVec(), targetDistance)) {
                ++this.timeoutCounter;
                if (this.shouldMove()) {
                    this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 1D, this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
                }
            } else {
                --this.timeoutCounter;
                pushButton(worldIn, pos, state);
            }
        }

        protected void pushButton(ServerWorld worldIn, BlockPos pos, BlockState state) {

            PlayerEntity push = FakePlayerFactory.get(worldIn, new GameProfile(null, "apertotemporalis"));
            BlockRayTraceResult result = new BlockRayTraceResult(apertotemporalis.getPositionVec(), apertotemporalis.getHorizontalFacing().getOpposite(), pos, false);

            try {
                state.onBlockActivated(worldIn, push, Hand.MAIN_HAND, result);
                MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(push, Hand.MAIN_HAND, pos, apertotemporalis.getHorizontalFacing().getOpposite()));
            } catch (NullPointerException ex) {
                LostInTime.LOGGER.info(String.format("Entity could not push block at: %s", pos));
            }
            push.remove(false);
        }

    }
}
