package com.electrifiedded.weirdmobsmod.client;

import com.electrifiedded.archaiclib.legacy.client.LegacyRenderLiving;
import com.electrifiedded.weirdmobsmod.CommonProxy;
import com.electrifiedded.weirdmobsmod.entity.*;
import com.electrifiedded.weirdmobsmod.model.*;
import net.minecraft.util.ResourceLocation;

import static com.electrifiedded.archaiclib.legacy.client.LegacyRenderLiving.register;
import static com.electrifiedded.archaiclib.legacy.client.LegacyRenderLiving.tex;

public class ClientProxy extends CommonProxy {
    private static final String M = "weirdmobsmod:textures/mobs/";

    @Override
    public void preInit() {
        ResourceLocation be = tex(M + "bigear.png"), beT = tex(M + "bigear_tamed.png"), beS = tex(M + "bigear_sitting.png"),
                beTN = tex(M + "bigear_tamed_necklace.png"), beSN = tex(M + "bigear_sitting_necklace.png");
        LegacyRenderLiving.<EntityBigEar>register(EntityBigEar.class, ModelBigEar::new, 0.3F,
                e -> e.isSitting() ? (e.hasNecklace() ? beSN : beS) : e.isTamed() ? (e.hasNecklace() ? beTN : beT) : be);
        ResourceLocation tt = tex(M + "terrortrickster.png"), ttH = tex(M + "terrortrickster_hostile.png");
        LegacyRenderLiving.<EntityTerrorTrickster>register(EntityTerrorTrickster.class, ModelTerrorTrickster::new, 0.3F, e -> e.isHostile() ? ttH : tt);

        register(EntityBigFish.class, ModelBigFish::new, 0.3F, M + "bigfish.png");
        register(EntityBUDRBot.class, ModelBUDRBot::new, 0.3F, M + "budrbot.png");
        register(EntityButterMonger.class, ModelButterMonger::new, 0.3F, M + "buttermonger.png");
        register(EntityCoohBird.class, ModelCoohBird::new, 0.3F, M + "coohbird.png");
        register(EntityCrazyBird.class, ModelCrazyBird::new, 0.3F, M + "crazybird.png");
        register(EntityCyclopsMan.class, ModelCyclopsMan::new, 0.3F, M + "cyclopsman.png");
        register(EntityDistortedMan.class, ModelDistortedMan::new, 0.3F, M + "distortedman.png");
        register(EntityFatFish.class, ModelFatFish::new, 0.3F, M + "fatfish.png");
        register(EntityFatMan.class, ModelFatMan::new, 0.3F, M + "fatman.png");
        register(EntityGooftile.class, ModelGooftile::new, 0.3F, M + "gooftile.png");
        register(EntityKnifeMan.class, ModelKnifeMan::new, 0.3F, M + "knifeman.png");
        register(EntityPotionStick.class, ModelPotionStick::new, 0.3F, M + "potionstick.png");
        register(EntitySpiderCreeper.class, ModelSpiderCreeper::new, 0.3F, M + "spidercreeper.png");
        register(EntitySpinningFeets.class, ModelSpinningFeets::new, 0.3F, M + "spinningfeets.png");
        register(EntityStickMan.class, ModelStickMan::new, 0.3F, M + "stickman.png");
        register(EntityStickManBaby.class, ModelStickManBaby::new, 0.3F, M + "stickmanbaby.png");
        register(EntityTallMan.class, ModelTallMan::new, 0.3F, M + "tallman.png");
        register(EntityTrinitope.class, ModelTrinitope::new, 0.3F, M + "trinitope.png");
        register(EntityVestCreeper.class, ModelVestCreeper::new, 0.3F, M + "vestcreeper.png");
        register(EntityVillagerHead.class, ModelVillagerHead::new, 0.3F, M + "villagerhead.png");
        register(EntityWalkingPainting.class, ModelWalkingPainting::new, 0.3F, M + "walkingpainting.png");
        register(EntityWalkingRadio.class, ModelWalkingRadio::new, 0.3F, M + "walkingradio.png");
        register(EntityWitherCreeper.class, ModelWitherCreeper::new, 0.3F, M + "withercreeper.png");
    }
}
