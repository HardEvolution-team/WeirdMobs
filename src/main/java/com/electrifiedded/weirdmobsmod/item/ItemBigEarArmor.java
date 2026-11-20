package com.electrifiedded.weirdmobsmod.item;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/** Big Ear armor set and the Stick Man Head (same behaviour, different texture). */
public class ItemBigEarArmor extends ItemArmor {
    public ItemBigEarArmor(ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, 0, slot);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (getArmorMaterial() == WeirdMobsMod.stickManArmor) return "weirdmobsmod:textures/models/armor/stickmanarmor.png";
        return slot == EntityEquipmentSlot.LEGS ? "weirdmobsmod:textures/models/armor/bigeararmor_2.png"
                : "weirdmobsmod:textures/models/armor/bigeararmor_1.png";
    }
}
