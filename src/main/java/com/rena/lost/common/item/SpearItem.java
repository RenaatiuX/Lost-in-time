package com.rena.lost.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rena.lost.common.entity.misc.SpearEntity;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class SpearItem extends TieredItem implements IVanishable {

    private final Multimap<Attribute, AttributeModifier> spearAttributes;

    public SpearItem(IItemTier tier, Properties properties) {
        super(tier, properties);
        // item properties
        ImmutableMultimap.Builder<Attribute, AttributeModifier> mapBuilder = ImmutableMultimap.builder();
        mapBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 2.0D + tier.getAttackDamage(), AttributeModifier.Operation.ADDITION));
        mapBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
        this.spearAttributes = mapBuilder.build();

    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entityLiving;

        int useDuration = getUseDuration(stack) - timeLeft;
        if (useDuration < 10) {
            return;
        }

        if (!worldIn.isRemote()) {
            throwSpear(worldIn, player, stack);
        }

        player.addStat(Stats.ITEM_USED.get(this));
    }

    protected void throwSpear(final World world, final PlayerEntity thrower, final ItemStack stack) {
        stack.damageItem(1, thrower, e -> e.sendBreakAnimation(thrower.getActiveHand()));
        SpearEntity spear = new SpearEntity(world, thrower, stack);
        spear.setDirectionAndMovement(thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 2.25F, 1.0F);
        // set pickup status and remove the itemstack
        if (thrower.abilities.isCreativeMode) {
            spear.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        } else {
            thrower.inventory.deleteStack(stack);
        }
        world.addEntity(spear);
        world.playMovingSound(null, spear, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getDamage() >= stack.getMaxDamage() - 1) {
            return ActionResult.resultFail(stack);
        }
        player.setActiveHand(hand);
        return ActionResult.resultConsume(stack);
    }

    @Override
    public boolean hasEffect(final ItemStack stack) {
        return super.hasEffect(stack) || stack.getOrCreateChildTag("effect").contains("effect");
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        final CompoundNBT nbt = stack.getOrCreateChildTag("effect").copy();
        if (nbt.contains("effect")) {
            nbt.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(nbt.getString("effect")))));
            target.addPotionEffect(EffectInstance.read(nbt));
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if ((double) state.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(2, entityLiving, (entity) -> {
                entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
            });
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.spearAttributes : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.LOYALTY;
    }

    @Override
    public int getItemEnchantability() {
        return Math.max(1, super.getItemEnchantability() / 2);
    }

}
