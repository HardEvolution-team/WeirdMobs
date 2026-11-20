package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPotionStick extends EntityMob {
    public EntityPotionStick(World world) {
        super(world);
        this.setSize(0.3F, 1.4F); // 0.08 wide in 1.7 - nearly impossible to hit in 1.12
    }

    @Override protected void initEntityAI() {
        // 1.7 used the legacy (non-task) mob AI: wander + chase the nearest player.
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("potionstick.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("potionstick.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("potionstick.death"); }

    @Override protected Item getDropItem() { return WeirdMobsMod.potionStick; }

    @Override public boolean isPotionApplicable(PotionEffect effect) { return false; }

    @Override public boolean attackEntityAsMob(Entity e) {
        boolean hit = super.attackEntityAsMob(e);
        if (hit && e instanceof EntityLivingBase) {
            EnumDifficulty d = this.world.getDifficulty();
            int b = d == EnumDifficulty.NORMAL ? 11 : d == EnumDifficulty.HARD ? 31 : 0;
            if (b > 0 && !(e instanceof EntityPlayer && ((EntityPlayer) e).capabilities.isCreativeMode)) {
                EntityLivingBase l = (EntityLivingBase) e;
                l.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, b * 20, 0));
                l.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, b * 20, 0));
                l.addPotionEffect(new PotionEffect(MobEffects.POISON, b * 20, 0));
            }
        }
        return hit;
    }
}
