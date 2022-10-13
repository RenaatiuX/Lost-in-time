package com.rena.lost.core;

import com.rena.lost.LostInTime;
import com.rena.lost.common.block.MudBlock;
import com.rena.lost.common.block.WaterPlantBlock;
import com.rena.lost.common.tab.LostItemGroup;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Abs;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, LostInTime.MOD_ID);

    public static final RegistryObject<Block> ADOBE_BRICKS = register("adobe_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.ADOBE)
                    .setRequiresTool().hardnessAndResistance(1.0F, 3.0F)), LostItemGroup.LOST_TAB);

    public static final RegistryObject<Block> BROWN_CLAY = register("brown_clay",
            () -> new Block(AbstractBlock.Properties.create(Material.CLAY, MaterialColor.BROWN)
                    .hardnessAndResistance(0.6F).sound(SoundType.GROUND)), LostItemGroup.LOST_TAB);

    public static final RegistryObject<Block> WEICHSELIA_PLANT = register("weichselia_plant",
            () -> new TallFlowerBlock(AbstractBlock.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement()
                    .zeroHardnessAndResistance().sound(SoundType.PLANT)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> ARCHAEFRUCTUS = register("archaefructus",
            () -> new WaterPlantBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement()
                    .zeroHardnessAndResistance().sound(SoundType.PLANT)), LostItemGroup.LOST_TAB);

    public static final RegistryObject<Block> MUD = register("mud",
            () -> new MudBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.BROWN)
                    .hardnessAndResistance(0.6F).sound(SoundType.GROUND)), LostItemGroup.LOST_TAB);

    public static final RegistryObject<Block> CHISELED_PURPLE_BRICKS = register("chiseled_purple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.PURPLE)
                    .setRequiresTool().hardnessAndResistance(1.5F, 6.0F)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> POLISHED_PURPLE_BRICKS = register("polished_purple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.PURPLE)
                    .setRequiresTool().hardnessAndResistance(1.5F, 6.0F)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> PURPLE_BRICKS = register("purple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.PURPLE)
                    .setRequiresTool().hardnessAndResistance(1.5F, 6.0F)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> PURPLE_BRICKS_PILLAR = register("purple_bricks_pillar",
            () -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.PURPLE)
                    .setRequiresTool().hardnessAndResistance(1.5F, 6.0F)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> CRACKED_PURPLE_BRICKS = register("cracked_purple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.PURPLE)
                    .setRequiresTool().hardnessAndResistance(1.5F, 6.0F)), LostItemGroup.LOST_TAB);

    public static final RegistryObject<Block> VOIDITE_ORE = register("voidite_ore",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool()
                    .hardnessAndResistance(3.0F, 3.0F).harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> VOIDITE_BLOCK = register("voidite_block",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool()
                    .hardnessAndResistance(3.0F, 3.0F).harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)), LostItemGroup.LOST_TAB);

    public static final <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, ItemGroup tab){
        return register(name, blockSupplier, b -> new BlockItem(b, new Item.Properties().group(tab)));
    }

    public static final <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Function<Block, Item> item){
        RegistryObject<T> block = BLOCK.register(name, blockSupplier);
        ItemInit.ITEMS.register(name,  () -> item.apply(block.get()));
        return block;
    }

    public static RegistryObject<Block> registerBlock(final String name, Supplier<Block> block)
    {
        return BLOCK.register(name, block);
    }

}
