package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

/** Wolf-like pet: tame with gold ingots (1/3), heal with apples, right-click to sit, give a necklace for +10 HP (sneak to take it back). */
public class EntityBigEar extends EntityTameable {
    private static final DataParameter<Boolean> NECKLACE = EntityDataManager.createKey(EntityBigEar.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(EntityBigEar.class, DataSerializers.BOOLEAN);

    public EntityBigEar(World world) {
        super(world);
        this.setSize(0.6F, 1.3F);
        this.setTamed(false);
    }

    @Override protected void initEntityAI() {
        this.aiSit = new EntityAISit(this);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        // 1.7 targeted every player, even when tamed (it attacked its owner). Only wild ones hunt players now.
        this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityPlayer.class, false, null));
    }

    @Override protected void entityInit() {
        super.entityInit();
        this.dataManager.register(NECKLACE, false);
        this.dataManager.register(ANGRY, false);
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }

    @Override protected Item getDropItem() { return WeirdMobsMod.bigEarSkin; }

    @Override public void setAttackTarget(EntityLivingBase target) {
        super.setAttackTarget(target);
        if (target == null) setAngry(false);
        else if (!this.isTamed()) setAngry(true);
    }

    @Override protected void playStepSound(BlockPos pos, Block block) { this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F); }
    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("bigear.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("bigear.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("bigear.death"); }
    @Override protected float getSoundVolume() { return 0.4F; }

    @Override public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && !this.isTamed() && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) this.setDead();
    }

    @Override public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) return false;
        Entity e = source.getTrueSource();
        if (this.aiSit != null) this.aiSit.setSitting(false);
        if (e != null && !(e instanceof EntityPlayer) && !(e instanceof EntityArrow)) amount = (amount + 1.0F) / 2.0F;
        return super.attackEntityFrom(source, amount);
    }

    @Override public boolean attackEntityAsMob(Entity target) {
        return target.attackEntityFrom(DamageSource.causeMobDamage(this), this.isTamed() ? 4.0F : 2.0F);
    }

    @Override public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(tamed ? (hasNecklace() ? 30.0D : 20.0D) : 8.0D);
    }

    @Override public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (this.isTamed()) {
            if (stack.getItem() == Items.APPLE) {
                if (!this.world.isRemote && this.getHealth() < this.getMaxHealth()) {
                    this.heal(4.0F);
                    if (!player.capabilities.isCreativeMode) stack.shrink(1);
                }
                return true;
            }
            if (this.isOwner(player)) {
                if (!this.world.isRemote) {
                    if (stack.getItem() == WeirdMobsMod.necklace && !hasNecklace()) {
                        if (!player.capabilities.isCreativeMode) stack.shrink(1);
                        setNecklace(true);
                    } else if (player.isSneaking() && hasNecklace()) {
                        setNecklace(false);
                        ItemStack n = new ItemStack(WeirdMobsMod.necklace);
                        if (!player.inventory.addItemStackToInventory(n)) player.dropItem(n, false);
                    } else {
                        this.aiSit.setSitting(!this.isSitting());
                    }
                    this.isJumping = false;
                    this.navigator.clearPath();
                    this.setAttackTarget(null);
                }
                return true;
            }
        } else if (stack.getItem() == Items.GOLD_INGOT) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            if (!this.world.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamedBy(player);
                    this.navigator.clearPath();
                    this.setAttackTarget(null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.playTameEffect(true);
                    this.world.setEntityState(this, (byte) 7);
                } else {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte) 6);
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override public int getMaxSpawnedInChunk() { return 8; }

    @Override public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setNecklace(nbt.getBoolean("Test"));
    }

    @Override public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Test", hasNecklace()); // original key name kept
    }

    public boolean isAngry() { return this.dataManager.get(ANGRY); }
    public void setAngry(boolean angry) { this.dataManager.set(ANGRY, angry); }
    public boolean hasNecklace() { return this.dataManager.get(NECKLACE); }

    public void setNecklace(boolean on) {
        this.dataManager.set(NECKLACE, on);
        if (!this.isTamed()) return;
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(on ? 30.0D : 20.0D);
        if (on) this.setHealth(this.getMaxHealth());
        else if (this.getHealth() > this.getMaxHealth()) this.setHealth(this.getMaxHealth());
    }

    @Override protected boolean canDespawn() { return !this.isTamed() && this.ticksExisted > 2400; }

    @Override public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
        if (target instanceof EntityCreeper || target instanceof EntityGhast) return false;
        if (target instanceof EntityBigEar) {
            EntityBigEar o = (EntityBigEar) target;
            if (o.isTamed() && o.getOwner() == owner) return false;
        }
        if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target)) return false;
        return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
    }

    @Override public EntityAgeable createChild(EntityAgeable other) { return null; }
}
