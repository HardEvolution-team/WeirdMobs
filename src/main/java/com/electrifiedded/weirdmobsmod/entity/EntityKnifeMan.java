package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityKnifeMan extends EntityMob {
    public EntityKnifeMan(World world) {
        super(world);
        this.setSize(0.8F, 0.5F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("knifeman.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("knifeman.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("knifeman.death"); }

    @Override public void onLivingUpdate() {
        if (!this.onGround && this.motionY < 0.0D) this.motionY *= 0.8D;
        if (this.world.isRemote) {
            for (int i = 0; i < 10; ++i)
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + this.height, this.posZ, 0, 0, 0);
            this.world.spawnParticle(EnumParticleTypes.LAVA, this.posX, this.posY + this.height, this.posZ, 0, 0, 0);
        }
        super.onLivingUpdate();
    }

    @Override protected Item getDropItem() { return Items.IRON_SWORD; }

    @Override public void fall(float distance, float multiplier) {}

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }
}
