package com.electrifiedded.weirdmobsmod.entity;

import com.electrifiedded.weirdmobsmod.WeirdMobsMod;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/** Mini-Wither with a creeper body (three heads, wither skulls, block breaking when hurt). Mostly a copy of 1.7 EntityWither. */
public class EntityWitherCreeper extends EntityMob implements IRangedAttackMob {
    private static final DataParameter<Integer>[] TARGETS = new DataParameter[]{
            EntityDataManager.createKey(EntityWitherCreeper.class, DataSerializers.VARINT),
            EntityDataManager.createKey(EntityWitherCreeper.class, DataSerializers.VARINT),
            EntityDataManager.createKey(EntityWitherCreeper.class, DataSerializers.VARINT)};
    private static final DataParameter<Integer> INVULNERABILITY = EntityDataManager.createKey(EntityWitherCreeper.class, DataSerializers.VARINT);

    private final float[] xRotationHeads = new float[2];
    private final float[] yRotationHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];
    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];
    private int blockBreakCounter;
    private final BossInfoServer bossInfo = (BossInfoServer) new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS).setDarkenSky(false);

    private static final Predicate<Entity> NOT_UNDEAD = e -> e instanceof EntityLivingBase
            && ((EntityLivingBase) e).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD
            && ((EntityLivingBase) e).attackable();

    public EntityWitherCreeper(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(1.0F, 1.5F);
        this.isImmuneToFire = true;
        this.experienceValue = 50;
    }

    @Override protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 40, 20.0F));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, NOT_UNDEAD));
    }

    @Override protected void entityInit() {
        super.entityInit();
        for (DataParameter<Integer> p : TARGETS) this.dataManager.register(p, 0);
        this.dataManager.register(INVULNERABILITY, 0);
    }

    @Override public void writeEntityToNBT(NBTTagCompound nbt) { super.writeEntityToNBT(nbt); nbt.setInteger("Invul", getInvulTime()); }

    @Override public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setInvulTime(nbt.getInteger("Invul"));
        if (this.hasCustomName()) this.bossInfo.setName(this.getDisplayName());
    }

    @Override public void setCustomNameTag(String name) { super.setCustomNameTag(name); this.bossInfo.setName(this.getDisplayName()); }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_WITHER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.ENTITY_WITHER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_WITHER_DEATH; }

    @Override public void onLivingUpdate() {
        if (!this.world.isRemote && getWatchedTargetId(0) > 0) {
            Entity entity = this.world.getEntityByID(getWatchedTargetId(0));
            if (entity != null) {
                double dx = entity.posX - this.posX, dz = entity.posZ - this.posZ, d = dx * dx + dz * dz;
                if (d > 9.0D) {
                    double s = MathHelper.sqrt(d);
                    this.motionX += (dx / s * 0.5D - this.motionX) * 0.6D;
                    this.motionZ += (dz / s * 0.5D - this.motionZ) * 0.6D;
                }
            }
        }
        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05D)
            this.rotationYaw = (float) MathHelper.atan2(this.motionZ, this.motionX) * (180F / (float) Math.PI) - 90.0F;
        super.onLivingUpdate();

        for (int i = 0; i < 2; ++i) {
            this.yRotOHeads[i] = this.yRotationHeads[i];
            this.xRotOHeads[i] = this.xRotationHeads[i];
        }
        for (int i = 0; i < 2; ++i) {
            int id = getWatchedTargetId(i + 1);
            Entity t = id > 0 ? this.world.getEntityByID(id) : null;
            if (t != null) {
                double hx = headX(i + 1), hy = headY(i + 1), hz = headZ(i + 1);
                double dx = t.posX - hx, dy = t.posY + t.getEyeHeight() - hy, dz = t.posZ - hz;
                double dh = MathHelper.sqrt(dx * dx + dz * dz);
                float yaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                float pitch = (float) (-(MathHelper.atan2(dy, dh) * (180D / Math.PI)));
                this.xRotationHeads[i] = rotlerp(this.xRotationHeads[i], pitch, 40.0F);
                this.yRotationHeads[i] = rotlerp(this.yRotationHeads[i], yaw, 10.0F);
            } else {
                this.yRotationHeads[i] = rotlerp(this.yRotationHeads[i], this.renderYawOffset, 10.0F);
            }
        }
        if (this.world.isRemote) {
            boolean armored = isArmored();
            for (int j = 0; j < 3; ++j) {
                double x = headX(j), y = headY(j), z = headZ(j);
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + this.rand.nextGaussian() * 0.3D, y + this.rand.nextGaussian() * 0.2D, z + this.rand.nextGaussian() * 0.3D, 0, 0, 0);
                if (armored && this.world.rand.nextInt(4) == 0)
                    this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, x + this.rand.nextGaussian() * 0.3D, y + this.rand.nextGaussian() * 0.1D, z + this.rand.nextGaussian() * 0.3D, 0.7D, 0.7D, 0.5D);
            }
            if (getInvulTime() > 0) {
                for (int j = 0; j < 3; ++j)
                    this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + this.rand.nextGaussian(), this.posY + this.rand.nextGaussian() * 0.1D, this.posZ + this.rand.nextGaussian(), 0.7D, 0.7D, 0.9D);
            }
        }
    }

    @Override protected void updateAITasks() {
        if (getInvulTime() > 0) {
            int t = getInvulTime() - 1;
            if (t <= 0) {
                this.world.newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, ForgeEventFactory.getMobGriefingEvent(this.world, this));
                this.world.playBroadcastSound(1023, new BlockPos(this), 0);
            }
            setInvulTime(t);
            if (this.ticksExisted % 10 == 0) this.heal(10.0F);
        } else {
            super.updateAITasks();
            for (int i = 1; i < 3; ++i) {
                if (this.ticksExisted >= this.nextHeadUpdate[i - 1]) {
                    this.nextHeadUpdate[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
                    if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                        int k = this.idleHeadUpdates[i - 1]++;
                        if (k > 50) {
                            launchSkullToCoords(i + 1,
                                    MathHelper.nextDouble(this.rand, this.posX - 10.0D, this.posX + 10.0D),
                                    MathHelper.nextDouble(this.rand, this.posY - 5.0D, this.posY + 5.0D),
                                    MathHelper.nextDouble(this.rand, this.posZ - 10.0D, this.posZ + 10.0D), true);
                            this.idleHeadUpdates[i - 1] = 0;
                        }
                    }
                    int id = getWatchedTargetId(i);
                    if (id > 0) {
                        Entity e = this.world.getEntityByID(id);
                        if (e instanceof EntityLivingBase && e.isEntityAlive() && this.getDistanceSq(e) <= 900.0D && this.canEntityBeSeen(e)) {
                            launchSkullToEntity(i + 1, (EntityLivingBase) e);
                            this.nextHeadUpdate[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                            this.idleHeadUpdates[i - 1] = 0;
                        } else {
                            updateWatchedTargetId(i, 0);
                        }
                    } else {
                        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(20.0D, 8.0D, 20.0D), NOT_UNDEAD::apply);
                        for (int n = 0; n < 10 && !list.isEmpty(); ++n) {
                            EntityLivingBase l = list.get(this.rand.nextInt(list.size()));
                            if (l != this && l.isEntityAlive() && this.canEntityBeSeen(l)) {
                                if (!(l instanceof EntityPlayer) || !((EntityPlayer) l).capabilities.disableDamage) updateWatchedTargetId(i, l.getEntityId());
                                break;
                            }
                            list.remove(l);
                        }
                    }
                }
            }
            updateWatchedTargetId(0, this.getAttackTarget() != null ? this.getAttackTarget().getEntityId() : 0);

            if (this.blockBreakCounter > 0) {
                --this.blockBreakCounter;
                if (this.blockBreakCounter == 0 && ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
                    int y0 = MathHelper.floor(this.posY), x0 = MathHelper.floor(this.posX), z0 = MathHelper.floor(this.posZ);
                    boolean flag = false;
                    for (int dx = -1; dx <= 1; ++dx)
                        for (int dz = -1; dz <= 1; ++dz)
                            for (int dy = 0; dy <= 3; ++dy) {
                                BlockPos p = new BlockPos(x0 + dx, y0 + dy, z0 + dz);
                                IBlockState s = this.world.getBlockState(p);
                                if (!s.getBlock().isAir(s, this.world, p) && s.getBlock().canEntityDestroy(s, this.world, p, this)
                                        && ForgeEventFactory.onEntityDestroyBlock(this, p, s))
                                    flag = this.world.destroyBlock(p, true) || flag;
                            }
                    if (flag) this.world.playEvent(null, 1022, new BlockPos(this), 0);
                }
            }
            if (this.ticksExisted % 20 == 0) this.heal(1.0F);
        }
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override public void addTrackingPlayer(EntityPlayerMP player) { super.addTrackingPlayer(player); this.bossInfo.addPlayer(player); }
    @Override public void removeTrackingPlayer(EntityPlayerMP player) { super.removeTrackingPlayer(player); this.bossInfo.removePlayer(player); }

    @Override public void setInWeb() {}
    @Override public int getTotalArmorValue() { return 4; }

    private double headX(int head) {
        if (head <= 0) return this.posX;
        float f = (this.renderYawOffset + 180 * (head - 1)) * 0.017453292F;
        return this.posX + MathHelper.cos(f) * 1.3D * 0.3D;
    }

    private double headY(int head) { return this.posY + 1.0D; }

    private double headZ(int head) {
        if (head <= 0) return this.posZ;
        float f = (this.renderYawOffset + 180 * (head - 1)) * 0.017453292F;
        return this.posZ + MathHelper.sin(f) * 1.3D * 0.3D;
    }

    private float rotlerp(float cur, float target, float max) {
        float f = MathHelper.clamp(MathHelper.wrapDegrees(target - cur), -max, max);
        return cur + f;
    }

    private void launchSkullToEntity(int head, EntityLivingBase target) {
        launchSkullToCoords(head, target.posX, target.posY + target.getEyeHeight() * 0.5D, target.posZ, head == 0 && this.rand.nextFloat() < 0.001F);
    }

    private void launchSkullToCoords(int head, double x, double y, double z, boolean invulnerable) {
        this.world.playEvent(null, 1024, new BlockPos(this), 0);
        double hx = headX(head), hy = headY(head), hz = headZ(head);
        EntityWitherSkull skull = new EntityWitherSkull(this.world, this, x - hx, y - hy, z - hz);
        if (invulnerable) skull.setInvulnerable(true);
        skull.posY = hy;
        skull.posX = hx;
        skull.posZ = hz;
        this.world.spawnEntity(skull);
    }

    @Override public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) { launchSkullToEntity(0, target); }

    @Override public void setSwingingArms(boolean swinging) {}

    @Override public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source) || source == DamageSource.DROWN || getInvulTime() > 0) return false;
        if (isArmored() && source.getImmediateSource() instanceof EntityArrow) return false;
        Entity e = source.getTrueSource();
        if (e != null && !(e instanceof EntityPlayer) && e instanceof EntityLivingBase
                && ((EntityLivingBase) e).getCreatureAttribute() == this.getCreatureAttribute()) return false;
        if (this.blockBreakCounter <= 0) this.blockBreakCounter = 20;
        for (int i = 0; i < this.idleHeadUpdates.length; ++i) this.idleHeadUpdates[i] += 3;
        return super.attackEntityFrom(source, amount);
    }

    @Override protected void dropFewItems(boolean hit, int looting) { this.dropItem(WeirdMobsMod.netherStarPiece, 1); }

    @Override protected void despawnEntity() { this.idleTime = 0; }

    @SideOnly(Side.CLIENT)
    @Override public int getBrightnessForRender() { return 15728880; }

    @Override public void fall(float distance, float multiplier) {}

    @Override public void addPotionEffect(PotionEffect effect) {}

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
    }

    @SideOnly(Side.CLIENT) public float getHeadYRotation(int head) { return this.yRotationHeads[head]; }
    @SideOnly(Side.CLIENT) public float getHeadXRotation(int head) { return this.xRotationHeads[head]; }

    public int getInvulTime() { return this.dataManager.get(INVULNERABILITY); }
    public void setInvulTime(int t) { this.dataManager.set(INVULNERABILITY, t); }
    public int getWatchedTargetId(int head) { return this.dataManager.get(TARGETS[head]); }
    public void updateWatchedTargetId(int head, int id) { this.dataManager.set(TARGETS[head], id); }

    public boolean isArmored() { return this.getHealth() <= this.getMaxHealth() / 2.0F; }

    @Override public EnumCreatureAttribute getCreatureAttribute() { return EnumCreatureAttribute.UNDEAD; }

    @Override public boolean startRiding(Entity entity, boolean force) { return false; }

    @Override public boolean isNonBoss() { return false; }
}
