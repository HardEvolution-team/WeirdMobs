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
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityStickMan extends EntityAnimal {
    public EntityStickMan(World world) {
        super(world);
        this.setSize(0.7F, 1.8F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAITempt(this, 1.2D, Items.BOOK, false));
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.tasks.addTask(3, new EntityAIPanic(this, 1.5D));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("stickman.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("stickman.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("stickman.death"); }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override public boolean isBreedingItem(ItemStack stack) { return stack.getItem() == Items.BOOK; }

    @Override protected Item getDropItem() { return Items.PAPER; }

    /** 1.7 dropRareDrop: 2.5% when killed by a player. */
    @Override protected void dropLoot(boolean hit, int looting, DamageSource src) {
        super.dropLoot(hit, looting, src);
        if (hit && this.rand.nextInt(40) == 0) this.dropItem(WeirdMobsMod.stickManHead, 1);
    }

    @Override public EntityAgeable createChild(EntityAgeable other) {
        EntityStickManBaby b = new EntityStickManBaby(this.world);
        b.correctLifeTime = true;
        return b;
    }
}
