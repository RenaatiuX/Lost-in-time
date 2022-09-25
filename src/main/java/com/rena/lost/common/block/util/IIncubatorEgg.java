package com.rena.lost.common.block.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

public interface IIncubatorEgg {


    AnimalEntity createChildEntity(World world);

    /**
     *
     * @param currentEgg
     * @param world
     * @param efficiency - has to be between 1 and 2 otherwise has to throw IllegalArgumenException
     * @return
     */
    BlockState grow(BlockState currentEgg, World world, float efficiency);

}
