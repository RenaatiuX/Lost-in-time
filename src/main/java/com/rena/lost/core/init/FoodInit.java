package com.rena.lost.core.init;

import net.minecraft.item.Food;

public class FoodInit {

    public static final Food RAW_PELECANIMIMUS_MEAT = new Food.Builder()
            .hunger(3).saturation(0.3F).meat().build();
    public static final Food COOKED_PELECANIMIMUS_MEAT = new Food.Builder()
            .hunger(6).saturation(0.6F).meat().build();
    public static final Food RAW_DAKOSAURUS_MEAT = new Food.Builder()
            .hunger(3).saturation(0.6F).meat().build();
    public static final Food COOKED_DAKOSAURUS_MEAT = new Food.Builder()
            .hunger(10).saturation(0.6F).meat().build();
    public static final Food HYPSOCORMUS = new Food.Builder()
            .hunger(2).saturation(0.1F).build();
    public static final Food COOKED_HYPSOCORMUS = new Food.Builder()
            .hunger(5).saturation(0.6F).build();
    public static final Food TEPEXICHTHYS = new Food.Builder()
            .hunger(2).saturation(0.1F).build();
    public static final Food COOKED_TEPEXICHTHYS = new Food.Builder()
            .hunger(5).saturation(0.6F).build();
}
