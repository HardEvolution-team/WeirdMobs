package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityTerrorTrickster extends EntityMob {
    private static final DataParameter<Boolean> HOSTILE = EntityDataManager.createKey(EntityTerrorTrickster.class, DataSerializers.BOOLEAN);

    public EntityTerrorTrickster(World world) {
        super(world);
        this.setSize(1.3F, 1.3F);
    }

    @Override protected void entityInit() { super.entityInit(); this.dataManager.register(HOSTILE, false); }

    /** Synced to the client (was a plain field in 1.7, so the hostile texture never showed on servers). */
    public boolean isHostile() { return this.dataManager.get(HOSTILE); }

    @Override protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, true));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.tasks.addTask(3, new EntityAIWander(this, 0.5D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("terrortrickster.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("terrortrickster.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("terrortrickster.death"); }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && !isHostile() && this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.dataManager.set(HOSTILE, true);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        }
    }
}
