package com.leclowndu93150.bobo_tweaks.additional.enchantments.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.CustomEnchantmentCategory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EnchantmentModuleConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/bobo_tweaks_enchantments.json");
    
    public static boolean enableEnchantmentModule = true;
    
    public static class EnchantmentCap {
        public static boolean enabled = true;
        public static Map<String, Integer> itemCaps = new HashMap<>();
        
        static {
            itemCaps.put("minecraft:diamond_sword", 3);
            itemCaps.put("minecraft:netherite_sword", 4);
        }
    }
    
    public static class Reprisal {
        public static boolean enabled = true;
        public static String category = "WEAPON";
        public static int maxLevel = 3;
        public static double baseDamageBoost = 2.0;
        public static double damageBoostPerLevel = 1.0;
        public static int baseDuration = 100;
        public static int durationPerLevel = 20;
        public static int baseCooldown = 200;
        public static int cooldownReductionPerLevel = 20;
    }
    
    public static class Momentum {
        public static boolean enabled = true;
        public static String category = "WEAPON";
        public static int maxLevel = 3;
        public static int baseMaxStacks = 5;
        public static int maxStackIncreasePerLevel = 2;
        public static int stackDuration = 200;
        public static double damageBoostPerStack = 0.5;
        public static double damageBoostPerLevel = 0.25;
        public static int killStacks = 3;
        public static int allyKillStacks = 1;
    }
    
    public static class Spellblade {
        public static boolean enabled = true;
        public static String category = "WEAPON_AND_BOW";
        public static int maxLevel = 3;
        
        public static class PassiveA {
            public static double baseMeleeSpellPowerBoost = 10.0;
            public static double meleeBoostPerLevel = 5.0;
            public static double baseRangedSpellPowerBoost = 8.0;
            public static double rangedBoostPerLevel = 4.0;
            public static int duration = 100;
            public static int baseCooldown = 160;
            public static int cooldownDecreasePerLevel = 20;
        }
        
        public static class PassiveB {
            public static double baseArrowDamageBoost = 15.0;
            public static double arrowBoostPerLevel = 5.0;
            public static double baseAttackDamageBoost = 12.0;
            public static double attackBoostPerLevel = 4.0;
            public static int duration = 100;
            public static int baseCooldown = 160;
            public static int cooldownDecreasePerLevel = 20;
        }
    }
    
    public static class MagicalAttunement {
        public static boolean enabled = true;
        public static String category = "WEAPON";
        public static int maxLevel = 3;
        public static double baseDamagePerLevel = 3.0;
        public static double maxManaPercent = 0.1;
        public static int duration = 20;
        public static int baseCooldown = 200;
        public static int cooldownDecreasePerLevel = 20;
    }
    
    public static class Perfectionist {
        public static boolean enabled = true;
        public static String category = "WEAPON";
        public static int maxLevel = 3;
        public static double baseAttackSpeedBoost = 10.0;
        public static double attackSpeedPerLevel = 5.0;
        public static double baseCastSpeedBoost = 15.0;
        public static double castSpeedPerLevel = 5.0;
        public static int duration = 100;
    }
    
    public static class Hunter {
        public static boolean enabled = true;
        public static String category = "WEAPON_AND_BOW";
        public static int maxLevel = 3;
        public static double baseCritDamageBoost = 20.0;
        public static double critDamagePerLevel = 10.0;
        public static double flatCritChance = 15.0;
    }
    
    public static class Multiscale {
        public static boolean enabled = true;
        public static String category = "ARMOR_CHEST";
        public static int maxLevel = 3;
        public static double flatArmorPerLevel = 2.0;
        public static double percentArmorPerLevel = 5.0;
    }
    
    public static class InvigoratingDefenses {
        public static boolean enabled = true;
        public static String category = "ARMOR_CHEST";
        public static int maxLevel = 3;
        public static double baseMovementSpeedBoost = 20.0;
        public static double speedBoostPerLevel = 5.0;
        public static double baseHealthRestored = 2.0;
        public static double healthRestoredPerLevel = 1.0;
        public static int cooldown = 200;
        public static int baseDuration = 60;
        public static int durationPerLevel = 20;
    }
    
    public static class LifeSurge {
        public static boolean enabled = true;
        public static String category = "ARMOR_CHEST";
        public static int maxLevel = 3;
        public static double healthThreshold = 0.4;
        public static double flatArmorPerLevel = 3.0;
        public static double percentArmorPerLevel = 10.0;
        public static double flatLifestealPerLevel = 5.0;
        public static double flatSpellStealPerLevel = 5.0;
        public static int duration = 100;
        public static int baseCooldown = 400;
        public static int cooldownDecreasePerLevel = 40;
    }
    
    public static class ShadowWalker {
        public static boolean enabled = true;
        public static String category = "ARMOR_CHEST";
        public static int maxLevel = 3;
        public static int invisibilityDuration = 60;
        public static int movementSpeedDuration = 60;
        public static double movementSpeedPercent = 30.0;
        public static double baseDamageAmplifier = 20.0;
        public static double damageAmplifierPerLevel = 10.0;
    }

    public static EnchantmentCategory getCategoryFromString(String categoryName) {
        return switch (categoryName.toUpperCase()) {
            case "ARMOR" -> EnchantmentCategory.ARMOR;
            case "ARMOR_FEET" -> EnchantmentCategory.ARMOR_FEET;
            case "ARMOR_LEGS" -> EnchantmentCategory.ARMOR_LEGS;
            case "ARMOR_CHEST" -> EnchantmentCategory.ARMOR_CHEST;
            case "ARMOR_HEAD" -> EnchantmentCategory.ARMOR_HEAD;
            case "WEAPON" -> EnchantmentCategory.WEAPON;
            case "DIGGER" -> EnchantmentCategory.DIGGER;
            case "FISHING_ROD" -> EnchantmentCategory.FISHING_ROD;
            case "TRIDENT" -> EnchantmentCategory.TRIDENT;
            case "BREAKABLE" -> EnchantmentCategory.BREAKABLE;
            case "BOW" -> EnchantmentCategory.BOW;
            case "WEARABLE" -> EnchantmentCategory.WEARABLE;
            case "CROSSBOW" -> EnchantmentCategory.CROSSBOW;
            case "VANISHABLE" -> EnchantmentCategory.VANISHABLE;
            case "WEAPON_AND_BOW" -> CustomEnchantmentCategory.WEAPON_AND_BOW;
            default -> EnchantmentCategory.WEAPON;
        };
    }
    
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                
                if (json.has("enabled")) {
                    enableEnchantmentModule = json.get("enabled").getAsBoolean();
                }
                
                loadEnchantmentCap(json);
                loadReprisal(json);
                loadMomentum(json);
                loadSpellblade(json);
                loadMagicalAttunement(json);
                loadPerfectionist(json);
                loadHunter(json);
                loadMultiscale(json);
                loadInvigoratingDefenses(json);
                loadLifeSurge(json);
                loadShadowWalker(json);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }
    
    private static void loadEnchantmentCap(JsonObject json) {
        if (json.has("enchantment_cap")) {
            JsonObject cap = json.getAsJsonObject("enchantment_cap");
            if (cap.has("enabled")) {
                EnchantmentCap.enabled = cap.get("enabled").getAsBoolean();
            }
            if (cap.has("item_caps")) {
                EnchantmentCap.itemCaps.clear();
                JsonObject caps = cap.getAsJsonObject("item_caps");
                for (Map.Entry<String, JsonElement> entry : caps.entrySet()) {
                    EnchantmentCap.itemCaps.put(entry.getKey(), entry.getValue().getAsInt());
                }
            }
        }
    }
    
    private static void loadReprisal(JsonObject json) {
        if (json.has("reprisal")) {
            JsonObject reprisal = json.getAsJsonObject("reprisal");
            if (reprisal.has("enabled")) Reprisal.enabled = reprisal.get("enabled").getAsBoolean();
            if (reprisal.has("category")) Reprisal.category = reprisal.get("category").getAsString();
            if (reprisal.has("max_level")) Reprisal.maxLevel = reprisal.get("max_level").getAsInt();
            if (reprisal.has("base_damage_boost")) Reprisal.baseDamageBoost = reprisal.get("base_damage_boost").getAsDouble();
            if (reprisal.has("damage_boost_per_level")) Reprisal.damageBoostPerLevel = reprisal.get("damage_boost_per_level").getAsDouble();
            if (reprisal.has("base_duration")) Reprisal.baseDuration = reprisal.get("base_duration").getAsInt();
            if (reprisal.has("duration_per_level")) Reprisal.durationPerLevel = reprisal.get("duration_per_level").getAsInt();
            if (reprisal.has("base_cooldown")) Reprisal.baseCooldown = reprisal.get("base_cooldown").getAsInt();
            if (reprisal.has("cooldown_reduction_per_level")) Reprisal.cooldownReductionPerLevel = reprisal.get("cooldown_reduction_per_level").getAsInt();
        }
    }
    
    private static void loadMomentum(JsonObject json) {
        if (json.has("momentum")) {
            JsonObject momentum = json.getAsJsonObject("momentum");
            if (momentum.has("enabled")) Momentum.enabled = momentum.get("enabled").getAsBoolean();
            if (momentum.has("category")) Momentum.category = momentum.get("category").getAsString();
            if (momentum.has("max_level")) Momentum.maxLevel = momentum.get("max_level").getAsInt();
            if (momentum.has("base_max_stacks")) Momentum.baseMaxStacks = momentum.get("base_max_stacks").getAsInt();
            if (momentum.has("max_stack_increase_per_level")) Momentum.maxStackIncreasePerLevel = momentum.get("max_stack_increase_per_level").getAsInt();
            if (momentum.has("stack_duration")) Momentum.stackDuration = momentum.get("stack_duration").getAsInt();
            if (momentum.has("damage_boost_per_stack")) Momentum.damageBoostPerStack = momentum.get("damage_boost_per_stack").getAsDouble();
            if (momentum.has("damage_boost_per_level")) Momentum.damageBoostPerLevel = momentum.get("damage_boost_per_level").getAsDouble();
            if (momentum.has("kill_stacks")) Momentum.killStacks = momentum.get("kill_stacks").getAsInt();
            if (momentum.has("ally_kill_stacks")) Momentum.allyKillStacks = momentum.get("ally_kill_stacks").getAsInt();
        }
    }
    
    private static void loadSpellblade(JsonObject json) {
        if (json.has("spellblade")) {
            JsonObject spellblade = json.getAsJsonObject("spellblade");
            if (spellblade.has("enabled")) Spellblade.enabled = spellblade.get("enabled").getAsBoolean();
            if (spellblade.has("category")) Spellblade.category = spellblade.get("category").getAsString();
            if (spellblade.has("max_level")) Spellblade.maxLevel = spellblade.get("max_level").getAsInt();
            
            if (spellblade.has("passive_a")) {
                JsonObject passiveA = spellblade.getAsJsonObject("passive_a");
                if (passiveA.has("base_melee_spell_power_boost")) Spellblade.PassiveA.baseMeleeSpellPowerBoost = passiveA.get("base_melee_spell_power_boost").getAsDouble();
                if (passiveA.has("melee_boost_per_level")) Spellblade.PassiveA.meleeBoostPerLevel = passiveA.get("melee_boost_per_level").getAsDouble();
                if (passiveA.has("base_ranged_spell_power_boost")) Spellblade.PassiveA.baseRangedSpellPowerBoost = passiveA.get("base_ranged_spell_power_boost").getAsDouble();
                if (passiveA.has("ranged_boost_per_level")) Spellblade.PassiveA.rangedBoostPerLevel = passiveA.get("ranged_boost_per_level").getAsDouble();
                if (passiveA.has("duration")) Spellblade.PassiveA.duration = passiveA.get("duration").getAsInt();
                if (passiveA.has("base_cooldown")) Spellblade.PassiveA.baseCooldown = passiveA.get("base_cooldown").getAsInt();
                if (passiveA.has("cooldown_decrease_per_level")) Spellblade.PassiveA.cooldownDecreasePerLevel = passiveA.get("cooldown_decrease_per_level").getAsInt();
            }
            
            if (spellblade.has("passive_b")) {
                JsonObject passiveB = spellblade.getAsJsonObject("passive_b");
                if (passiveB.has("base_arrow_damage_boost")) Spellblade.PassiveB.baseArrowDamageBoost = passiveB.get("base_arrow_damage_boost").getAsDouble();
                if (passiveB.has("arrow_boost_per_level")) Spellblade.PassiveB.arrowBoostPerLevel = passiveB.get("arrow_boost_per_level").getAsDouble();
                if (passiveB.has("base_attack_damage_boost")) Spellblade.PassiveB.baseAttackDamageBoost = passiveB.get("base_attack_damage_boost").getAsDouble();
                if (passiveB.has("attack_boost_per_level")) Spellblade.PassiveB.attackBoostPerLevel = passiveB.get("attack_boost_per_level").getAsDouble();
                if (passiveB.has("duration")) Spellblade.PassiveB.duration = passiveB.get("duration").getAsInt();
                if (passiveB.has("base_cooldown")) Spellblade.PassiveB.baseCooldown = passiveB.get("base_cooldown").getAsInt();
                if (passiveB.has("cooldown_decrease_per_level")) Spellblade.PassiveB.cooldownDecreasePerLevel = passiveB.get("cooldown_decrease_per_level").getAsInt();
            }
        }
    }
    
    private static void loadMagicalAttunement(JsonObject json) {
        if (json.has("magical_attunement")) {
            JsonObject ma = json.getAsJsonObject("magical_attunement");
            if (ma.has("enabled")) MagicalAttunement.enabled = ma.get("enabled").getAsBoolean();
            if (ma.has("category")) MagicalAttunement.category = ma.get("category").getAsString();
            if (ma.has("max_level")) MagicalAttunement.maxLevel = ma.get("max_level").getAsInt();
            if (ma.has("base_damage_per_level")) MagicalAttunement.baseDamagePerLevel = ma.get("base_damage_per_level").getAsDouble();
            if (ma.has("max_mana_percent")) MagicalAttunement.maxManaPercent = ma.get("max_mana_percent").getAsDouble();
            if (ma.has("duration")) MagicalAttunement.duration = ma.get("duration").getAsInt();
            if (ma.has("base_cooldown")) MagicalAttunement.baseCooldown = ma.get("base_cooldown").getAsInt();
            if (ma.has("cooldown_decrease_per_level")) MagicalAttunement.cooldownDecreasePerLevel = ma.get("cooldown_decrease_per_level").getAsInt();
        }
    }
    
    private static void loadPerfectionist(JsonObject json) {
        if (json.has("perfectionist")) {
            JsonObject perf = json.getAsJsonObject("perfectionist");
            if (perf.has("enabled")) Perfectionist.enabled = perf.get("enabled").getAsBoolean();
            if (perf.has("category")) Perfectionist.category = perf.get("category").getAsString();
            if (perf.has("max_level")) Perfectionist.maxLevel = perf.get("max_level").getAsInt();
            if (perf.has("base_attack_speed_boost")) Perfectionist.baseAttackSpeedBoost = perf.get("base_attack_speed_boost").getAsDouble();
            if (perf.has("attack_speed_per_level")) Perfectionist.attackSpeedPerLevel = perf.get("attack_speed_per_level").getAsDouble();
            if (perf.has("base_cast_speed_boost")) Perfectionist.baseCastSpeedBoost = perf.get("base_cast_speed_boost").getAsDouble();
            if (perf.has("cast_speed_per_level")) Perfectionist.castSpeedPerLevel = perf.get("cast_speed_per_level").getAsDouble();
            if (perf.has("duration")) Perfectionist.duration = perf.get("duration").getAsInt();
        }
    }
    
    private static void loadHunter(JsonObject json) {
        if (json.has("hunter")) {
            JsonObject hunter = json.getAsJsonObject("hunter");
            if (hunter.has("enabled")) Hunter.enabled = hunter.get("enabled").getAsBoolean();
            if (hunter.has("category")) Hunter.category = hunter.get("category").getAsString();
            if (hunter.has("max_level")) Hunter.maxLevel = hunter.get("max_level").getAsInt();
            if (hunter.has("base_crit_damage_boost")) Hunter.baseCritDamageBoost = hunter.get("base_crit_damage_boost").getAsDouble();
            if (hunter.has("crit_damage_per_level")) Hunter.critDamagePerLevel = hunter.get("crit_damage_per_level").getAsDouble();
            if (hunter.has("flat_crit_chance")) Hunter.flatCritChance = hunter.get("flat_crit_chance").getAsDouble();
        }
    }
    
    private static void loadMultiscale(JsonObject json) {
        if (json.has("multiscale")) {
            JsonObject multi = json.getAsJsonObject("multiscale");
            if (multi.has("enabled")) Multiscale.enabled = multi.get("enabled").getAsBoolean();
            if (multi.has("category")) Multiscale.category = multi.get("category").getAsString();
            if (multi.has("max_level")) Multiscale.maxLevel = multi.get("max_level").getAsInt();
            if (multi.has("flat_armor_per_level")) Multiscale.flatArmorPerLevel = multi.get("flat_armor_per_level").getAsDouble();
            if (multi.has("percent_armor_per_level")) Multiscale.percentArmorPerLevel = multi.get("percent_armor_per_level").getAsDouble();
        }
    }
    
    private static void loadInvigoratingDefenses(JsonObject json) {
        if (json.has("invigorating_defenses")) {
            JsonObject invig = json.getAsJsonObject("invigorating_defenses");
            if (invig.has("enabled")) InvigoratingDefenses.enabled = invig.get("enabled").getAsBoolean();
            if (invig.has("category")) InvigoratingDefenses.category = invig.get("category").getAsString();
            if (invig.has("max_level")) InvigoratingDefenses.maxLevel = invig.get("max_level").getAsInt();
            if (invig.has("base_movement_speed_boost")) InvigoratingDefenses.baseMovementSpeedBoost = invig.get("base_movement_speed_boost").getAsDouble();
            if (invig.has("speed_boost_per_level")) InvigoratingDefenses.speedBoostPerLevel = invig.get("speed_boost_per_level").getAsDouble();
            if (invig.has("base_health_restored")) InvigoratingDefenses.baseHealthRestored = invig.get("base_health_restored").getAsDouble();
            if (invig.has("health_restored_per_level")) InvigoratingDefenses.healthRestoredPerLevel = invig.get("health_restored_per_level").getAsDouble();
            if (invig.has("cooldown")) InvigoratingDefenses.cooldown = invig.get("cooldown").getAsInt();
            if (invig.has("base_duration")) InvigoratingDefenses.baseDuration = invig.get("base_duration").getAsInt();
            if (invig.has("duration_per_level")) InvigoratingDefenses.durationPerLevel = invig.get("duration_per_level").getAsInt();
        }
    }
    
    private static void loadLifeSurge(JsonObject json) {
        if (json.has("life_surge")) {
            JsonObject life = json.getAsJsonObject("life_surge");
            if (life.has("enabled")) LifeSurge.enabled = life.get("enabled").getAsBoolean();
            if (life.has("category")) LifeSurge.category = life.get("category").getAsString();
            if (life.has("max_level")) LifeSurge.maxLevel = life.get("max_level").getAsInt();
            if (life.has("health_threshold")) LifeSurge.healthThreshold = life.get("health_threshold").getAsDouble();
            if (life.has("flat_armor_per_level")) LifeSurge.flatArmorPerLevel = life.get("flat_armor_per_level").getAsDouble();
            if (life.has("percent_armor_per_level")) LifeSurge.percentArmorPerLevel = life.get("percent_armor_per_level").getAsDouble();
            if (life.has("flat_lifesteal_per_level")) LifeSurge.flatLifestealPerLevel = life.get("flat_lifesteal_per_level").getAsDouble();
            if (life.has("flat_spell_steal_per_level")) LifeSurge.flatSpellStealPerLevel = life.get("flat_spell_steal_per_level").getAsDouble();
            if (life.has("duration")) LifeSurge.duration = life.get("duration").getAsInt();
            if (life.has("base_cooldown")) LifeSurge.baseCooldown = life.get("base_cooldown").getAsInt();
            if (life.has("cooldown_decrease_per_level")) LifeSurge.cooldownDecreasePerLevel = life.get("cooldown_decrease_per_level").getAsInt();
        }
    }
    
    private static void loadShadowWalker(JsonObject json) {
        if (json.has("shadow_walker")) {
            JsonObject shadow = json.getAsJsonObject("shadow_walker");
            if (shadow.has("enabled")) ShadowWalker.enabled = shadow.get("enabled").getAsBoolean();
            if (shadow.has("category")) ShadowWalker.category = shadow.get("category").getAsString();
            if (shadow.has("max_level")) ShadowWalker.maxLevel = shadow.get("max_level").getAsInt();
            if (shadow.has("invisibility_duration")) ShadowWalker.invisibilityDuration = shadow.get("invisibility_duration").getAsInt();
            if (shadow.has("movement_speed_duration")) ShadowWalker.movementSpeedDuration = shadow.get("movement_speed_duration").getAsInt();
            if (shadow.has("movement_speed_percent")) ShadowWalker.movementSpeedPercent = shadow.get("movement_speed_percent").getAsDouble();
            if (shadow.has("base_damage_amplifier")) ShadowWalker.baseDamageAmplifier = shadow.get("base_damage_amplifier").getAsDouble();
            if (shadow.has("damage_amplifier_per_level")) ShadowWalker.damageAmplifierPerLevel = shadow.get("damage_amplifier_per_level").getAsDouble();
        }
    }
    
    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            json.addProperty("enabled", enableEnchantmentModule);
            
            saveEnchantmentCap(json);
            saveReprisal(json);
            saveMomentum(json);
            saveSpellblade(json);
            saveMagicalAttunement(json);
            savePerfectionist(json);
            saveHunter(json);
            saveMultiscale(json);
            saveInvigoratingDefenses(json);
            saveLifeSurge(json);
            saveShadowWalker(json);
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(json, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void saveEnchantmentCap(JsonObject json) {
        JsonObject cap = new JsonObject();
        cap.addProperty("_description", "Settings for limiting the number of enchantments on items.");
        cap.addProperty("enabled", EnchantmentCap.enabled);
        
        JsonObject itemCaps = new JsonObject();
        for (Map.Entry<String, Integer> entry : EnchantmentCap.itemCaps.entrySet()) {
            itemCaps.addProperty(entry.getKey(), entry.getValue());
        }
        cap.add("item_caps", itemCaps);
        json.add("enchantment_cap", cap);
    }
    
    private static void saveReprisal(JsonObject json) {
        JsonObject reprisal = new JsonObject();
        reprisal.addProperty("_description", "Reprisal: Boosts damage after taking a hit.");
        reprisal.addProperty("enabled", Reprisal.enabled);
        reprisal.addProperty("category", Reprisal.category);
        reprisal.addProperty("max_level", Reprisal.maxLevel);
        reprisal.addProperty("_base_damage_boost_description", "Base damage boost in percentage.");
        reprisal.addProperty("base_damage_boost", Reprisal.baseDamageBoost);
        reprisal.addProperty("_damage_boost_per_level_description", "Additional damage boost in percentage per enchantment level.");
        reprisal.addProperty("damage_boost_per_level", Reprisal.damageBoostPerLevel);
        reprisal.addProperty("_base_duration_description", "Base duration of the effect in ticks (20 ticks = 1 second).");
        reprisal.addProperty("base_duration", Reprisal.baseDuration);
        reprisal.addProperty("_duration_per_level_description", "Additional duration in ticks per enchantment level.");
        reprisal.addProperty("duration_per_level", Reprisal.durationPerLevel);
        reprisal.addProperty("_base_cooldown_description", "Base cooldown in ticks.");
        reprisal.addProperty("base_cooldown", Reprisal.baseCooldown);
        reprisal.addProperty("_cooldown_reduction_per_level_description", "Cooldown reduction in ticks per enchantment level.");
        reprisal.addProperty("cooldown_reduction_per_level", Reprisal.cooldownReductionPerLevel);
        json.add("reprisal", reprisal);
    }
    
    private static void saveMomentum(JsonObject json) {
        JsonObject momentum = new JsonObject();
        momentum.addProperty("_description", "Momentum: Gain damage stacks on kills.");
        momentum.addProperty("enabled", Momentum.enabled);
        momentum.addProperty("category", Momentum.category);
        momentum.addProperty("max_level", Momentum.maxLevel);
        momentum.addProperty("_base_max_stacks_description", "Base maximum number of stacks.");
        momentum.addProperty("base_max_stacks", Momentum.baseMaxStacks);
        momentum.addProperty("_max_stack_increase_per_level_description", "Increase in max stacks per enchantment level.");
        momentum.addProperty("max_stack_increase_per_level", Momentum.maxStackIncreasePerLevel);
        momentum.addProperty("_stack_duration_description", "Duration of each stack in ticks.");
        momentum.addProperty("stack_duration", Momentum.stackDuration);
        momentum.addProperty("_damage_boost_per_stack_description", "Damage boost in percentage per stack.");
        momentum.addProperty("damage_boost_per_stack", Momentum.damageBoostPerStack);
        momentum.addProperty("_damage_boost_per_level_description", "Additional damage boost in percentage per level.");
        momentum.addProperty("damage_boost_per_level", Momentum.damageBoostPerLevel);
        momentum.addProperty("_kill_stacks_description", "Number of stacks gained on killing a mob.");
        momentum.addProperty("kill_stacks", Momentum.killStacks);
        momentum.addProperty("_ally_kill_stacks_description", "Number of stacks gained when an ally kills a mob.");
        momentum.addProperty("ally_kill_stacks", Momentum.allyKillStacks);
        json.add("momentum", momentum);
    }
    
    private static void saveSpellblade(JsonObject json) {
        JsonObject spellblade = new JsonObject();
        spellblade.addProperty("_description", "Spellblade: Grants buffs on spell casts and attacks.");
        spellblade.addProperty("enabled", Spellblade.enabled);
        spellblade.addProperty("category", Spellblade.category);
        spellblade.addProperty("max_level", Spellblade.maxLevel);
        
        JsonObject passiveA = new JsonObject();
        passiveA.addProperty("_description", "Passive A: Boosts spell power after a melee or ranged attack.");
        passiveA.addProperty("_base_melee_spell_power_boost_description", "Base spell power boost for melee attacks in percentage.");
        passiveA.addProperty("base_melee_spell_power_boost", Spellblade.PassiveA.baseMeleeSpellPowerBoost);
        passiveA.addProperty("_melee_boost_per_level_description", "Additional spell power boost for melee attacks per level in percentage.");
        passiveA.addProperty("melee_boost_per_level", Spellblade.PassiveA.meleeBoostPerLevel);
        passiveA.addProperty("_base_ranged_spell_power_boost_description", "Base spell power boost for ranged attacks in percentage.");
        passiveA.addProperty("base_ranged_spell_power_boost", Spellblade.PassiveA.baseRangedSpellPowerBoost);
        passiveA.addProperty("_ranged_boost_per_level_description", "Additional spell power boost for ranged attacks per level in percentage.");
        passiveA.addProperty("ranged_boost_per_level", Spellblade.PassiveA.rangedBoostPerLevel);
        passiveA.addProperty("_duration_description", "Duration of the boost in ticks.");
        passiveA.addProperty("duration", Spellblade.PassiveA.duration);
        passiveA.addProperty("_base_cooldown_description", "Base cooldown in ticks.");
        passiveA.addProperty("base_cooldown", Spellblade.PassiveA.baseCooldown);
        passiveA.addProperty("_cooldown_decrease_per_level_description", "Cooldown decrease per level in ticks.");
        passiveA.addProperty("cooldown_decrease_per_level", Spellblade.PassiveA.cooldownDecreasePerLevel);
        spellblade.add("passive_a", passiveA);
        
        JsonObject passiveB = new JsonObject();
        passiveB.addProperty("_description", "Passive B: Boosts attack damage after a spell cast.");
        passiveB.addProperty("_base_arrow_damage_boost_description", "Base arrow damage boost in percentage.");
        passiveB.addProperty("base_arrow_damage_boost", Spellblade.PassiveB.baseArrowDamageBoost);
        passiveB.addProperty("_arrow_boost_per_level_description", "Additional arrow damage boost per level in percentage.");
        passiveB.addProperty("arrow_boost_per_level", Spellblade.PassiveB.arrowBoostPerLevel);
        passiveB.addProperty("_base_attack_damage_boost_description", "Base attack damage boost in percentage.");
        passiveB.addProperty("base_attack_damage_boost", Spellblade.PassiveB.baseAttackDamageBoost);
        passiveB.addProperty("_attack_boost_per_level_description", "Additional attack damage boost per level in percentage.");
        passiveB.addProperty("attack_boost_per_level", Spellblade.PassiveB.attackBoostPerLevel);
        passiveB.addProperty("_duration_description", "Duration of the boost in ticks.");
        passiveB.addProperty("duration", Spellblade.PassiveB.duration);
        passiveB.addProperty("_base_cooldown_description", "Base cooldown in ticks.");
        passiveB.addProperty("base_cooldown", Spellblade.PassiveB.baseCooldown);
        passiveB.addProperty("_cooldown_decrease_per_level_description", "Cooldown decrease per level in ticks.");
        passiveB.addProperty("cooldown_decrease_per_level", Spellblade.PassiveB.cooldownDecreasePerLevel);
        spellblade.add("passive_b", passiveB);
        
        json.add("spellblade", spellblade);
    }
    
    private static void saveMagicalAttunement(JsonObject json) {
        JsonObject ma = new JsonObject();
        ma.addProperty("_description", "Magical Attunement: Deals extra damage based on mana when using spells.");
        ma.addProperty("enabled", MagicalAttunement.enabled);
        ma.addProperty("category", MagicalAttunement.category);
        ma.addProperty("max_level", MagicalAttunement.maxLevel);
        ma.addProperty("_base_damage_per_level_description", "Base damage dealt per level of enchantment.");
        ma.addProperty("base_damage_per_level", MagicalAttunement.baseDamagePerLevel);
        ma.addProperty("_max_mana_percent_description", "Maximum mana percentage that contributes to the damage.");
        ma.addProperty("max_mana_percent", MagicalAttunement.maxManaPercent);
        ma.addProperty("_duration_description", "Duration of the effect in ticks.");
        ma.addProperty("duration", MagicalAttunement.duration);
        ma.addProperty("_base_cooldown_description", "Base cooldown in ticks.");
        ma.addProperty("base_cooldown", MagicalAttunement.baseCooldown);
        ma.addProperty("_cooldown_decrease_per_level_description", "Cooldown decrease per level in ticks.");
        ma.addProperty("cooldown_decrease_per_level", MagicalAttunement.cooldownDecreasePerLevel);
        json.add("magical_attunement", ma);
    }
    
    private static void savePerfectionist(JsonObject json) {
        JsonObject perf = new JsonObject();
        perf.addProperty("_description", "Perfectionist: Boosts attack and cast speed at full health.");
        perf.addProperty("enabled", Perfectionist.enabled);
        perf.addProperty("category", Perfectionist.category);
        perf.addProperty("max_level", Perfectionist.maxLevel);
        perf.addProperty("_base_attack_speed_boost_description", "Base attack speed boost in percentage.");
        perf.addProperty("base_attack_speed_boost", Perfectionist.baseAttackSpeedBoost);
        perf.addProperty("_attack_speed_per_level_description", "Additional attack speed boost per level in percentage.");
        perf.addProperty("attack_speed_per_level", Perfectionist.attackSpeedPerLevel);
        perf.addProperty("_base_cast_speed_boost_description", "Base cast speed boost in percentage.");
        perf.addProperty("base_cast_speed_boost", Perfectionist.baseCastSpeedBoost);
        perf.addProperty("_cast_speed_per_level_description", "Additional cast speed boost per level in percentage.");
        perf.addProperty("cast_speed_per_level", Perfectionist.castSpeedPerLevel);
        perf.addProperty("_duration_description", "Duration of the boost in ticks.");
        perf.addProperty("duration", Perfectionist.duration);
        json.add("perfectionist", perf);
    }
    
    private static void saveHunter(JsonObject json) {
        JsonObject hunter = new JsonObject();
        hunter.addProperty("_description", "Hunter: Increases critical strike damage and chance.");
        hunter.addProperty("enabled", Hunter.enabled);
        hunter.addProperty("category", Hunter.category);
        hunter.addProperty("max_level", Hunter.maxLevel);
        hunter.addProperty("_base_crit_damage_boost_description", "Base critical damage boost in percentage.");
        hunter.addProperty("base_crit_damage_boost", Hunter.baseCritDamageBoost);
        hunter.addProperty("_crit_damage_per_level_description", "Additional critical damage boost per level in percentage.");
        hunter.addProperty("crit_damage_per_level", Hunter.critDamagePerLevel);
        hunter.addProperty("_flat_crit_chance_description", "Flat critical strike chance increase in percentage.");
        hunter.addProperty("flat_crit_chance", Hunter.flatCritChance);
        json.add("hunter", hunter);
    }
    
    private static void saveMultiscale(JsonObject json) {
        JsonObject multi = new JsonObject();
        multi.addProperty("_description", "Multiscale: Provides flat and percentage-based armor.");
        multi.addProperty("enabled", Multiscale.enabled);
        multi.addProperty("category", Multiscale.category);
        multi.addProperty("max_level", Multiscale.maxLevel);
        multi.addProperty("_flat_armor_per_level_description", "Flat armor increase per enchantment level.");
        multi.addProperty("flat_armor_per_level", Multiscale.flatArmorPerLevel);
        multi.addProperty("_percent_armor_per_level_description", "Percentage armor increase per enchantment level.");
        multi.addProperty("percent_armor_per_level", Multiscale.percentArmorPerLevel);
        json.add("multiscale", multi);
    }
    
    private static void saveInvigoratingDefenses(JsonObject json) {
        JsonObject invig = new JsonObject();
        invig.addProperty("_description", "Invigorating Defenses: Grants movement speed and health regeneration when blocking an attack.");
        invig.addProperty("enabled", InvigoratingDefenses.enabled);
        invig.addProperty("category", InvigoratingDefenses.category);
        invig.addProperty("max_level", InvigoratingDefenses.maxLevel);
        invig.addProperty("_base_movement_speed_boost_description", "Base movement speed boost in percentage.");
        invig.addProperty("base_movement_speed_boost", InvigoratingDefenses.baseMovementSpeedBoost);
        invig.addProperty("_speed_boost_per_level_description", "Additional movement speed boost per level in percentage.");
        invig.addProperty("speed_boost_per_level", InvigoratingDefenses.speedBoostPerLevel);
        invig.addProperty("_base_health_restored_description", "Base health restored.");
        invig.addProperty("base_health_restored", InvigoratingDefenses.baseHealthRestored);
        invig.addProperty("_health_restored_per_level_description", "Additional health restored per level.");
        invig.addProperty("health_restored_per_level", InvigoratingDefenses.healthRestoredPerLevel);
        invig.addProperty("_cooldown_description", "Cooldown in ticks.");
        invig.addProperty("cooldown", InvigoratingDefenses.cooldown);
        invig.addProperty("_base_duration_description", "Base duration of the effect in ticks.");
        invig.addProperty("base_duration", InvigoratingDefenses.baseDuration);
        invig.addProperty("_duration_per_level_description", "Additional duration per level in ticks.");
        invig.addProperty("duration_per_level", InvigoratingDefenses.durationPerLevel);
        json.add("invigorating_defenses", invig);
    }
    
    private static void saveLifeSurge(JsonObject json) {
        JsonObject life = new JsonObject();
        life.addProperty("_description", "Life Surge: Grants defensive stats and lifesteal/spellsteal when low on health.");
        life.addProperty("enabled", LifeSurge.enabled);
        life.addProperty("category", LifeSurge.category);
        life.addProperty("max_level", LifeSurge.maxLevel);
        life.addProperty("_health_threshold_description", "Health percentage below which the effect activates.");
        life.addProperty("health_threshold", LifeSurge.healthThreshold);
        life.addProperty("_flat_armor_per_level_description", "Flat armor increase per enchantment level.");
        life.addProperty("flat_armor_per_level", LifeSurge.flatArmorPerLevel);
        life.addProperty("_percent_armor_per_level_description", "Percentage armor increase per enchantment level.");
        life.addProperty("percent_armor_per_level", LifeSurge.percentArmorPerLevel);
        life.addProperty("_flat_lifesteal_per_level_description", "Flat lifesteal increase per enchantment level.");
        life.addProperty("flat_lifesteal_per_level", LifeSurge.flatLifestealPerLevel);
        life.addProperty("_flat_spell_steal_per_level_description", "Flat spell steal increase per enchantment level.");
        life.addProperty("flat_spell_steal_per_level", LifeSurge.flatSpellStealPerLevel);
        life.addProperty("_duration_description", "Duration of the effect in ticks.");
        life.addProperty("duration", LifeSurge.duration);
        life.addProperty("_base_cooldown_description", "Base cooldown in ticks.");
        life.addProperty("base_cooldown", LifeSurge.baseCooldown);
        life.addProperty("_cooldown_decrease_per_level_description", "Cooldown decrease per level in ticks.");
        life.addProperty("cooldown_decrease_per_level", LifeSurge.cooldownDecreasePerLevel);
        json.add("life_surge", life);
    }
    
    private static void saveShadowWalker(JsonObject json) {
        JsonObject shadow = new JsonObject();
        shadow.addProperty("_description", "Shadow Walker: Grants invisibility and a damage boost after sneaking.");
        shadow.addProperty("enabled", ShadowWalker.enabled);
        shadow.addProperty("category", ShadowWalker.category);
        shadow.addProperty("max_level", ShadowWalker.maxLevel);
        shadow.addProperty("_invisibility_duration_description", "Duration of the invisibility in ticks.");
        shadow.addProperty("invisibility_duration", ShadowWalker.invisibilityDuration);
        shadow.addProperty("_movement_speed_duration_description", "Duration of the movement speed boost in ticks.");
        shadow.addProperty("movement_speed_duration", ShadowWalker.movementSpeedDuration);
        shadow.addProperty("_movement_speed_percent_description", "Movement speed boost in percentage.");
        shadow.addProperty("movement_speed_percent", ShadowWalker.movementSpeedPercent);
        shadow.addProperty("_base_damage_amplifier_description", "Base damage amplifier in percentage.");
        shadow.addProperty("base_damage_amplifier", ShadowWalker.baseDamageAmplifier);
        shadow.addProperty("_damage_amplifier_per_level_description", "Additional damage amplifier per level in percentage.");
        shadow.addProperty("damage_amplifier_per_level", ShadowWalker.damageAmplifierPerLevel);
        json.add("shadow_walker", shadow);
    }
}