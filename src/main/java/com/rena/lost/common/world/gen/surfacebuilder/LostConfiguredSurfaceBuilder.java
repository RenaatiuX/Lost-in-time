package com.rena.lost.common.world.gen.surfacebuilder;

import com.rena.lost.LostInTime;
import com.rena.lost.common.world.gen.surfacebuilder.biomesurface.WetlandSurfaceBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class LostConfiguredSurfaceBuilder {

    public static final SurfaceBuilderConfig PODZOL = new SurfaceBuilderConfig(Blocks.PODZOL.getDefaultState(),
            Blocks.PODZOL.getDefaultState(), Blocks.PODZOL.getDefaultState());
    public static final SurfaceBuilderConfig COARSE = new SurfaceBuilderConfig(Blocks.COARSE_DIRT.getDefaultState(),
            Blocks.COARSE_DIRT.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState());
    public static final SurfaceBuilderConfig GRASS = new SurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(),
            Blocks.GRASS_BLOCK.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState());

}
