package com.rena.lost.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;

public class DoublePlantWaterBlock extends DoublePlantBlock implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DoublePlantWaterBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, Boolean.TRUE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        BlockPos blockpos = context.getPos();
        return blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up())
                .isReplaceable(context) ? this.getDefaultState()
                .with(WATERLOGGED, ifluidstate.isTagged(FluidTags.WATER)
                        && ifluidstate.getLevel() == 8) : null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return facing == Direction.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, false), 3);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        if (state.get(HALF) != DoubleBlockHalf.UPPER)
        {
            BlockPos posBelow = pos.down();
            BlockState existingState = worldIn.getBlockState(pos);
            Block existingBlock = existingState.getBlock();
            return (existingBlock == this || existingState.getMaterial() == Material.WATER) && this.isExposed(worldIn, pos.up()) && worldIn.getBlockState(posBelow).isSolidSide(worldIn, posBelow, Direction.UP);
        }
        else
        {
            BlockState blockstate = worldIn.getBlockState(pos.down());
            if (state.getBlock() != this) return worldIn.isAirBlock(pos);
            return this.isExposed(worldIn, pos) && blockstate.getBlock() == this && blockstate.get(HALF) == DoubleBlockHalf.LOWER && blockstate.get(WATERLOGGED);
        }
    }

    @Override
    public void placeAt(IWorld worldIn, BlockPos pos, int flags) {
        worldIn.setBlockState(pos, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, true), flags);
        worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, false), flags);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.PLAINS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HALF);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    protected boolean isExposed(IWorldReader world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == this ? !state.get(WATERLOGGED) : world.isAirBlock(pos);
    }
}
