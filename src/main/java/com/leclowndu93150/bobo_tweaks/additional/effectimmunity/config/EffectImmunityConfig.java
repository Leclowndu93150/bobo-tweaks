package com.leclowndu93150.bobo_tweaks.additional.effectimmunity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EffectImmunityConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("bobotweaks/effect_immunity.json");
    
    private static EffectImmunityData data = new EffectImmunityData();
    
    public static class EffectImmunityData {
        public GlobalImmunity globalImmunity = new GlobalImmunity();
        public Map<String, List<String>> mobSpecificImmunities = new HashMap<>();
        public Map<String, List<String>> effectSpecificImmunities = new HashMap<>();
        
        public EffectImmunityData() {
            // Default values
            globalImmunity.enabled = false;
            globalImmunity.effects = Arrays.asList("minecraft:poison", "minecraft:wither");
            
            mobSpecificImmunities.put("minecraft:zombie", Arrays.asList("minecraft:poison", "minecraft:wither"));
            mobSpecificImmunities.put("minecraft:skeleton", Arrays.asList("minecraft:slowness"));
            
            effectSpecificImmunities.put("minecraft:poison", Arrays.asList("minecraft:zombie", "minecraft:spider"));
            effectSpecificImmunities.put("minecraft:wither", Arrays.asList("minecraft:wither_skeleton", "minecraft:wither"));
        }
    }
    
    public static class GlobalImmunity {
        public boolean enabled = false;
        public List<String> effects = new ArrayList<>();
    }
    
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save();
                return;
            }
            
            String json = Files.readString(CONFIG_PATH);
            data = GSON.fromJson(json, EffectImmunityData.class);
            
            if (data == null) {
                data = new EffectImmunityData();
                save();
            }
            
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to load effect immunity config", e);
            data = new EffectImmunityData();
        }
    }
    
    public static void save() {
        try {
            String json = GSON.toJson(data);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to save effect immunity config", e);
        }
    }
    
    public static boolean isGlobalImmunityEnabled() {
        return data.globalImmunity.enabled;
    }
    
    public static List<String> getGlobalImmunityEffects() {
        return data.globalImmunity.effects;
    }
    
    public static Map<String, List<String>> getMobSpecificImmunities() {
        return data.mobSpecificImmunities;
    }
    
    public static Map<String, List<String>> getEffectSpecificImmunities() {
        return data.effectSpecificImmunities;
    }
    
    public static List<String> getImmunitiesForMob(String entityId) {
        return data.mobSpecificImmunities.getOrDefault(entityId, Collections.emptyList());
    }
    
    public static List<String> getImmuneMobsForEffect(String effectId) {
        return data.effectSpecificImmunities.getOrDefault(effectId, Collections.emptyList());
    }
}