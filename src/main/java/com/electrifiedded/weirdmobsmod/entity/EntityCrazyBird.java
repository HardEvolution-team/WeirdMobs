package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/** Follows iron/gold/diamonds; feeding it swaps the item for seeds and it flees players forever. */
public class EntityCrazyBird extends EntityAnimal {
    private boolean isTriggered;
    private EntityAITempt aiDiamond, aiGold, aiIron;

    public EntityCrazyBird(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    @Override protected void initEntityAI() {
        this.aiDiamond = new EntityAITempt(this, 1.2D, Items.DIAMOND, false);
        this.aiGold = new EntityAITempt(this, 1.2D, Items.GOLD_INGOT, false);
        this.aiIron = new EntityAITempt(this, 1.2D, Items.IRON_INGOT, false);
        this.tasks.addTask(0, this.aiIron);
        this.tasks.addTask(1, this.aiGold);
        this.tasks.addTask(2, this.aiDiamond);
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    private void trigger() {
        if (this.isTriggered) return;
        this.isTriggered = true;
        this.tasks.removeTask(this.aiIron);
        this.tasks.removeTask(this.aiGold);
        this.tasks.removeTask(this.aiDiamond);
        this.tasks.addTask(4, new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16.0F, 1.0D, 2.0D));
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }

    @Override protected SoundEvent getAmbientSound() { return WeirdMobsMod.sound("crazybird.living"); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return WeirdMobsMod.sound("crazybird.hurt"); }
    @Override protected SoundEvent getDeathSound() { return WeirdMobsMod.sound("crazybird.death"); }

    @Override public void writeEntityToNBT(NBTTagCompound nbt) { super.writeEntityToNBT(nbt); nbt.setBoolean("isTriggered", this.isTriggered); }
    @Override public void readEntityFromNBT(NBTTagCompound nbt) { super.readEntityFromNBT(nbt); if (nbt.getBoolean("isTriggered")) trigger(); }

    @Override public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Item i = stack.getItem();
        if (i != Items.DIAMOND && i != Items.GOLD_INGOT && i != Items.IRON_INGOT) return super.processInteract(player, hand);
        if (!this.world.isRemote) {
            trigger();
            stack.shrink(1);
            ItemStack seeds = new ItemStack(Items.WHEAT_SEEDS);
            if (stack.isEmpty()) player.setHeldItem(hand, seeds);
            else if (!player.inventory.addItemStackToInventory(seeds)) player.dropItem(seeds, false);
        }
        return true;
    }

    @Override protected void dropFewItems(boolean hit, int looting) {
        this.dropItem(Items.FEATHER, 2);
        this.dropItem(WeirdMobsMod.crazyBirdRaw, 1);
    }

    @Override public EntityAgeable createChild(EntityAgeable other) { return null; }
}
