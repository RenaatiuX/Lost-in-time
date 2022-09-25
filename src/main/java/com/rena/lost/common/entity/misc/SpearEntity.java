package com.rena.lost.common.entity.misc;

import com.rena.lost.LostInTime;
import com.rena.lost.core.EntityInit;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SpearEntity extends AbstractArrowEntity {

    protected static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(SpearEntity.class, DataSerializers.ITEMSTACK);

    protected boolean dealtDamage;
    protected boolean enchanted;
    protected int loyaltyLevel;
    protected int returningTicks;

    protected ResourceLocation texture = new ResourceLocation(LostInTime.MOD_ID, "textures/entity/spear/wooden_spear.png");

    public SpearEntity(EntityType<? extends SpearEntity> type, World world) {
        super(type, world);
    }

    public SpearEntity(World world, LivingEntity thrower, ItemStack item) {
        super(EntityInit.SPEAR_ENTITY.get(), thrower, world);
        setArrowStack(item);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (this.timeInGround > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getShooter();
        if ((this.dealtDamage || this.getNoClip()) && entity != null) {
            if (loyaltyLevel > 0 && !this.shouldReturnToThrower()) {
                if (!this.world.isRemote && this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1F);
                }

                this.remove();
            } else if (loyaltyLevel > 0) {
                this.setNoClip(true);
                Vector3d vector3d = new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosYEye() - this.getPosY(),
                        entity.getPosZ() - this.getPosZ());
                this.setRawPosition(this.getPosX(), this.getPosY() + vector3d.y * 0.015D * loyaltyLevel, this.getPosZ());
                if (this.world.isRemote()) {
                    this.lastTickPosY = this.getPosY();
                }

                double d0 = 0.05D * loyaltyLevel;
                this.setMotion(this.getMotion().scale(0.95D).add(vector3d.normalize().scale(d0)));
                if (this.returningTicks == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returningTicks;
            }
        }

        super.tick();
    }

    protected boolean shouldReturnToThrower() {
        Entity entity = this.getShooter();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayerEntity) || !entity.isSpectator());
    }

    @Override
    protected ItemStack getArrowStack() {
        return this.getDataManager().get(ITEM).copy();
    }

    protected void setArrowStack(final ItemStack stack) {
        this.getDataManager().set(ITEM, stack.copy());
        final ResourceLocation name = stack.getItem().getRegistryName();
        this.texture = new ResourceLocation(name.getNamespace(), "textures/entity/spear/" + name.getPath() + ".png");
        this.loyaltyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, stack);
        this.enchanted = stack.hasEffect();
    }

    @Override
    public void notifyDataManagerChange(final DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if(key.equals(ITEM)) {
            ItemStack stack = getArrowStack();
            final ResourceLocation name = stack.getItem().getRegistryName();
            this.texture = new ResourceLocation(name.getNamespace(), "textures/entity/spear/" + name.getPath() + ".png");
            this.loyaltyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, stack);
            this.enchanted = stack.hasEffect();
        }
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult raytrace) {
        Entity entity = raytrace.getEntity();
        dealtDamage = true;

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            this.setDamage(this.getDamage() + EnchantmentHelper.getModifierForCreature(this.getArrowStack(), living.getCreatureAttribute()));
        }

        Entity thrower = getShooter();
        DamageSource source = DamageSource.causeTridentDamage(this, (thrower == null) ? this : thrower);
        SoundEvent sound = SoundEvents.ITEM_TRIDENT_HIT;

        if (entity.attackEntityFrom(source, (float) this.getDamage())) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                if (thrower instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(living, thrower);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity) thrower, living);
                }
                arrowHit(living);
            }
        }

        setMotion(getMotion().mul(-0.01D, -0.1D, -0.01D));
        playSound(sound, 1.0F, 1.0F);
    }

    @Nullable
    @Override
    protected EntityRayTraceResult rayTraceEntities(Vector3d startVec, Vector3d endVec) {
        return this.dealtDamage ? null : super.rayTraceEntities(startVec, endVec);
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        Entity entity = this.getShooter();
        if (entity == null || entity.getUniqueID() == entityIn.getUniqueID()) {
            super.onCollideWithPlayer(entityIn);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Spear", 10)) {
            setArrowStack(ItemStack.read(compound.getCompound("Spear")));
        }
        dealtDamage = compound.getBoolean("damage");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Spear", getArrowStack().write(new CompoundNBT()));
        compound.putBoolean("damage", this.dealtDamage);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected SoundEvent getHitEntitySound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInRangeToRender3d(double dX, double dY, double dZ) { return true; }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexture() { return texture; }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnchanted() { return enchanted; }


}
