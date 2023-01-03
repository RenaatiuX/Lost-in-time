package com.rena.lost.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.rena.lost.common.block.HorizontalLogBlock;
import com.rena.lost.core.init.BlockInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class LogFeature extends Feature<NoFeatureConfig> {
    public LogFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        int len = rand.nextInt(3) + 3;
        int offsetX = rand.nextInt(2), offsetZ = 1 - offsetX;

        if(pos.getY() != generator.getSeaLevel() - 1)
            return false;

        boolean wasLastBranch = false;
        for (int a = 0; a < len; a++) {
            BlockState state = BlockInit.HORIZONTAL_LOG.get().getDefaultState();
            if (offsetX != 0)
                state = state.with(RotatedPillarBlock.AXIS, Direction.EAST.getAxis());
            else if (offsetZ != 0)
                state = state.with(RotatedPillarBlock.AXIS, Direction.SOUTH.getAxis());
            this.setBlockState(reader, pos.add(offsetX * a, 0, offsetZ * a), state);
            if (wasLastBranch) {
                wasLastBranch = false;
            } else if (rand.nextInt(6) == 0) {
                wasLastBranch = true;
                BlockState state2 = BlockInit.HORIZONTAL_LOG.get().getDefaultState();
                if (offsetX != 0)
                    state2 = state.with(RotatedPillarBlock.AXIS, Direction.SOUTH.getAxis());
                else if (offsetZ != 0)
                    state2 = state.with(RotatedPillarBlock.AXIS, Direction.EAST.getAxis());
                BlockPos newPos = pos.add(offsetX * a, 0, offsetZ * a);
                if (rand.nextInt(2) == 0)
                    newPos = newPos.add(offsetZ != 0 ? 1 : 0, 0, offsetX != 0 ? 1 : 0);
                else
                    newPos = newPos.add(offsetZ != 0 ? -1 : 0, 0, offsetX != 0 ? -1 : 0);
                if (reader.getBlockState(newPos).getBlock() == Blocks.WATER) {
                    this.setBlockState(reader, newPos, state2);
                }
            }
        }
        return true;
    }
}
