package com.rena.lost.common.events;

import com.rena.lost.LostInTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LostInTime.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerForgeEvents {

    public void getCriticalHit(CriticalHitEvent event){

    }

    public static void onCriticalHit(PlayerEntity player, Entity target, float damageModifier, boolean vanillaCritical){

    }

}
