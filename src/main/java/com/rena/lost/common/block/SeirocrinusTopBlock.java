package com.rena.lost.common.block;

import com.rena.lost.core.init.BlockInit;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SeirocrinusTopBlock extends AbstractTopPlantBlock implements ILiquidContainer{
    public static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

    public SeirocrinusTopBlock(Properties properties, Direction direction, VoxelShape shape, boolean waterloggable, double growthChance) {
        super(properties, direction, shape, waterloggable, growthChance);
    }

    @Override
    protected int getGrowthAmount(Random rand) {
        return 1;
    }

    @Override
    protected boolean canGrowIn(BlockState state) {
        return state.matchesBlock(Blocks.WATER);
    }

    @Override
    protected Block getBodyPlantBlock() {
        return BlockInit.SEIROCRINUS_PLANT.get();
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return false;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        return fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8 ? super.getStateForPlacement(context) : null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStillFluidState(false);
    }
}
