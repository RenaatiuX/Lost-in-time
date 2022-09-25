package com.rena.lost.core;

import com.rena.lost.LostInTime;
import com.rena.lost.client.ClientISTERProvider;
import com.rena.lost.common.item.SpearItem;
import com.rena.lost.common.item.armor.ConcavenatorMaskItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LostInTime.MOD_ID);

    public static final RegistryObject<Item> CONCAVENATOR_MASK =ITEMS.register("concavenator_mask",
            ()-> new ConcavenatorMaskItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));

    public static final RegistryObject<Item> WOODEN_SPEAR =ITEMS.register("wooden_spear",
            ()-> new SpearItem(ItemTier.WOOD, new Item.Properties().group(ItemGroup.COMBAT)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("wooden_spear"))));

    public static final RegistryObject<Item> ADOBE_BRICK = ITEMS.register("adobe_brick",
            ()-> new Item(new Item.Properties().group(ItemGroup.COMBAT)));
    public static final RegistryObject<Item> MUD_BALL = ITEMS.register("mud_ball",
            ()-> new Item(new Item.Properties().group(ItemGroup.COMBAT)));
}
