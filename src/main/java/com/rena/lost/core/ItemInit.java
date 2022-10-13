package com.rena.lost.core;

import com.rena.lost.LostInTime;
import com.rena.lost.client.ClientISTERProvider;
import com.rena.lost.common.item.SpearItem;
import com.rena.lost.common.item.armor.ConcavenatorMaskItem;
import com.rena.lost.common.tab.LostItemGroup;
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
            ()-> new ConcavenatorMaskItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, new Item.Properties().group(LostItemGroup.LOST_TAB)));

    public static final RegistryObject<Item> WOODEN_SPEAR =ITEMS.register("wooden_spear",
            ()-> new SpearItem(ItemTier.WOOD, new Item.Properties().group(LostItemGroup.LOST_TAB)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("wooden_spear"))));
    public static final RegistryObject<Item> STONE_SPEAR =ITEMS.register("stone_spear",
            ()-> new SpearItem(ItemTier.STONE, new Item.Properties().group(LostItemGroup.LOST_TAB)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("stone_spear"))));
    public static final RegistryObject<Item> GOLD_SPEAR =ITEMS.register("golden_spear",
            ()-> new SpearItem(ItemTier.GOLD, new Item.Properties().group(LostItemGroup.LOST_TAB)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("golden_spear"))));
    public static final RegistryObject<Item> IRON_SPEAR =ITEMS.register("iron_spear",
            ()-> new SpearItem(ItemTier.IRON, new Item.Properties().group(LostItemGroup.LOST_TAB)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("iron_spear"))));
    public static final RegistryObject<Item> DIAMOND_SPEAR =ITEMS.register("diamond_spear",
            ()-> new SpearItem(ItemTier.DIAMOND, new Item.Properties().group(LostItemGroup.LOST_TAB)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("diamond_spear"))));
    public static final RegistryObject<Item> NETHERITE_SPEAR =ITEMS.register("netherite_spear",
            ()-> new SpearItem(ItemTier.NETHERITE, new Item.Properties().group(LostItemGroup.LOST_TAB)
                    .setISTER(() -> () -> ClientISTERProvider.bakeSpearISTER("netherite_spear"))));

    public static final RegistryObject<Item> ADOBE_BRICK = ITEMS.register("adobe_brick",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));
    public static final RegistryObject<Item> MUD_BALL = ITEMS.register("mud_ball",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));

    public static final RegistryObject<Item> BROWN_CLAY_BALL = ITEMS.register("brown_clay_ball",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));

    public static final RegistryObject<Item> CONCAVENATOR_HUMP = ITEMS.register("concavenator_hump",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));

    public static final RegistryObject<Item> RAW_PELECANIMIMUS_MEAT = ITEMS.register("raw_pelecanimimus_meat",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB).food(FoodInit.RAW_PELECANIMIMUS_MEAT)));
    public static final RegistryObject<Item> COOKED_PELECANIMIMUS_MEAT = ITEMS.register("cooked_pelecanimimus_meat",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB).food(FoodInit.COOKED_PELECANIMIMUS_MEAT)));

    public static final RegistryObject<Item> VOIDITE_CRYSTAL = ITEMS.register("voidite_crystal",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));
    public static final RegistryObject<Item> VOIDITE_FRAGMENT = ITEMS.register("voidite_fragment",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));
    public static final RegistryObject<Item> VOIDITE_ORB = ITEMS.register("voidite_orb",
            ()-> new Item(new Item.Properties().group(LostItemGroup.LOST_TAB)));
}
