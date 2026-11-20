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

public class EntityFatMan extends EntityMob {
    public EntityFatMan(World world) {
        super(world);
        this.setSize(0.7F, 0.7F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("fatman.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("fatman.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("fatman.death"); }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override protected Item getDropItem() { return WeirdMobsMod.fatManRaw; }
}
