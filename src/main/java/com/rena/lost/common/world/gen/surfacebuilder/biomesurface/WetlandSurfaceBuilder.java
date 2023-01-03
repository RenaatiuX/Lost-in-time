package com.rena.lost.common.world.gen.surfacebuilder.biomesurface;

import com.mojang.serialization.Codec;
import com.rena.lost.common.world.gen.surfacebuilder.LostConfiguredSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class WetlandSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
    public WetlandSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        int randomizer = random.nextInt(5);

        if (randomizer <= 2)
            SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, LostConfiguredSurfaceBuilder.PODZOL);
        if (randomizer == 3)
            SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, LostConfiguredSurfaceBuilder.COARSE);
        if (randomizer == 4)
            SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, LostConfiguredSurfaceBuilder.GRASS);
    }
}
