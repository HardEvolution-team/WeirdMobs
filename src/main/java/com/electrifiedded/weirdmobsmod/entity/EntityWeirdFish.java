package com.electrifiedded.weirdmobsmod.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Items;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/** Shared squid-like swimming logic of Big Fish and Fat Fish (1.7 copied EntitySquid twice). */
public abstract class EntityWeirdFish extends EntityWaterMob {
    public float fishPitch, prevFishPitch, fishYaw, prevFishYaw, fishRotation, prevFishRotation, tentacleAngle, lastTentacleAngle;
    private float randomMotionSpeed, rotationVelocity, rotateSpeed;
    private float randomMotionVecX, randomMotionVecY, randomMotionVecZ;

    protected EntityWeirdFish(World world) {
        super(world);
        this.setSize(0.95F, 0.95F);
        this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }

    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected float getSoundVolume() { return 0.4F; }
    @Override protected boolean canTriggerWalking() { return false; }

    @Override protected void dropFewItems(boolean hit, int looting) {
        int j = this.rand.nextInt(3 + looting) + 1;
        for (int k = 0; k < j; ++k) this.dropItem(Items.FISH, 1);
    }

    @Override public boolean isInWater() {
        return this.world.handleMaterialAcceleration(this.getEntityBoundingBox().grow(0.0D, -0.6D, 0.0D), Material.WATER, this);
    }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        this.prevFishPitch = this.fishPitch;
        this.prevFishYaw = this.fishYaw;
        this.prevFishRotation = this.fishRotation;
        this.lastTentacleAngle = this.tentacleAngle;
        this.fishRotation += this.rotationVelocity;
        if (this.fishRotation > (float) (Math.PI * 2)) {
            this.fishRotation -= (float) (Math.PI * 2);
            if (this.rand.nextInt(10) == 0) this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
        }
        if (this.isInWater()) {
            if (this.fishRotation < (float) Math.PI) {
                float f = this.fishRotation / (float) Math.PI;
                this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
                if (f > 0.75F) {
                    this.randomMotionSpeed = 1.0F;
                    this.rotateSpeed = 1.0F;
                } else {
                    this.rotateSpeed *= 0.8F;
                }
            } else {
                this.tentacleAngle = 0.0F;
                this.randomMotionSpeed *= 0.9F;
                this.rotateSpeed *= 0.99F;
            }
            if (!this.world.isRemote) {
                this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
                this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
                this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
            }
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.renderYawOffset += (-((float) MathHelper.atan2(this.motionX, this.motionZ)) * (180F / (float) Math.PI) - this.renderYawOffset) * 0.1F;
            this.rotationYaw = this.renderYawOffset;
            this.fishYaw += (float) Math.PI * this.rotateSpeed * 1.5F;
            this.fishPitch += (-((float) MathHelper.atan2(f, this.motionY)) * (180F / (float) Math.PI) - this.fishPitch) * 0.1F;
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.fishRotation)) * (float) Math.PI * 0.25F;
            if (!this.world.isRemote) {
                this.motionX = 0.0D;
                this.motionY -= 0.08D;
                this.motionY *= 0.98D;
                this.motionZ = 0.0D;
            }
            this.fishPitch = (float) (this.fishPitch + (-90.0F - this.fishPitch) * 0.02D);
        }
    }

    @Override public void travel(float strafe, float vertical, float forward) {
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    @Override protected void updateAITasks() {
        super.updateAITasks();
        if (this.idleTime > 100) {
            this.randomMotionVecX = this.randomMotionVecY = this.randomMotionVecZ = 0.0F;
        } else if (this.rand.nextInt(50) == 0 || !this.inWater
                || this.randomMotionVecX == 0.0F && this.randomMotionVecY == 0.0F && this.randomMotionVecZ == 0.0F) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            this.randomMotionVecX = MathHelper.cos(f) * 0.2F;
            this.randomMotionVecY = -0.1F + this.rand.nextFloat() * 0.2F;
            this.randomMotionVecZ = MathHelper.sin(f) * 0.2F;
        }
    }

    @Override public boolean getCanSpawnHere() {
        return this.posY > 45.0D && this.posY < 63.0D && super.getCanSpawnHere();
    }
}
