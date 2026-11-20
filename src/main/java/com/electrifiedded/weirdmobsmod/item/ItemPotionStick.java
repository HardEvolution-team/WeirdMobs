package com.electrifiedded.weirdmobsmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;

public class ItemPotionStick extends ItemSword {
    public ItemPotionStick(ToolMaterial material) {
        super(material);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof EntityLivingBase && !player.world.isRemote) {
            EntityLivingBase l = (EntityLivingBase) entity;
            l.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 0));
            l.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 0));
            l.addPotionEffect(new PotionEffect(MobEffects.POISON, 300, 0));
        }
        return false;
    }
}
