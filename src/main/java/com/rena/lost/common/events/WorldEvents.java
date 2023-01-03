package com.rena.lost.common.events;

import com.rena.lost.LostInTime;
import com.rena.lost.core.init.FeaturesInit;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LostInTime.MOD_ID)
public class WorldEvents {

    @SubscribeEvent
    public static void onBiomeLoadingEvent(final BiomeLoadingEvent event){
        FeaturesInit.generateOres(event);
    }

}
