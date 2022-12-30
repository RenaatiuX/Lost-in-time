package com.rena.lost.common.item;

import com.rena.lost.common.block.LostPortalBlock;
import com.rena.lost.core.init.BlockInit;
import com.rena.lost.core.init.DimensionInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OrbPortalItem extends Item {
    public OrbPortalItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getPlayer() != null) {
            if (context.getPlayer().world.getDimensionKey() == DimensionInit.MESOZOIC_KEY
                    || context.getPlayer().world.getDimensionKey() == World.OVERWORLD) {
                for (Direction direction : Direction.Plane.VERTICAL) {
                    BlockPos framePos = context.getPos().offset(direction);
                    if (((LostPortalBlock) BlockInit.MESOZOIC_PORTAL_BLOCK.get()).trySpawnPortal(context.getWorld(), framePos)) {
                        context.getWorld().playSound(context.getPlayer(), framePos, SoundEvents.BLOCK_PORTAL_TRIGGER,
                                SoundCategory.BLOCKS, 1.0F, 1.0F);
                        context.getItem().damageItem(1, context.getPlayer(), (player) -> {
                            player.sendBreakAnimation(context.getHand());
                        });
                        return ActionResultType.CONSUME;
                    } else return ActionResultType.FAIL;
                }
            }
        }
        return ActionResultType.FAIL;
    }
}
