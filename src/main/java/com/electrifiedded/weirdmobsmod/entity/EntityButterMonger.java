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

public class EntityButterMonger extends EntityMob {
    public EntityButterMonger(World world) {
        super(world);
        this.setSize(1.0F, 1.5F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
        this.tasks.addTask(4, new EntityAIWander(this, 0.5D));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("buttermonger.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("buttermonger.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("buttermonger.death"); }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }
}
