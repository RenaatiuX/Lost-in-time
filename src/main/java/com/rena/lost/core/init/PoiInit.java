package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import com.rena.lost.core.init.BlockInit;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PoiInit {

    public static final DeferredRegister<PointOfInterestType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, LostInTime.MOD_ID);

    public static final RegistryObject<PointOfInterestType> MESOZOIC_PORTAL = POI.register("mesozoic_portal",
            () -> new PointOfInterestType("mesozoic_portal", PointOfInterestType.getAllStates(BlockInit.MESOZOIC_PORTAL_BLOCK.get()), 0 ,1));
}
