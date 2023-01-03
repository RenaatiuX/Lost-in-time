package com.rena.lost.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.rena.lost.common.block.SeirocrinusTopBlock;
import com.rena.lost.core.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class SeirocrinusFeature extends Feature<NoFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();
    public SeirocrinusFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (reader.getBlockState(pos).getBlock() != Blocks.WATER) {
            return false;
        } else {
            BlockState blockstate = reader.getBlockState(pos.up());
            if (!blockstate.matchesBlock(BlockInit.HORIZONTAL_LOG.get())) {
                return false;
            } else {
                this.placeRoofLogInWater(reader, rand, pos);
                this.placeRoofSeirocrinus(reader, rand, pos);
                return true;
            }
        }
    }

    private void placeRoofLogInWater(IWorld world, Random random, BlockPos pos) {
        world.setBlockState(pos, BlockInit.HORIZONTAL_LOG.get().getDefaultState(), 2);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();

        for (int i = 0; i < 200; ++i) {
            blockpos$mutable.setAndOffset(pos, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
            if (world.getBlockState(blockpos$mutable).getBlock() == Blocks.WATER) {
                int j = 0;

                for (Direction direction : DIRECTIONS) {
                    BlockState blockstate = world.getBlockState(blockpos$mutable1.setAndMove(blockpos$mutable, direction));
                    if (blockstate.matchesBlock(BlockInit.HORIZONTAL_LOG.get())) {
                        ++j;
                    }
                    if (j > 1) {
                        break;
                    }
                }
                if (j == 1) {
                    world.setBlockState(blockpos$mutable, BlockInit.HORIZONTAL_LOG.get().getDefaultState(), 2);
                }
            }
        }
    }

    private void placeRoofSeirocrinus(IWorld world, Random random, BlockPos pos) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i < 100; ++i) {
            blockpos$mutable.setAndOffset(pos, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
            if (world.getBlockState(blockpos$mutable).getBlock() == Blocks.WATER) {
                BlockState blockstate = world.getBlockState(blockpos$mutable.up());
                if (blockstate.matchesBlock(BlockInit.HORIZONTAL_LOG.get())) {
                    int j = MathHelper.nextInt(random, 1, 8);
                    if (random.nextInt(6) == 0) {
                        j *= 2;
                    }

                    if (random.nextInt(5) == 0) {
                        j = 1;
                    }

                    placeSeirocrinusColumn(world, random, blockpos$mutable, j, 17, 25);
                }
            }
        }

    }

    public static void placeSeirocrinusColumn(IWorld world, Random random, BlockPos.Mutable mutable, int height, int minAge, int maxAge) {
        for (int i = 0; i <= height; ++i) {
            if (world.getBlockState(mutable).getBlock() == Blocks.WATER) {
                if (i == height || world.getBlockState(mutable.down()).getBlock() != Blocks.WATER) {
                    world.setBlockState(mutable, BlockInit.SEIROCRINUS.get().getDefaultState().with(SeirocrinusTopBlock.AGE, MathHelper.nextInt(random, minAge, maxAge)), 2);
                    break;
                }
                world.setBlockState(mutable, BlockInit.SEIROCRINUS_PLANT.get().getDefaultState(), 2);
            }
            mutable.move(Direction.DOWN);
        }
    }
}
