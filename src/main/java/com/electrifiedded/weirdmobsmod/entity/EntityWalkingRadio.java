package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/** Dies (with a sound) in water; drops a random music disc. */
public class EntityWalkingRadio extends EntityAnimal {
    private static final Item[] RECORDS = {Items.RECORD_STRAD, Items.RECORD_CAT, Items.RECORD_FAR, Items.RECORD_CHIRP,
            Items.RECORD_MALL, Items.RECORD_MELLOHI, Items.RECORD_STAL, Items.RECORD_WAIT, Items.RECORD_WARD,
            Items.RECORD_11, Items.RECORD_13};
    private boolean hasPlayed;

    public EntityWalkingRadio(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.isInWater() && this.isEntityAlive()) {
            if (!this.hasPlayed) {
                SoundEvent s = WeirdMobsMod.sound("walkingradio.water");
                if (s != null) this.playSound(s, 1.0F, 1.0F);
                this.hasPlayed = true;
            }
            this.attackEntityFrom(DamageSource.DROWN, Float.MAX_VALUE);
        }
    }

    @Override protected Item getDropItem() { return RECORDS[this.rand.nextInt(RECORDS.length)]; }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("walkingradio.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("walkingradio.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("walkingradio.death"); }

    @Override public EntityAgeable createChild(EntityAgeable other) { return null; }
}
