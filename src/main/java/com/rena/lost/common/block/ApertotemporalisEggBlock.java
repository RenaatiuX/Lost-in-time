package com.rena.lost.common.block;

import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import com.rena.lost.core.EntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;

import java.util.Random;

public class ApertotemporalisEggBlock extends BaseEggBlock{

    public ApertotemporalisEggBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected <T extends AnimalEntity> Class<T>[] canTrampleOn() {
        return new Class[0];
    }

    @Override
    public ApertotemporalisEntity createChildEntity(World world) {
        return EntityInit.APERTOTEMPORALIS_ENTITY.get().create(world);
    }

    @Override
    public BlockState grow(BlockState currentEgg, World world, float efficiency) {
        if(canGrow(world, efficiency)) {
            int i = currentEgg.get(BlockStateProperties.HATCH_0_2);
            if(i < 2) {
                return currentEgg.with(BlockStateProperties.HATCH_0_2, i + 1);
            }
        }
        return currentEgg;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (hasProperHabitat(worldIn, pos)) {
            int i = state.get(HATCH);
            if (i < 2) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS,
                        0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.setBlockState(pos, state.with(HATCH, i + 1), 2);
            } else {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS,
                        0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.removeBlock(pos, false);

                for (int j = 0; j < state.get(EGGS); ++j) {
                    worldIn.playEvent(2001, pos, Block.getStateId(state));
                    ApertotemporalisEntity animal = createChildEntity(worldIn);
                    animal.setGrowingAge(-24000);
                    animal.setHome(pos);
                    animal.setLocationAndAngles((double) pos.getX() + 0.3D + (double) j * 0.2D,
                            pos.getY(), (double) pos.getZ() + 0.3D, 0.0F, 0.0F);
                    worldIn.addEntity(animal);
                }
            }
        }
    }

    public static boolean hasProperHabitat(IBlockReader reader, BlockPos blockReader) {
        return reader.getBlockState(blockReader).isIn(Tags.Blocks.DIRT);
    }

    protected boolean canGrow(World world, float efficiency) {
        return world.rand.nextDouble() < 0.01*efficiency;
    }

}
