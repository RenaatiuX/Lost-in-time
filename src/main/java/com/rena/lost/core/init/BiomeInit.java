package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeInit {

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, LostInTime.MOD_ID);

    public static final RegistryKey<Biome> WETLAND = register("wetland");

    public static void toDictionary() {
        BiomeDictionary.addTypes(WETLAND, BiomeDictionary.Type.WET, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH, BiomeDictionary.Type.WATER);
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(LostInTime.MOD_ID, name);
    }

    private static RegistryKey<Biome> register(String name) {
        BIOMES.register(name, BiomeMaker::makeVoidBiome);
        return RegistryKey.getOrCreateKey(Registry.BIOME_KEY, name(name));
    }
}
