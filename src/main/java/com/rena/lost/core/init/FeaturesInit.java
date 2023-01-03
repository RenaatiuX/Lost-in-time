package com.rena.lost.core.init;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.rena.lost.LostInTime;
import com.rena.lost.common.world.gen.enums.OreType;
import com.rena.lost.common.world.gen.feature.LogFeature;
import com.rena.lost.common.world.gen.feature.SeirocrinusFeature;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class FeaturesInit {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, LostInTime.MOD_ID);

    public static final RegistryObject<Feature<NoFeatureConfig>> HORIZONTAL_LOG = FEATURES.register("horizontal_log",
            () -> new LogFeature(NoFeatureConfig.CODEC));
    public static final RegistryObject<Feature<NoFeatureConfig>> SEIROCRINUS = FEATURES.register("seirocrinus",
            () -> new SeirocrinusFeature(NoFeatureConfig.CODEC));


    public static void generateOres(final BiomeLoadingEvent event) {
        spawnOreInAllBiomes(OreType.VOIDITE_ORE, event, Dimension.THE_END.toString());
    }

    public static final class ConfiguredFeatures {

        public static final ConfiguredFeature<?, ?> WEICHSELIA = Feature.RANDOM_PATCH.withConfiguration(
                new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(BlockInit.WEICHSELIA_PLANT.get().getDefaultState()),
                        new DoublePlantBlockPlacer()).tries(16).preventProjection().build()
        );
        public static final ConfiguredFeature<?, ?> ARCHAEFRUCTUS = Feature.RANDOM_PATCH.withConfiguration(
                new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(BlockInit.ARCHAEFRUCTUS.get().getDefaultState()),
                        new SimpleBlockPlacer()).replaceable().tries(64).preventProjection().build()
        );
        public static final ConfiguredFeature<?, ?> WATER_GRASS = Feature.RANDOM_PATCH.withConfiguration(
                new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(BlockInit.WATER_GRASS.get().getDefaultState()),
                        new DoublePlantBlockPlacer()).replaceable().tries(64).preventProjection().build()
        );
        public static final ConfiguredFeature<?, ?> WATER_GRASS_2 = Feature.RANDOM_PATCH.withConfiguration(
                new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(BlockInit.WATER_GRASS_2.get().getDefaultState()),
                        new DoublePlantBlockPlacer()).replaceable().tries(64).preventProjection().build()
        );

        public static final ConfiguredFeature<?, ?> BORDER_MUD = Feature.DISK.withConfiguration(
                new SphereReplaceConfig(BlockInit.MUD.get().getDefaultState(),
                        FeatureSpread.create(4, 2), 1, ImmutableList.of(Blocks.DIRT.getDefaultState(),
                        Blocks.GRASS_BLOCK.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState()))
        );

        //Feature
        public static final ConfiguredFeature<?, ?> HORIZONTAL_LOG = FeaturesInit.HORIZONTAL_LOG.get().withConfiguration(
                IFeatureConfig.NO_FEATURE_CONFIG);
        public static final ConfiguredFeature<?, ?> SEIROCRINUS = FeaturesInit.SEIROCRINUS.get().withConfiguration(
                IFeatureConfig.NO_FEATURE_CONFIG);

    }

    public static void registerConfiguredFeatures() {
            register("weichselia", ConfiguredFeatures.WEICHSELIA.withPlacement(
                    Features.Placements.VEGETATION_PLACEMENT)
                    .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                    .count(7));

            register("archaefructus", ConfiguredFeatures.ARCHAEFRUCTUS.withPlacement(
                    Features.Placements.HEIGHTMAP_SPREAD_DOUBLE_PLACEMENT)
                    .count(7));

            register("water_grass", ConfiguredFeatures.WATER_GRASS.withPlacement(
                    Features.Placements.HEIGHTMAP_SPREAD_DOUBLE_PLACEMENT)
                    .count(50));
            register("water_grass_2", ConfiguredFeatures.WATER_GRASS_2.withPlacement(
                        Features.Placements.HEIGHTMAP_SPREAD_DOUBLE_PLACEMENT)
                .count(100));

            register("border_mud", ConfiguredFeatures.BORDER_MUD.withPlacement(
                    Features.Placements.SEAGRASS_DISK_PLACEMENT)
                    .count(5));


            //Feature
            register("horizontal_log", ConfiguredFeatures.HORIZONTAL_LOG.withPlacement(
                    Features.Placements.HEIGHTMAP_SPREAD_DOUBLE_PLACEMENT)
                    .count(2));
            register("seirocrinus", ConfiguredFeatures.SEIROCRINUS
                    .range(128).square().count(20));
    }

    private static <FC extends IFeatureConfig> void register(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(LostInTime.MOD_ID, name), feature);
    }

    private static OreFeatureConfig getEndFeatureConfig(OreType ore) {
        return new OreFeatureConfig(new BlockMatchRuleTest(Blocks.END_STONE),
                ore.getBlock().get().getDefaultState(), ore.getMaxVeinSize());
    }

    private static ConfiguredFeature<?, ?> makeOreFeature(OreType ore, String dimensionToSpawnIn) {
        OreFeatureConfig oreFeatureConfig = null;

        if(dimensionToSpawnIn.equals(Dimension.THE_END.toString())) {
            oreFeatureConfig = getEndFeatureConfig(ore);
        }

        ConfiguredPlacement<TopSolidRangeConfig> configuredPlacement = Placement.RANGE.configure(
                new TopSolidRangeConfig(ore.getMinHeight(), ore.getMinHeight(), ore.getMaxHeight()));

        return registerOreFeature(ore, oreFeatureConfig, configuredPlacement);
    }

    private static void spawnOreInAllBiomes(OreType currentOreType, final BiomeLoadingEvent event, String dimension) {
        event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                makeOreFeature(currentOreType, dimension));
    }

    private static ConfiguredFeature<?, ?> registerOreFeature(OreType ore, OreFeatureConfig oreFeatureConfig,
                                                              ConfiguredPlacement configuredPlacement) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, ore.getBlock().get().getRegistryName(),
                Feature.ORE.withConfiguration(oreFeatureConfig).withPlacement(configuredPlacement)
                        .square().count(ore.getVeinsPerChunk()));
    }
}
