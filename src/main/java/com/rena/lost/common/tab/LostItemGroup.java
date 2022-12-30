package com.rena.lost.common.tab;

import com.rena.lost.core.init.ItemInit;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class LostItemGroup {

    public static final ItemGroup LOST_TAB = new ItemGroup("lostGroup") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.CONCAVENATOR_MASK.get());
        }
    };

}
