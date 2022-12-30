package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import com.rena.lost.common.world.dimension.MesozicChunkGenerator;
import com.rena.lost.common.world.dimension.MesozoicBiomeProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class DimensionInit {

    public static final RegistryKey<World> MESOZOIC_KEY = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
            new ResourceLocation(LostInTime.MOD_ID, "mesozoic"));
    public static final RegistryKey<DimensionType> MESOZOIC_TYPE =
            RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, MESOZOIC_KEY.getRegistryName());

    private static ResourceLocation name(String name) {
        return new ResourceLocation(LostInTime.MOD_ID, name);
    }
    public static void registerDimensionStuff() {
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, name("chunk_generator"), MesozicChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_PROVIDER_CODEC, name("biome_provider"), MesozoicBiomeProvider.CODEC);
    }
}
