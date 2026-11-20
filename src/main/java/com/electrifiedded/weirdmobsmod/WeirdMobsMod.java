package com.electrifiedded.weirdmobsmod;

import com.electrifiedded.archaiclib.original.OriginalModAssets;
import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.legacy.sound.LegacyModSounds;
import com.electrifiedded.archaiclib.legacy.world.LegacySpawns;
import com.electrifiedded.archaiclib.v1710.api.item.ArchaicItem;
import com.electrifiedded.weirdmobsmod.block.BlockTerrorTricksterBlock;
import com.electrifiedded.weirdmobsmod.entity.*;
import com.electrifiedded.weirdmobsmod.item.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Arrays;
import java.util.List;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib@[1.10.0,)")
public class WeirdMobsMod {

    @Mod.Instance(Reference.MOD_ID)
    public static WeirdMobsMod instance;

    @SidedProxy(clientSide = "com.electrifiedded.weirdmobsmod.client.ClientProxy", serverSide = "com.electrifiedded.weirdmobsmod.CommonProxy")
    public static CommonProxy proxy;

    public static final ArmorMaterial bigEarArmor = EnumHelper.addArmorMaterial("WMM_BIGEARARMOR", "weirdmobsmod:bigeararmor", 15, new int[]{1, 3, 4, 2}, 10, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0F);
    public static final ArmorMaterial stickManArmor = EnumHelper.addArmorMaterial("WMM_STICKMANARMOR", "weirdmobsmod:stickmanarmor", 1, new int[]{1, 1, 1, 1}, 0, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0F);
    public static final Item.ToolMaterial potionStickTool = EnumHelper.addToolMaterial("WMM_POTIONSTICKTOOL", 0, 20, -3.0F, 0.0F, 10);

    public static Item netherStarPiece, bigEarSkin, fly, potionStick, bigEarArmorHelmet, bigEarArmorChestplate,
            bigEarArmorLeggings, bigEarArmorBoots, stickManHead, necklace, crystal, spawnWitherCreeper, evilFly,
            crazyBirdRaw, crazyBirdCooked, fatManRaw, fatManCooked;
    public static Block terrorTricksterBlock;

    private int nextEntityId = 0;

    public static final String ORIGINAL_NAME = "Weird-MobsV1.3.jar - Weird Mobs Continuation 1.3 for 1.7.10 (quentin452; original by ZurraGameZ and UKdeccy)";
    public static final String ORIGINAL_URL = "https://github.com/quentin452/Weird-mobs-Continuation/releases/tag/V1.3";

    public WeirdMobsMod() {
        MinecraftForge.EVENT_BUS.register(this);
        // textures and sounds come from the player's own copy of the original jar (archaic_originals/)
        OriginalModAssets.hide(Reference.MOD_ID, "sounds.json");
        OriginalModAssets.hide(Reference.MOD_ID, "lang/en_us.lang");
    }

    public static SoundEvent sound(String legacyName) {
        return LegacyModSounds.get(Reference.MOD_ID, legacyName);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        OriginalModAssets.require(Reference.MOD_ID, "Weird Mobs Mod 2", ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);
        LegacyModSounds.autoRegister(Reference.MOD_ID);
        registerEntities();
        proxy.preInit();
    }

    private static Item item(Item item, String name, String translation, String texture, CreativeTabs tab) {
        item.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        item.setTranslationKey(translation);
        if (tab != null) item.setCreativeTab(tab);
        LegacyGameRegistry.registerItem(item, name);
        ArchaicItem.registerMetaTexture(item, 0, texture);
        return item;
    }

    @SubscribeEvent
    public void onBlocks(RegistryEvent.Register<Block> e) {
        terrorTricksterBlock = new BlockTerrorTricksterBlock(Material.ROCK).setTranslationKey("TerrorTricksterBlock")
                .setRegistryName(Reference.MOD_ID, "terror_trickster_block");
        e.getRegistry().register(terrorTricksterBlock);
    }

    @SubscribeEvent
    public void onItems(RegistryEvent.Register<Item> e) {
        netherStarPiece = item(new Item(), "nether_star_piece", "NetherStarPiece", "netherstarpiece", CreativeTabs.MATERIALS);
        bigEarSkin = item(new Item(), "big_ear_skin", "BigEarSkin", "bigearskin", CreativeTabs.MATERIALS);
        fly = item(new Item(), "fly", "Fly", "fly", CreativeTabs.MISC);
        potionStick = item(new ItemPotionStick(potionStickTool), "potion_stick", "PotionStick", "potion_stick", CreativeTabs.COMBAT);
        bigEarArmorHelmet = item(new ItemBigEarArmor(bigEarArmor, EntityEquipmentSlot.HEAD), "big_ear_helmet", "BigEarHelmet", "bigeararmorhelmet", CreativeTabs.COMBAT);
        bigEarArmorChestplate = item(new ItemBigEarArmor(bigEarArmor, EntityEquipmentSlot.CHEST), "big_ear_chestplate", "BigEarChestplate", "bigeararmorchestplate", CreativeTabs.COMBAT);
        bigEarArmorLeggings = item(new ItemBigEarArmor(bigEarArmor, EntityEquipmentSlot.LEGS), "big_ear_leggings", "BigEarLeggings", "bigeararmorleggings", CreativeTabs.COMBAT);
        bigEarArmorBoots = item(new ItemBigEarArmor(bigEarArmor, EntityEquipmentSlot.FEET), "big_ear_boots", "BigEarBoots", "bigeararmorboots", CreativeTabs.COMBAT);
        stickManHead = item(new ItemBigEarArmor(stickManArmor, EntityEquipmentSlot.HEAD), "stick_man_head", "StickManHead", "stickmanhead", CreativeTabs.COMBAT);
        necklace = item(new Item(), "necklace", "Necklace", "necklace", CreativeTabs.MISC);
        crystal = item(new Item(), "crystal", "Crystal", "crystal", CreativeTabs.MATERIALS);
        spawnWitherCreeper = item(new ItemSpawnWitherCreeper(), "spawn_wither_creeper", "SpawnWitherCreeper", "spawnwithercreeper", CreativeTabs.MISC);
        evilFly = item(new Item(), "evil_fly", "EvilFly", "evil_fly", CreativeTabs.MISC);
        crazyBirdRaw = item(new net.minecraft.item.ItemFood(2, false), "raw_crazy_bird", "RawCrazyBird", "rawcrazybird", null);
        crazyBirdCooked = item(new net.minecraft.item.ItemFood(6, false), "cooked_crazy_bird", "CookedCrazyBird", "cookedcrazybird", null);
        fatManRaw = item(new net.minecraft.item.ItemFood(2, false), "raw_fat_man", "RawFatMan", "rawfatman", null);
        fatManCooked = item(new net.minecraft.item.ItemFood(6, false), "cooked_fat_man", "CookedFatMan", "cookedfatman", null);
        Item ib = new net.minecraft.item.ItemBlock(terrorTricksterBlock).setRegistryName(terrorTricksterBlock.getRegistryName());
        e.getRegistry().register(ib);
        LegacyGameRegistry.registerItemModel(ib, 0, "minecraft:stone", "inventory");
    }

    @SubscribeEvent
    public void onRecipes(RegistryEvent.Register<IRecipe> e) {
        shaped(e, new ItemStack(Items.NETHER_STAR), " N ", "NDN", " N ", 'N', netherStarPiece, 'D', Items.DIAMOND);
        shaped(e, new ItemStack(Blocks.DIRT), "FFF", "FFF", "FFF", 'F', fly);
        // 1.7 potion metas 8196 (poison), 8234 (weakness ext.), 8232 (weakness)
        shaped(e, new ItemStack(potionStick, 2), " P ", "S#W", " # ",
                'P', potion(PotionTypes.POISON), 'S', potion(PotionTypes.LONG_WEAKNESS), 'W', potion(PotionTypes.WEAKNESS), '#', Items.STICK);
        shaped(e, new ItemStack(bigEarArmorHelmet), "BBB", "B B", 'B', bigEarSkin);
        shaped(e, new ItemStack(bigEarArmorChestplate), "B B", "BBB", "BBB", 'B', bigEarSkin);
        shaped(e, new ItemStack(bigEarArmorLeggings), "BBB", "B B", "B B", 'B', bigEarSkin);
        shaped(e, new ItemStack(bigEarArmorBoots), "B B", "B B", 'B', bigEarSkin);
        shaped(e, new ItemStack(crystal), "ede", 'e', Items.EMERALD, 'd', Items.DIAMOND);
        shaped(e, new ItemStack(necklace), " s ", "sgs", " c ", 's', Items.STRING, 'g', Items.GOLD_INGOT, 'c', crystal);
        shaped(e, new ItemStack(spawnWitherCreeper), "fff", "bgb", "fff", 'f', evilFly, 'b', Items.BLAZE_ROD, 'g', Items.GOLD_INGOT);
        shaped(e, new ItemStack(evilFly), "nsn", "sfs", "nsn", 'f', fly, 'n', Items.NETHER_WART, 's', Items.SPIDER_EYE);
    }

    private static Ingredient potion(net.minecraft.potion.PotionType type) {
        return new net.minecraftforge.common.crafting.IngredientNBT(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), type)) {};
    }

    private int recipeId = 0;

    private void shaped(RegistryEvent.Register<IRecipe> e, ItemStack out, Object... params) {
        ShapedOreRecipe r = new ShapedOreRecipe(null, out, params);
        r.setRegistryName(new ResourceLocation(Reference.MOD_ID, "recipe_" + (recipeId++)));
        e.getRegistry().register(r);
    }

    private void entity(Class<? extends Entity> cls, String name, int egg1, int egg2) {
        if (egg1 < 0) LegacyGameRegistry.registerModEntity(cls, name, nextEntityId++, this, 80, 3, true);
        else LegacyGameRegistry.registerModEntity(cls, name, nextEntityId++, this, 80, 3, true, egg1, egg2);
    }

    private void registerEntities() {
        entity(EntityBigEar.class, "big_ear", 789516, 16712704);
        entity(EntityBigFish.class, "big_fish", 3227729, 5316640);
        entity(EntityBUDRBot.class, "budr_bot", 6182967, 16577861);
        entity(EntityButterMonger.class, "butter_monger", 12691762, 1446661);
        entity(EntityCoohBird.class, "cooh_bird", 11879680, 5517082);
        entity(EntityCrazyBird.class, "crazy_bird", 9276813, 16350478);
        entity(EntityCyclopsMan.class, "cyclops_man", 12018726, 8618883);
        entity(EntityDistortedMan.class, "distorted_man", 16756613, 7839281);
        entity(EntityFatFish.class, "fat_fish", 8026746, 5987163);
        entity(EntityFatMan.class, "fat_man", 12027505, 10905148);
        entity(EntityGooftile.class, "gooftile", 3045098, 793659);
        entity(EntityKnifeMan.class, "knife_man", 4605510, 7286832);
        entity(EntityPotionStick.class, "potion_stick", 1622287, 6703385);
        entity(EntitySpiderCreeper.class, "spider_creeper", 10230807, 4208688);
        entity(EntitySpinningFeets.class, "spinning_feets", 2960685, 11696970);
        entity(EntityStickMan.class, "stick_man", 16777215, 0);
        entity(EntityStickManBaby.class, "stick_man_baby", -1, 0);
        entity(EntityTallMan.class, "tall_man", 5329492, 15456628);
        entity(EntityTerrorTrickster.class, "terror_trickster", 13027014, 14803425);
        entity(EntityTrinitope.class, "trinitope", 8323072, 15687424);
        entity(EntityVestCreeper.class, "vest_creeper", 6213708, 14369818);
        entity(EntityVillagerHead.class, "villager_head", 12422002, 10335591);
        entity(EntityWalkingPainting.class, "walking_painting", 18573, 4862504);
        entity(EntityWalkingRadio.class, "walking_radio", 6250335, 16646915);
        entity(EntityWitherCreeper.class, "wither_creeper", 4671303, 12698049);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        GameRegistry.addSmelting(crazyBirdRaw, new ItemStack(crazyBirdCooked), 2.0F);
        GameRegistry.addSmelting(fatManRaw, new ItemStack(fatManCooked), 2.0F);

        // Original: registerSpawn() looped over every biome (its ocean/river check was always true).
        // Restricted to overworld biomes here so mobs do not flood the Nether/End.
        List<Biome> overworld = LegacySpawns.biomes(LegacySpawns::isOverworld);
        LegacySpawns.add(EntityBigEar.class, 1, 1, 1, EnumCreatureType.MONSTER, overworld);
        LegacySpawns.addSpawn(EntityBigFish.class, 3, 5, 15, EnumCreatureType.WATER_CREATURE, Biomes.OCEAN);
        LegacySpawns.addSpawn(EntityFatFish.class, 3, 5, 15, EnumCreatureType.WATER_CREATURE, Biomes.OCEAN);
        for (Class<? extends EntityLiving> c : Arrays.<Class<? extends EntityLiving>>asList(EntityButterMonger.class,
                EntityCyclopsMan.class, EntityDistortedMan.class, EntityFatMan.class, EntityKnifeMan.class,
                EntityPotionStick.class, EntityTallMan.class, EntityTrinitope.class, EntityVestCreeper.class, EntityVillagerHead.class))
            LegacySpawns.add(c, 3, 2, 6, EnumCreatureType.MONSTER, overworld);
        for (Class<? extends EntityLiving> c : Arrays.<Class<? extends EntityLiving>>asList(EntityBUDRBot.class,
                EntityCoohBird.class, EntityCrazyBird.class, EntityGooftile.class, EntitySpiderCreeper.class,
                EntitySpinningFeets.class, EntityStickMan.class, EntityWalkingPainting.class, EntityWalkingRadio.class))
            LegacySpawns.add(c, 3, 2, 6, EnumCreatureType.CREATURE, overworld);
        LegacySpawns.addSpawn(EntityTerrorTrickster.class, 1, 2, 6, EnumCreatureType.AMBIENT,
                Biomes.FOREST, Biomes.PLAINS, Biomes.FOREST_HILLS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.SWAMPLAND,
                Biomes.EXTREME_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.BEACH, Biomes.MESA,
                Biomes.MESA_ROCK, Biomes.MUSHROOM_ISLAND);
    }
}
