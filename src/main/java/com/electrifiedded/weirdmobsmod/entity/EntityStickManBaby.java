package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/** Grows into a Stick Man after 7000-8000 ticks; paper speeds it up. */
public class EntityStickManBaby extends EntityAnimal {
    private int lifeTime;
    private final int life;
    public boolean correctLifeTime;

    public EntityStickManBaby(World world) {
        super(world);
        this.life = this.rand.nextInt(1000) + 7000;
        this.setSize(0.25F, 1.3F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAITempt(this, 2.1D, Items.PAPER, false));
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("stickman.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("stickman.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("stickman.death"); }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @Override public void writeEntityToNBT(NBTTagCompound nbt) { super.writeEntityToNBT(nbt); nbt.setInteger("lifeTime", lifeTime); }
    @Override public void readEntityFromNBT(NBTTagCompound nbt) { super.readEntityFromNBT(nbt); lifeTime = nbt.getInteger("lifeTime"); correctLifeTime = true; }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.world.isRemote) return;
        if (this.correctLifeTime) ++this.lifeTime;
        if (this.lifeTime >= this.life) {
            EntityStickMan man = new EntityStickMan(this.world);
            man.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            ((WorldServer) this.world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 120, 0.3D, 0.3D, 0.3D, 0.0D);
            this.world.spawnEntity(man);
            this.setDead();
        }
    }

    @Override public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != Items.PAPER) return false;
        if (!this.world.isRemote) {
            ((WorldServer) this.world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX, this.posY + this.height / 2.0F, this.posZ, 10, 0.1D, 0.1D, 0.1D, 0.0D);
            this.lifeTime += 300;
            this.correctLifeTime = true;
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
        }
        return true;
    }

    @Override protected Item getDropItem() { return Items.PAPER; }

    @Override public EntityAgeable createChild(EntityAgeable other) { return null; }
}
