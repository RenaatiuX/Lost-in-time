package com.rena.lost;


import com.rena.lost.client.model.LITModels;
import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import com.rena.lost.core.EntityInit;
import com.rena.lost.core.ItemInit;
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

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LostInTime.MOD_ID)
public class LostInTime
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "lost";
    public static final String ARMOR_DIR = MOD_ID + ":textures/armor/";

    public LostInTime() {
        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        GeckoLib.initialize();

        ItemInit.ITEMS.register(bus);
        EntityInit.ENTITY_TYPES.register(bus);

        bus.addListener(this::setup);
        // Register the doClientStuff method for modloading
        bus.addListener(this::doClientStuff);
        bus.addListener(this::registerEntityAttributes);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.APERTOTEMPORALIS_ENTITY.get(), ApertotemporalisEntity.createAttributes().create());
    }

}
