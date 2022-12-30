package com.rena.lost.common.block;

import com.rena.lost.core.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TeleporterBlock extends Block {

    public static final IntegerProperty CHARGES = BlockStateProperties.CHARGES;

    public TeleporterBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(CHARGES, 0));
    }

    private static boolean isValidFuel(ItemStack stack) {
        return stack.getItem() == ItemInit.VOIDITE_FRAGMENT.get();
    }

    private static boolean notFullyCharged(BlockState state) {
        return state.get(CHARGES) < 4;
    }

    public static void chargeTeleporter(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(CHARGES, state.get(CHARGES) + 1), 3);
        world.playSound(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(CHARGES) != 0) {
            if (rand.nextInt(100) == 0) {
                worldIn.playSound(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            double d0 = (double)pos.getX() + 0.5D + (0.5D - rand.nextDouble());
            double d1 = (double)pos.getY() + 1.0D;
            double d2 = (double)pos.getZ() + 0.5D + (0.5D - rand.nextDouble());
            double d3 = (double)rand.nextFloat() * 0.04D;
            worldIn.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, 0.0D, d3, 0.0D);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CHARGES);
    }
}
