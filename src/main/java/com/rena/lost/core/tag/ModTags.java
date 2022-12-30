package com.rena.lost.core.tag;

import com.rena.lost.LostInTime;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags {

    public static class Blocks {

        public static final Tags.IOptionalNamedTag<Block> MESOZOIC_PORTAL_FRAME = tag("mesozoic_portal_frame_block");
        private static Tags.IOptionalNamedTag<Block> tag(String name){
            return BlockTags.createOptional(new ResourceLocation(LostInTime.MOD_ID, name));
        }
        private static Tags.IOptionalNamedTag<Block> forgeTag(String name){
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }

    }

}
