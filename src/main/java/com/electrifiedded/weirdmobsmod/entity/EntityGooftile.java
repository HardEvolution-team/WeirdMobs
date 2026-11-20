package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityGooftile extends EntityAnimal {
    public EntityGooftile(World world) {
        super(world);
        this.setSize(1.0F, 1.5F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("gooftile.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("gooftile.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("gooftile.death"); }

    @Override public EntityAgeable createChild(EntityAgeable other) { return null; }
}
