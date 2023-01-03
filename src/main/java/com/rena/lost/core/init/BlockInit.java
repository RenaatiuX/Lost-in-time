package com.rena.lost.core.init;

import com.rena.lost.LostInTime;
import com.rena.lost.common.block.*;
import com.rena.lost.common.tab.LostItemGroup;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
    public static final RegistryObject<Block>  CLADOPHLEBIS = register("cladophlebis",
            () -> new LostFlowerPlantBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement()
                    .zeroHardnessAndResistance().sound(SoundType.PLANT)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> WATER_GRASS = register("water_grass",
            () -> new DoublePlantWaterBlock(AbstractBlock.Properties.create(Material.SEA_GRASS).doesNotBlockMovement()
                    .zeroHardnessAndResistance().sound(SoundType.PLANT)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> WATER_GRASS_2 = register("water_grass_2",
            () -> new DoublePlantWaterBlock(AbstractBlock.Properties.create(Material.SEA_GRASS).doesNotBlockMovement()
                    .zeroHardnessAndResistance().sound(SoundType.PLANT)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> DUCKWEED = BLOCK.register("duckweed",
            () -> new DuckWeedBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement()
                    .zeroHardnessAndResistance().sound(SoundType.LILY_PADS).notSolid()));
    public static final RegistryObject<Block> MUD = register("mud",
            () -> new MudBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.BROWN)
                    .hardnessAndResistance(0.6F).sound(SoundType.GROUND)), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> HORIZONTAL_LOG = register("horizontal_log",
            () -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(2.0F).sound(SoundType.WOOD)), LostItemGroup.LOST_TAB);

    public static final RegistryObject<Block> SEIROCRINUS = register("seirocrinus",
            () -> new SeirocrinusTopBlock(AbstractBlock.Properties.create(Material.OCEAN_PLANT)
                    .doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)
                    , Direction.DOWN, SeirocrinusTopBlock.SHAPE, true, 0.14D), LostItemGroup.LOST_TAB);
    public static final RegistryObject<Block> SEIROCRINUS_PLANT = BLOCK.register("seirocrinus_plant",
            () -> new SeirocrinusBlock(AbstractBlock.Properties.create(Material.OCEAN_PLANT)
                    .doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.WET_GRASS)
                    , Direction.DOWN, VoxelShapes.fullCube(), true));

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
    public static final RegistryObject<Block> MESOZOIC_PORTAL_BLOCK = BLOCK.register("mesozoic_portal_block",
            () -> new LostPortalBlock(AbstractBlock.Properties.create(Material.PORTAL).hardnessAndResistance(-1F)
                    .doesNotBlockMovement().tickRandomly().setLightLevel((state) -> 11)));
    public static final RegistryObject<Block> APERTOTEMPORALIS_EGG = register("apertotemporalis_egg",
            () -> new ApertotemporalisEggBlock(AbstractBlock.Properties.create(Material.DRAGON_EGG, MaterialColor.SAND)
                    .hardnessAndResistance(0.5F).sound(SoundType.METAL).tickRandomly().notSolid()), LostItemGroup.LOST_TAB);

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
