package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import com.rena.lost.common.entity.aquatic.ApertotemporalisEntity;
import com.rena.lost.common.entity.aquatic.DakosaurusEntity;
import com.rena.lost.common.entity.misc.SpearEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInit {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, LostInTime.MOD_ID);

    public static final RegistryObject<EntityType<SpearEntity>> SPEAR_ENTITY = ENTITY_TYPES.register("spear",
            ()-> EntityType.Builder.<SpearEntity>create(SpearEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).setTrackingRange(4).setUpdateInterval(20).build("spear"));


    public static final RegistryObject<EntityType<ApertotemporalisEntity>> APERTOTEMPORALIS_ENTITY = ENTITY_TYPES.register("apertotemporalis",
            ()-> EntityType.Builder.<ApertotemporalisEntity>create(ApertotemporalisEntity::new, EntityClassification.WATER_AMBIENT).size(1.2F, 0.4F).trackingRange(10).build("apertotemporalis"));
    public static final RegistryObject<EntityType<DakosaurusEntity>> DAKOSAURUS_ENTITY = ENTITY_TYPES.register("dakosaurus",
            ()-> EntityType.Builder.<DakosaurusEntity>create(DakosaurusEntity::new, EntityClassification.WATER_CREATURE).size(1.8F, 1.2F).build("dakosaurus"));
}
