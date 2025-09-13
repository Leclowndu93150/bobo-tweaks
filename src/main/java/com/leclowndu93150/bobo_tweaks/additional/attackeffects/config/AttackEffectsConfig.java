package com.leclowndu93150.bobo_tweaks.additional.attackeffects.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AttackEffectsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("bobotweaks/attack_effects.json");
    
    private static AttackEffectsData data = new AttackEffectsData();
    
    public static class AttackEffectsData {
        public Map<String, EntityAttackConfig> entityConfigs = new HashMap<>();
        
        public AttackEffectsData() {
            // Default examples
            EntityAttackConfig skeletonConfig = new EntityAttackConfig();
            skeletonConfig.appliedToPlayer = false;
            skeletonConfig.effectsApplied = Arrays.asList(
                new EffectConfig("minecraft:slowness", 400, 1),  // 20 seconds, amplifier 1
                new EffectConfig("minecraft:weakness", 300, 0)   // 15 seconds, amplifier 0
            );
            entityConfigs.put("minecraft:skeleton", skeletonConfig);
            
            EntityAttackConfig zombieConfig = new EntityAttackConfig();
            zombieConfig.appliedToPlayer = true;
            zombieConfig.effectsApplied = Arrays.asList(
                new EffectConfig("minecraft:poison", 200, 1)     // 10 seconds, amplifier 1
            );
            entityConfigs.put("minecraft:zombie", zombieConfig);
        }
    }
    
    public static class EntityAttackConfig {
        public boolean appliedToPlayer = false;
        public List<EffectConfig> effectsApplied = new ArrayList<>();
    }
    
    public static class EffectConfig {
        public String effectId;
        public int duration = 200; // 10 seconds in ticks
        public int amplifier = 0;
        
        public EffectConfig() {}
        
        public EffectConfig(String effectId, int duration, int amplifier) {
            this.effectId = effectId;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }
    
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save();
                return;
            }
            
            String json = Files.readString(CONFIG_PATH);
            data = GSON.fromJson(json, AttackEffectsData.class);
            
            if (data == null) {
                data = new AttackEffectsData();
                save();
            }
            
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to load attack effects config", e);
            data = new AttackEffectsData();
        }
    }
    
    public static void save() {
        try {
            String json = GSON.toJson(data);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to save attack effects config", e);
        }
    }
    
    public static Map<String, EntityAttackConfig> getEntityConfigs() {
        return data.entityConfigs;
    }
    
    public static EntityAttackConfig getConfigForEntity(String entityId) {
        return data.entityConfigs.get(entityId);
    }
    
    public static boolean hasConfigForEntity(String entityId) {
        return data.entityConfigs.containsKey(entityId);
    }
}