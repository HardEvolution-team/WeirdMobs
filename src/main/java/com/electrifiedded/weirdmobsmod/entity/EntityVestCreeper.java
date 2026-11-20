package com.electrifiedded.weirdmobsmod.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/** Creeper with a TNT vest: short fuse (30), radius 9, smokes/sparks while ignited, drops TNT. Gunpowder makes the blast bigger. */
public class EntityVestCreeper extends EntityCreeper {
    private int radius = 9;

    public EntityVestCreeper(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        applyCreeperParams();
    }

    /** EntityCreeper keeps fuseTime/explosionRadius private; set them through its own NBT keys. */
    private void applyCreeperParams() {
        NBTTagCompound t = new NBTTagCompound();
        super.writeEntityToNBT(t);
        t.setShort("Fuse", (short) 30);
        t.setByte("ExplosionRadius", (byte) radius);
        super.readEntityFromNBT(t);
    }

    @Override public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("ExplosionRadius")) radius = nbt.getByte("ExplosionRadius");
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
    }

    @Override public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote && this.isEntityAlive() && this.getCreeperFlashIntensity(0.0F) > 0.0F) {
            boolean late = this.getCreeperFlashIntensity(0.0F) > 0.66F;
            int n = late ? 100 : 10;
            for (int l = 0; l < n; ++l)
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + this.rand.nextGaussian() * 0.1D,
                        this.posY + this.height + this.rand.nextGaussian() * 0.1D, this.posZ + this.rand.nextGaussian() * 0.1D, 0, 0, 0);
            if (late) this.world.spawnParticle(EnumParticleTypes.LAVA, this.posX, this.posY + this.height, this.posZ, 0, 10.0D, 0);
        }
    }

    @Override protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == Items.GUNPOWDER) {
            if (!this.world.isRemote && radius < 15) {
                radius++;
                applyCreeperParams();
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
            }
            player.swingArm(hand);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override protected ResourceLocation getLootTable() { return null; }

    @Override protected Item getDropItem() { return Item.getItemFromBlock(Blocks.TNT); }
}
