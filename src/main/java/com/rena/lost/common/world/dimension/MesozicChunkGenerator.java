package com.rena.lost.common.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class MesozicChunkGenerator extends NoiseChunkGenerator {

    public static final Codec<MesozicChunkGenerator> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                            BiomeProvider.CODEC.fieldOf("biome_source")
                                    .forGetter((chunkGenerator) -> chunkGenerator.biomeProvider),
                            Codec.LONG.fieldOf("seed").stable()
                                    .orElseGet(SeedBearer::giveMeSeed)
                                    .forGetter((chunkGenerator) -> chunkGenerator.field_236084_w_),
                            DimensionSettings.DIMENSION_SETTINGS_CODEC.fieldOf("settings")
                                    .forGetter((chunkGenerator) -> chunkGenerator.field_236080_h_))
                    .apply(instance, instance.stable(MesozicChunkGenerator::new)));

    public MesozicChunkGenerator(BiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> dimensionSettingsSupplier) {
        super(biomeProvider, seed, dimensionSettingsSupplier);
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ChunkGenerator func_230349_a_(long seed) {
        return new MesozicChunkGenerator(this.biomeProvider.getBiomeProvider(seed), seed, this.field_236080_h_);
    }
}
