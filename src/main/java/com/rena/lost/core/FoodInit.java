package com.rena.lost.core;

import net.minecraft.item.Food;

public class FoodInit {

    public static final Food RAW_PELECANIMIMUS_MEAT = new Food.Builder()
            .hunger(3).saturation(0.3F).meat().build();
    public static final Food COOKED_PELECANIMIMUS_MEAT = new Food.Builder()
            .hunger(6).saturation(0.6F).meat().build();
}
