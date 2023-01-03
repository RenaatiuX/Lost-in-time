package com.rena.lost;


import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import com.rena.lost.common.entity.aquatic.DakosaurusEntity;
import com.rena.lost.core.init.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LostInTime.MOD_ID)
public class LostInTime {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "lost";
    public static final String ARMOR_DIR = MOD_ID + ":textures/armor/";

    public LostInTime() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::doClientStuff);
        bus.addListener(this::registerEntityAttributes);

        GeckoLib.initialize();

        MinecraftForge.EVENT_BUS.register(this);

        BiomeInit.BIOMES.register(bus);
        BlockInit.BLOCK.register(bus);
        ItemInit.ITEMS.register(bus);
        EntityInit.ENTITY_TYPES.register(bus);
        PoiInit.POI.register(bus);
        FeaturesInit.FEATURES.register(bus);
        SurfaceBuilderInit.SURFACE_BUILDERS.register(bus);

    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FeaturesInit.registerConfiguredFeatures();
            BiomeInit.toDictionary();
            DimensionInit.registerDimensionStuff();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.APERTOTEMPORALIS_ENTITY.get(), ApertotemporalisEntity.createAttributes().create());
        event.put(EntityInit.DAKOSAURUS_ENTITY.get(), DakosaurusEntity.createAttributes().create());
    }

}
