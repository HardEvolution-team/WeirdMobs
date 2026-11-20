package com.electrifiedded.weirdmobsmod.entity;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.world.World;

/** Explodes when a fishing hook touches it. */
public class EntityFatFish extends EntityWeirdFish {
    public EntityFatFish(World world) {
        super(world);
    }

    @Override public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.isEntityAlive()
                && !this.world.getEntitiesWithinAABB(EntityFishHook.class, this.getEntityBoundingBox().grow(1.0D)).isEmpty()) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F, false);
            this.setDead();
        }
    }
}
