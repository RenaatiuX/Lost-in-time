package com.rena.lost.client.event;

import com.rena.lost.LostInTime;
import com.rena.lost.client.render.SpearRenderer;
import com.rena.lost.client.render.entities.ApertotemporalisRender;
import com.rena.lost.common.item.armor.ConcavenatorMaskItem;
import com.rena.lost.core.EntityInit;
import com.rena.lost.core.ItemInit;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LostInTime.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        armorModel();
        modelProperties();
        entityRenderer();
    }

    private static void armorModel(){
        ConcavenatorMaskItem.initArmorModel();
    }
    private static void modelProperties(){
        ItemModelsProperties.registerProperty(ItemInit.WOODEN_SPEAR.get(), new ResourceLocation("throwing"), (item, world, entity) ->
                (entity != null && entity.isHandActive() && entity.getActiveItemStack() == item) ? 1.0F : 0.0F);
    }

    private static void entityRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.SPEAR_ENTITY.get(), SpearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.APERTOTEMPORALIS_ENTITY.get(), ApertotemporalisRender::new);
    }

    private static void registerEntityRenderer(){

    }
}
