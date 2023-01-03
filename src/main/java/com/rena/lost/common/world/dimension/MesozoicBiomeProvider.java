package com.rena.lost.common.world.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rena.lost.LostInTime;
import com.rena.lost.core.init.BiomeInit;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.Layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MesozoicBiomeProvider extends NetherBiomeProvider {

    public static final MapCodec<NetherBiomeProvider> PACKET_CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                            Codec.LONG.fieldOf("seed")
                                    .orElseGet(SeedBearer::giveMeSeed)
                                    .forGetter((netherProvider) -> netherProvider.seed),
                            RecordCodecBuilder.<Pair<Biome.Attributes, Supplier<Biome>>>create(
                                            (biomeAttributes) -> biomeAttributes.group(
                                                            Biome.Attributes.CODEC.fieldOf("parameters")
                                                                    .forGetter(Pair::getFirst),
                                                            Biome.BIOME_CODEC.fieldOf("biome")
                                                                    .forGetter(Pair::getSecond))
                                                    .apply(biomeAttributes, Pair::of))
                                    .listOf().fieldOf("biomes")
                                    .forGetter((netherProvider) -> netherProvider.biomeAttributes),
                            NetherBiomeProvider.Noise.CODEC.fieldOf("temperature_noise")
                                    .forGetter((netherProvider) -> netherProvider.temperatureNoise),
                            NetherBiomeProvider.Noise.CODEC.fieldOf("humidity_noise")
                                    .forGetter((netherProvider) -> netherProvider.humidityNoise),
                            NetherBiomeProvider.Noise.CODEC.fieldOf("altitude_noise")
                                    .forGetter((netherProvider) -> netherProvider.altitudeNoise),
                            NetherBiomeProvider.Noise.CODEC.fieldOf("weirdness_noise")
                                    .forGetter((netherProvider) -> netherProvider.weirdnessNoise))
                    .apply(instance, NetherBiomeProvider::new));

    public static final Codec<NetherBiomeProvider> CODEC = Codec.mapEither(DefaultBuilder.CODEC, PACKET_CODEC).xmap((either) ->
            either.map(DefaultBuilder::build, Function.identity()), (netherProvider) ->
            netherProvider.getDefaultBuilder().map(Either::<DefaultBuilder, NetherBiomeProvider>left).orElseGet(() ->
                    Either.right(netherProvider))).codec();

    private MesozoicBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, Optional<Pair<Registry<Biome>, Preset>> netherProviderPreset) {
        super(seed, biomeAttributes, netherProviderPreset);
    }

    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        return new MesozoicBiomeProvider(seed, this.biomeAttributes, this.netherProviderPreset);
    }

}
