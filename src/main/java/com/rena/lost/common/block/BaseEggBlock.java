package com.rena.lost.common.block;

import com.rena.lost.common.block.util.IIncubatorEgg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class BaseEggBlock extends Block implements IIncubatorEgg {

    private static final VoxelShape ONE_EGG_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
    private static final VoxelShape MULTI_EGG_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);

    public static final IntegerProperty HATCH = BlockStateProperties.HATCH_0_2;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS_1_4;

    public BaseEggBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HATCH, 0).with(EGGS, 1));
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        this.tryTrample(worldIn, pos, entityIn, 100);
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!(entityIn instanceof ZombieEntity)) {
            this.tryTrample(worldIn, pos, entityIn, 3);
        }

        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    private void tryTrample(World worldIn, BlockPos pos, Entity trampler, int chances) {
        if (!this.canTrample(worldIn, trampler)) {
            super.onEntityWalk(worldIn, pos, trampler);
        } else {
            if (!worldIn.isRemote && worldIn.rand.nextInt(chances) == 0) {
                this.removeOneEgg(worldIn, pos, worldIn.getBlockState(pos));
            }

        }
    }

    private void removeOneEgg(World worldIn, BlockPos pos, BlockState state) {
        worldIn.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + worldIn.rand.nextFloat() * 0.2F);
        int i = state.get(EGGS);
        if (i <= 1) {
            worldIn.destroyBlock(pos, false);
        } else {
            worldIn.setBlockState(pos, state.with(EGGS, i - 1), 2);
            worldIn.playEvent(2001, pos, Block.getStateId(state));
        }

    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (hasProperHabitat(worldIn, pos)) {
            int i = state.get(HATCH);
            if (i < 2) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS,
                        0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.setBlockState(pos, state.with(HATCH, i + 1), 2);
            } else {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS,
                        0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.removeBlock(pos, false);

                for (int j = 0; j < state.get(EGGS); ++j) {
                    worldIn.playEvent(2001, pos, Block.getStateId(state));
                    AnimalEntity animal = createChildEntity(worldIn);
                    animal.setGrowingAge(-24000);
                    animal.setLocationAndAngles((double) pos.getX() + 0.3D + (double) j * 0.2D,
                            pos.getY(), (double) pos.getZ() + 0.3D, 0.0F, 0.0F);
                    worldIn.addEntity(animal);
                }
            }
        }
    }

    public static boolean hasProperHabitat(IBlockReader reader, BlockPos blockReader) {
        return reader.getBlockState(blockReader).isIn(BlockTags.SAND);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (hasProperHabitat(worldIn, pos) && !worldIn.isRemote) {
            worldIn.playEvent(2005, pos, 0);
        }
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        this.removeOneEgg(worldIn, pos, state);
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        return useContext.getItem().getItem() == this.asItem() && state.get(EGGS) < 4 || super.isReplaceable(state, useContext);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        return blockstate.matchesBlock(this) ? blockstate.with(EGGS, Math.min(4, blockstate.get(EGGS) + 1)) : super.getStateForPlacement(context);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean canTrample(World worldIn, Entity trampler) {
        for (Class<AnimalEntity> clazz : canTrampleOn()) {
            if (trampler.getClass().equals(clazz)) {
                return false;
            }
        }
        if (!(trampler instanceof LivingEntity)) {
            return false;
        }
        return trampler instanceof PlayerEntity || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, trampler);
    }

    protected abstract <T extends AnimalEntity> Class<T>[] canTrampleOn();

}
