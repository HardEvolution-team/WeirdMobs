package com.electrifiedded.weirdmobsmod.block;

import com.electrifiedded.weirdmobsmod.entity.EntityTerrorTrickster;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Looks like stone; breaking it releases a Terror Trickster. */
public class BlockTerrorTricksterBlock extends Block {
    public BlockTerrorTricksterBlock(Material material) {
        super(material);
        setHardness(1.5F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            EntityTerrorTrickster t = new EntityTerrorTrickster(world);
            t.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntity(t);
            t.spawnExplosionParticle();
        }
        super.onPlayerDestroy(world, pos, state);
    }
}
