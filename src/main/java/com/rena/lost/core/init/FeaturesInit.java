package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FeaturesInit {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, LostInTime.MOD_ID);




    public static final class ConfiguredFeatures {

        public static final ConfiguredFeature<?, ?> WEICHSELIA = Feature.RANDOM_PATCH.withConfiguration(
                new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(BlockInit.WEICHSELIA_PLANT.get().getDefaultState()),
                        new DoublePlantBlockPlacer()).tries(32).preventProjection().build()
        );

        public static final ConfiguredFeature<?, ?> COAL_ORE = Feature.ORE.withConfiguration(
                new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, Blocks.COAL_ORE.getDefaultState(), 17)
        );
        public static final ConfiguredFeature<?, ?> IRON_ORE = Feature.ORE.withConfiguration(
                new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, Blocks.IRON_ORE.getDefaultState(), 9)
        );
        public static final ConfiguredFeature<?, ?> GOLD_ORE = Feature.ORE.withConfiguration(
                new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, Blocks.GOLD_ORE.getDefaultState(), 9)
        );
        public static final ConfiguredFeature<?, ?> DIAMOND_ORE = Feature.ORE.withConfiguration(
                new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, Blocks.DIAMOND_ORE.getDefaultState(), 8)
        );

    }

    public static void registerConfiguredFeatures() {
            register("weichselia", ConfiguredFeatures.WEICHSELIA.withPlacement(Features.Placements.VEGETATION_PLACEMENT)
                    .withPlacement(Features.Placements.VEGETATION_PLACEMENT)
                    .withPlacement(Features.Placements.FLOWER_TALL_GRASS_PLACEMENT)
                    .square()
                    .withPlacement(Placement.COUNT_NOISE
                            .configure(new NoiseDependant(-0.8D, 0, 7))));
    }

    private static <FC extends IFeatureConfig> void register(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(LostInTime.MOD_ID, name), feature);
    }
}
