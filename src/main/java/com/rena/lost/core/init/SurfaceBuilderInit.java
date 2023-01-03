package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import com.rena.lost.common.world.gen.surfacebuilder.biomesurface.WetlandSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SurfaceBuilderInit {

    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, LostInTime.MOD_ID);

    public static final RegistryObject<SurfaceBuilder<SurfaceBuilderConfig>> WETLAND = SURFACE_BUILDERS.register("wetland",
            () -> new WetlandSurfaceBuilder(SurfaceBuilderConfig.CODEC));


}
