package com.electrifiedded.weirdmobsmod.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/** Spider (climbs, leaps, hostile in the dark) that ignites and explodes when within 4 blocks of its target. */
public class EntitySpiderCreeper extends EntitySpider {
    private static final int FUSE = 30;
    private int timeSinceIgnited;
    private boolean played;

    public EntitySpiderCreeper(World world) {
        super(world);
        this.setSize(1.4F, 0.9F);
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D); // 0.8 in 1.7 (old AI scaled it down)
    }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        EntityLivingBase target = this.getAttackTarget();
        if (this.world.isRemote || target == null || !target.isEntityAlive()) return;
        if (this.getDistance(target) < 4.0F) {
            if (!this.played) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
                this.played = true;
            }
            ((net.minecraft.world.WorldServer) this.world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    this.posX, this.posY + 1.0D, this.posZ, 20, 0.3D, 0.1D, 0.3D, 0.0D);
            if (++this.timeSinceIgnited >= FUSE) {
                boolean grief = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this);
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F, grief);
                this.setDead();
            }
        } else {
            this.timeSinceIgnited = 0;
            this.played = false;
        }
    }

    @Override protected ResourceLocation getLootTable() { return null; }

    @Override protected void dropFewItems(boolean hit, int looting) {
        int n = this.rand.nextInt(3 + looting);
        for (int i = 0; i < n; ++i) this.dropItem(Items.STRING, 1);
        this.dropItem(Items.GUNPOWDER, 1);
        if (hit && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + looting) > 0)) this.dropItem(Items.SPIDER_EYE, 1);
    }
}
