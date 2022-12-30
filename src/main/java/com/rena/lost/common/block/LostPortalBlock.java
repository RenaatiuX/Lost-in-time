package com.rena.lost.common.block;

import com.rena.lost.common.world.DimensionTeleporter;
import com.rena.lost.core.init.BlockInit;
import com.rena.lost.core.init.DimensionInit;
import com.rena.lost.core.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

import java.util.Random;

public class LostPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    public LostPortalBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(AXIS)) {
            case Z:
                return Z_AABB;
            case X:
            default:
                return X_AABB;
        }
    }

    public boolean trySpawnPortal(IWorld worldIn, BlockPos pos) {
        LostPortalBlock.Size LostPortalBlock$size = this.isPortal(worldIn, pos);
        if (LostPortalBlock$size != null && !onTrySpawnPortal(worldIn, pos, LostPortalBlock$size)) {
            LostPortalBlock$size.placePortalBlocks();
            return true;
        } else {
            return false;
        }
    }

    public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, LostPortalBlock.Size size) {
        return MinecraftForge.EVENT_BUS.post(new PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }

    @Cancelable
    public static class PortalSpawnEvent extends BlockEvent {
        private final LostPortalBlock.Size size;

        public PortalSpawnEvent(IWorld world, BlockPos pos, BlockState state, LostPortalBlock.Size size) {
            super(world, pos, state);
            this.size = size;
        }

        public LostPortalBlock.Size getPortalSize()
        {
            return size;
        }
    }

    @Nullable
    public LostPortalBlock.Size isPortal(IWorld worldIn, BlockPos pos) {
        LostPortalBlock.Size LostPortalBlock$size = new Size(worldIn, pos, Direction.Axis.X);
        if (LostPortalBlock$size.isValid() && LostPortalBlock$size.portalBlockCount == 0) {
            return LostPortalBlock$size;
        } else {
            LostPortalBlock.Size LostPortalBlock$size1 = new Size(worldIn, pos, Direction.Axis.Z);
            return LostPortalBlock$size1.isValid() && LostPortalBlock$size1.portalBlockCount == 0 ? LostPortalBlock$size1 : null;
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis direction$axis = facing.getAxis();
        Direction.Axis direction$axis1 = stateIn.get(AXIS);
        boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
        return !flag && facingState.getBlock() != this && !(new Size(worldIn, currentPos, direction$axis1)).validatePortal() ?
                Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
        if(!entity.isPassenger() && !entity.isBeingRidden() && entity.canChangeDimension()) {
            if(entity.hasPortalCooldown()) {
                entity.setPortalCooldown();
            }
            else {
                if(!entity.world.isRemote && !pos.equals(entity.entityBlockPosition)) {
                    entity.entityBlockPosition = pos.toImmutable();
                }
                World entityWorld = entity.world;
                if(entityWorld != null) {
                    MinecraftServer minecraftserver = entityWorld.getServer();
                    RegistryKey<World> destination = entity.world.getDimensionKey() == DimensionInit.MESOZOIC_KEY
                            ? World.OVERWORLD : DimensionInit.MESOZOIC_KEY;
                    if(minecraftserver != null) {
                        ServerWorld destinationWorld = minecraftserver.getWorld(destination);
                        if(destinationWorld != null && minecraftserver.getAllowNether() && !entity.isPassenger()) {
                            entity.world.getProfiler().startSection("mesozoic_portal");
                            entity.setPortalCooldown();
                            entity.changeDimension(destinationWorld, new DimensionTeleporter(destinationWorld));
                            entity.world.getProfiler().endSection();
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D,
                    (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT,
                    SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }

        for(int i = 0; i < 4; ++i) {
            double x = (double)pos.getX() + rand.nextDouble();
            double y = (double)pos.getY() + rand.nextDouble();
            double z = (double)pos.getZ() + rand.nextDouble();
            double xSpeed = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double ySpeed = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double zSpeed = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (!worldIn.getBlockState(pos.west()).matchesBlock(this) && !worldIn.getBlockState(pos.east()).matchesBlock(this)) {
                x = (double)pos.getX() + 0.5D + 0.25D * (double)j;
                xSpeed = rand.nextFloat() * 2.0F * (float)j;
            } else {
                z = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
                zSpeed = rand.nextFloat() * 2.0F * (float)j;
            }

            // TODO: Particles
            // worldIn.addParticle(PARTICLE_TYPE, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        switch(rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch(state.get(AXIS)) {
                    case Z:
                        return state.with(AXIS, Direction.Axis.X);
                    case X:
                        return state.with(AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public static class Size {
        private final IWorld level;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private int portalBlockCount;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size(IWorld level, BlockPos pos, Direction.Axis axis) {
            this.level = level;
            this.axis = axis;
            if (axis == Direction.Axis.X) {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }

            for(BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && this.canConnect(level.getBlockState(pos.down())); pos = pos.down()) {
            }

            int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
            if (i >= 0) {
                this.bottomLeft = pos.offset(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }

        }

        protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
            int i;
            for(i = 0; i < 22; ++i) {
                BlockPos blockpos = pos.offset(directionIn, i);
                if(!this.canConnect(this.level.getBlockState(blockpos)) ||
                        !(this.level.getBlockState(blockpos.down()).isIn(ModTags.Blocks.MESOZOIC_PORTAL_FRAME))) {
                    break;
                }
            }

            BlockPos framePos = pos.offset(directionIn, i);
            return this.level.getBlockState(framePos).isIn(ModTags.Blocks.MESOZOIC_PORTAL_FRAME) ? i : 0;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int calculatePortalHeight() {
            label56:
            for(this.height = 0; this.height < 21; ++this.height) {
                for(int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    if (!this.canConnect(blockstate)) {
                        break label56;
                    }

                    Block block = blockstate.getBlock();
                    if (block == BlockInit.MESOZOIC_PORTAL_BLOCK.get()) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        BlockPos framePos = blockpos.offset(this.leftDir);
                        if (!(this.level.getBlockState(framePos).isIn(ModTags.Blocks.MESOZOIC_PORTAL_FRAME))) {
                            break label56;
                        }
                    } else if (i == this.width - 1) {
                        BlockPos framePos = blockpos.offset(this.rightDir);
                        if (!(this.level.getBlockState(framePos).isIn(ModTags.Blocks.MESOZOIC_PORTAL_FRAME))) {
                            break label56;
                        }
                    }
                }
            }

            for(int j = 0; j < this.width; ++j) {
                BlockPos framePos = this.bottomLeft.offset(this.rightDir, j).up(this.height);
                if (!(this.level.getBlockState(framePos).isIn(ModTags.Blocks.MESOZOIC_PORTAL_FRAME))) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            } else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        protected boolean canConnect(BlockState pos) {
            Block block = pos.getBlock();
            return pos.isAir() || block == BlockInit.MESOZOIC_PORTAL_BLOCK.get();
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks() {
            for(int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

                for(int j = 0; j < this.height; ++j) {
                    this.level.setBlockState(blockpos.up(j), BlockInit.MESOZOIC_PORTAL_BLOCK.get().getDefaultState().with(LostPortalBlock.AXIS, this.axis), 18);
                }
            }

        }

        private boolean isPortalCountValidForSize() {
            return this.portalBlockCount >= this.width * this.height;
        }

        public boolean validatePortal() {
            return this.isValid() && this.isPortalCountValidForSize();
        }
    }
}
