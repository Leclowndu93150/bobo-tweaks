package com.leclowndu93150.bobo_tweaks.additional.enchantments.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
        public static int maxLevel = 3;
        public static double baseDamagePerLevel = 3.0;
        public static double maxManaPercent = 0.1;
        public static int duration = 20;
        public static int baseCooldown = 200;
        public static int cooldownDecreasePerLevel = 20;
    }
    
    public static class Perfectionist {
        public static boolean enabled = true;
        public static int maxLevel = 3;
        public static double baseAttackSpeedBoost = 10.0;
        public static double attackSpeedPerLevel = 5.0;
        public static double baseCastSpeedBoost = 15.0;
        public static double castSpeedPerLevel = 5.0;
        public static int duration = 100;
    }
    
    public static class Hunter {
        public static boolean enabled = true;
        public static int maxLevel = 3;
        public static double baseCritDamageBoost = 20.0;
        public static double critDamagePerLevel = 10.0;
        public static double flatCritChance = 15.0;
    }
    
    public static class Multiscale {
        public static boolean enabled = true;
        public static int maxLevel = 3;
        public static double flatArmorPerLevel = 2.0;
        public static double percentArmorPerLevel = 5.0;
    }
    
    public static class InvigoratingDefenses {
        public static boolean enabled = true;
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
        public static int maxLevel = 3;
        public static int invisibilityDuration = 60;
        public static int movementSpeedDuration = 60;
        public static double movementSpeedPercent = 30.0;
        public static double baseDamageAmplifier = 20.0;
        public static double damageAmplifierPerLevel = 10.0;
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
            if (multi.has("max_level")) Multiscale.maxLevel = multi.get("max_level").getAsInt();
            if (multi.has("flat_armor_per_level")) Multiscale.flatArmorPerLevel = multi.get("flat_armor_per_level").getAsDouble();
            if (multi.has("percent_armor_per_level")) Multiscale.percentArmorPerLevel = multi.get("percent_armor_per_level").getAsDouble();
        }
    }
    
    private static void loadInvigoratingDefenses(JsonObject json) {
        if (json.has("invigorating_defenses")) {
            JsonObject invig = json.getAsJsonObject("invigorating_defenses");
            if (invig.has("enabled")) InvigoratingDefenses.enabled = invig.get("enabled").getAsBoolean();
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
        reprisal.addProperty("enabled", Reprisal.enabled);
        reprisal.addProperty("max_level", Reprisal.maxLevel);
        reprisal.addProperty("base_damage_boost", Reprisal.baseDamageBoost);
        reprisal.addProperty("damage_boost_per_level", Reprisal.damageBoostPerLevel);
        reprisal.addProperty("base_duration", Reprisal.baseDuration);
        reprisal.addProperty("duration_per_level", Reprisal.durationPerLevel);
        reprisal.addProperty("base_cooldown", Reprisal.baseCooldown);
        reprisal.addProperty("cooldown_reduction_per_level", Reprisal.cooldownReductionPerLevel);
        json.add("reprisal", reprisal);
    }
    
    private static void saveMomentum(JsonObject json) {
        JsonObject momentum = new JsonObject();
        momentum.addProperty("enabled", Momentum.enabled);
        momentum.addProperty("max_level", Momentum.maxLevel);
        momentum.addProperty("base_max_stacks", Momentum.baseMaxStacks);
        momentum.addProperty("max_stack_increase_per_level", Momentum.maxStackIncreasePerLevel);
        momentum.addProperty("stack_duration", Momentum.stackDuration);
        momentum.addProperty("damage_boost_per_stack", Momentum.damageBoostPerStack);
        momentum.addProperty("damage_boost_per_level", Momentum.damageBoostPerLevel);
        momentum.addProperty("kill_stacks", Momentum.killStacks);
        momentum.addProperty("ally_kill_stacks", Momentum.allyKillStacks);
        json.add("momentum", momentum);
    }
    
    private static void saveSpellblade(JsonObject json) {
        JsonObject spellblade = new JsonObject();
        spellblade.addProperty("enabled", Spellblade.enabled);
        spellblade.addProperty("max_level", Spellblade.maxLevel);
        
        JsonObject passiveA = new JsonObject();
        passiveA.addProperty("base_melee_spell_power_boost", Spellblade.PassiveA.baseMeleeSpellPowerBoost);
        passiveA.addProperty("melee_boost_per_level", Spellblade.PassiveA.meleeBoostPerLevel);
        passiveA.addProperty("base_ranged_spell_power_boost", Spellblade.PassiveA.baseRangedSpellPowerBoost);
        passiveA.addProperty("ranged_boost_per_level", Spellblade.PassiveA.rangedBoostPerLevel);
        passiveA.addProperty("duration", Spellblade.PassiveA.duration);
        passiveA.addProperty("base_cooldown", Spellblade.PassiveA.baseCooldown);
        passiveA.addProperty("cooldown_decrease_per_level", Spellblade.PassiveA.cooldownDecreasePerLevel);
        spellblade.add("passive_a", passiveA);
        
        JsonObject passiveB = new JsonObject();
        passiveB.addProperty("base_arrow_damage_boost", Spellblade.PassiveB.baseArrowDamageBoost);
        passiveB.addProperty("arrow_boost_per_level", Spellblade.PassiveB.arrowBoostPerLevel);
        passiveB.addProperty("base_attack_damage_boost", Spellblade.PassiveB.baseAttackDamageBoost);
        passiveB.addProperty("attack_boost_per_level", Spellblade.PassiveB.attackBoostPerLevel);
        passiveB.addProperty("duration", Spellblade.PassiveB.duration);
        passiveB.addProperty("base_cooldown", Spellblade.PassiveB.baseCooldown);
        passiveB.addProperty("cooldown_decrease_per_level", Spellblade.PassiveB.cooldownDecreasePerLevel);
        spellblade.add("passive_b", passiveB);
        
        json.add("spellblade", spellblade);
    }
    
    private static void saveMagicalAttunement(JsonObject json) {
        JsonObject ma = new JsonObject();
        ma.addProperty("enabled", MagicalAttunement.enabled);
        ma.addProperty("max_level", MagicalAttunement.maxLevel);
        ma.addProperty("base_damage_per_level", MagicalAttunement.baseDamagePerLevel);
        ma.addProperty("max_mana_percent", MagicalAttunement.maxManaPercent);
        ma.addProperty("duration", MagicalAttunement.duration);
        ma.addProperty("base_cooldown", MagicalAttunement.baseCooldown);
        ma.addProperty("cooldown_decrease_per_level", MagicalAttunement.cooldownDecreasePerLevel);
        json.add("magical_attunement", ma);
    }
    
    private static void savePerfectionist(JsonObject json) {
        JsonObject perf = new JsonObject();
        perf.addProperty("enabled", Perfectionist.enabled);
        perf.addProperty("max_level", Perfectionist.maxLevel);
        perf.addProperty("base_attack_speed_boost", Perfectionist.baseAttackSpeedBoost);
        perf.addProperty("attack_speed_per_level", Perfectionist.attackSpeedPerLevel);
        perf.addProperty("base_cast_speed_boost", Perfectionist.baseCastSpeedBoost);
        perf.addProperty("cast_speed_per_level", Perfectionist.castSpeedPerLevel);
        perf.addProperty("duration", Perfectionist.duration);
        json.add("perfectionist", perf);
    }
    
    private static void saveHunter(JsonObject json) {
        JsonObject hunter = new JsonObject();
        hunter.addProperty("enabled", Hunter.enabled);
        hunter.addProperty("max_level", Hunter.maxLevel);
        hunter.addProperty("base_crit_damage_boost", Hunter.baseCritDamageBoost);
        hunter.addProperty("crit_damage_per_level", Hunter.critDamagePerLevel);
        hunter.addProperty("flat_crit_chance", Hunter.flatCritChance);
        json.add("hunter", hunter);
    }
    
    private static void saveMultiscale(JsonObject json) {
        JsonObject multi = new JsonObject();
        multi.addProperty("enabled", Multiscale.enabled);
        multi.addProperty("max_level", Multiscale.maxLevel);
        multi.addProperty("flat_armor_per_level", Multiscale.flatArmorPerLevel);
        multi.addProperty("percent_armor_per_level", Multiscale.percentArmorPerLevel);
        json.add("multiscale", multi);
    }
    
    private static void saveInvigoratingDefenses(JsonObject json) {
        JsonObject invig = new JsonObject();
        invig.addProperty("enabled", InvigoratingDefenses.enabled);
        invig.addProperty("max_level", InvigoratingDefenses.maxLevel);
        invig.addProperty("base_movement_speed_boost", InvigoratingDefenses.baseMovementSpeedBoost);
        invig.addProperty("speed_boost_per_level", InvigoratingDefenses.speedBoostPerLevel);
        invig.addProperty("base_health_restored", InvigoratingDefenses.baseHealthRestored);
        invig.addProperty("health_restored_per_level", InvigoratingDefenses.healthRestoredPerLevel);
        invig.addProperty("cooldown", InvigoratingDefenses.cooldown);
        invig.addProperty("base_duration", InvigoratingDefenses.baseDuration);
        invig.addProperty("duration_per_level", InvigoratingDefenses.durationPerLevel);
        json.add("invigorating_defenses", invig);
    }
    
    private static void saveLifeSurge(JsonObject json) {
        JsonObject life = new JsonObject();
        life.addProperty("enabled", LifeSurge.enabled);
        life.addProperty("max_level", LifeSurge.maxLevel);
        life.addProperty("health_threshold", LifeSurge.healthThreshold);
        life.addProperty("flat_armor_per_level", LifeSurge.flatArmorPerLevel);
        life.addProperty("percent_armor_per_level", LifeSurge.percentArmorPerLevel);
        life.addProperty("flat_lifesteal_per_level", LifeSurge.flatLifestealPerLevel);
        life.addProperty("flat_spell_steal_per_level", LifeSurge.flatSpellStealPerLevel);
        life.addProperty("duration", LifeSurge.duration);
        life.addProperty("base_cooldown", LifeSurge.baseCooldown);
        life.addProperty("cooldown_decrease_per_level", LifeSurge.cooldownDecreasePerLevel);
        json.add("life_surge", life);
    }
    
    private static void saveShadowWalker(JsonObject json) {
        JsonObject shadow = new JsonObject();
        shadow.addProperty("enabled", ShadowWalker.enabled);
        shadow.addProperty("max_level", ShadowWalker.maxLevel);
        shadow.addProperty("invisibility_duration", ShadowWalker.invisibilityDuration);
        shadow.addProperty("movement_speed_duration", ShadowWalker.movementSpeedDuration);
        shadow.addProperty("movement_speed_percent", ShadowWalker.movementSpeedPercent);
        shadow.addProperty("base_damage_amplifier", ShadowWalker.baseDamageAmplifier);
        shadow.addProperty("damage_amplifier_per_level", ShadowWalker.damageAmplifierPerLevel);
        json.add("shadow_walker", shadow);
    }
}