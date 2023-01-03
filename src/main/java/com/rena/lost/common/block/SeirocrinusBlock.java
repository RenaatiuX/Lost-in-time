package com.rena.lost.common.block;

import com.rena.lost.core.init.BlockInit;
import net.minecraft.block.AbstractBodyPlantBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class SeirocrinusBlock extends AbstractBodyPlantBlock implements ILiquidContainer {
    public SeirocrinusBlock(Properties properties, Direction growthDirection, VoxelShape shape, boolean waterloggable) {
        super(properties, growthDirection, shape, waterloggable);
    }

    @Override
    protected AbstractTopPlantBlock getTopPlantBlock() {
        return (AbstractTopPlantBlock) BlockInit.SEIROCRINUS.get();
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return false;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStillFluidState(false);
    }
}
