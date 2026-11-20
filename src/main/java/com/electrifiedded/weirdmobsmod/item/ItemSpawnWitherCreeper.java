package com.electrifiedded.weirdmobsmod.item;

import com.electrifiedded.weirdmobsmod.entity.EntityWitherCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemSpawnWitherCreeper extends Item {
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hx, float hy, float hz) {
        if (world.isRemote) return EnumActionResult.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);
        BlockPos p = pos.offset(facing);
        if (!player.canPlayerEdit(p, facing, stack)) return EnumActionResult.FAIL;
        double yOff = facing == EnumFacing.UP && world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockFence ? 0.5D : 0.0D;
        EntityWitherCreeper e = new EntityWitherCreeper(world);
        e.setLocationAndAngles(p.getX() + 0.5D, p.getY() + yOff, p.getZ() + 0.5D, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
        e.rotationYawHead = e.rotationYaw;
        e.renderYawOffset = e.rotationYaw;
        e.onInitialSpawn(world.getDifficultyForLocation(p), null);
        if (stack.hasDisplayName()) e.setCustomNameTag(stack.getDisplayName());
        world.spawnEntity(e);
        e.playLivingSound();
        if (!player.capabilities.isCreativeMode) stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
