package com.rena.lost.common.entity.goal;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.GameRules;

import java.util.Random;

public class MateGoal extends BreedGoal {

    private final BaseSemiAquaticEntity semiAquatic;

    MateGoal(BaseSemiAquaticEntity semiAquatic, double speedIn) {
        super(semiAquatic, speedIn);
        this.semiAquatic = semiAquatic;
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute() && !this.semiAquatic.hasEgg();
    }

    @Override
    protected void spawnBaby() {
        ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
        if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
            serverplayerentity = this.targetMate.getLoveCause();
        }

        if (serverplayerentity != null) {
            serverplayerentity.addStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, this.animal);
        }

        this.semiAquatic.setHasEgg(true);
        this.animal.resetInLove();
        this.targetMate.resetInLove();
        Random random = this.animal.getRNG();
        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), random.nextInt(7) + 1));
        }
    }
}
