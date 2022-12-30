package com.rena.lost.common.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;

public class MesozoicBiomeProvider extends OverworldBiomeProvider {

    public static final Codec<OverworldBiomeProvider> CODEC = RecordCodecBuilder.create((builder) ->
            builder.group(Codec.LONG.fieldOf("seed").stable().orElseGet(SeedBearer::giveMeSeed)
                    .forGetter((overworldProvider) -> overworldProvider.seed),
                    Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", Boolean.FALSE,
                            Lifecycle.stable()).forGetter((overworldProvider) -> overworldProvider.legacyBiomes),
                    Codec.BOOL.fieldOf("large_biomes").orElse(false)
                            .stable().forGetter((overworldProvider) -> overworldProvider.largeBiomes),
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter((overworldProvider) ->
                            overworldProvider.lookupRegistry)).apply(builder, builder.stable(OverworldBiomeProvider::new)));

    public MesozoicBiomeProvider(long seed, boolean legacyBiomes, boolean largeBiomes, Registry<Biome> lookupRegistry) {
        super(seed, legacyBiomes, largeBiomes, lookupRegistry);
    }


    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        return new MesozoicBiomeProvider(seed, this.legacyBiomes, this.largeBiomes, this.lookupRegistry);
    }
}
