package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/** Kicks up block particles; standing still for 5 s turns the grass below into dirt. */
public class EntitySpinningFeets extends EntityAnimal {
    private double lx, ly, lz;
    private int waitingTime;

    public EntitySpinningFeets(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(3, new EntityAIPanic(this, 1.2D));
        this.tasks.addTask(3, new EntityAILookIdle(this));
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("spinningfeets.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("spinningfeets.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("spinningfeets.death"); }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        BlockPos below = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 0.2D), MathHelper.floor(this.posZ));
        IBlockState state = this.world.getBlockState(below);
        if (this.world.isRemote) {
            if (this.onGround && !this.inWater && state.getMaterial() != Material.AIR) {
                for (int a = 0; a < 2; ++a)
                    this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                            this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D,
                            this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D,
                            Block.getStateId(state));
            }
            if (this.inWater) {
                for (int a = 0; a < 2; ++a)
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
                            this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D,
                            this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 0, 0, 0);
            }
            return;
        }
        if (this.lx == this.posX && this.ly == this.posY && this.lz == this.posZ) {
            if (++this.waitingTime >= 100 && state.getBlock() == Blocks.GRASS) this.world.setBlockState(below, Blocks.DIRT.getDefaultState());
        } else {
            this.waitingTime = 0;
        }
        this.lx = this.posX;
        this.ly = this.posY;
        this.lz = this.posZ;
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override public EntityAgeable createChild(EntityAgeable other) { return null; }
}
