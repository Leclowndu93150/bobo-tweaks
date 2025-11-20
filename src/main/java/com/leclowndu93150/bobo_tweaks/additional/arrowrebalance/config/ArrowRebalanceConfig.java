package com.leclowndu93150.bobo_tweaks.additional.arrowrebalance.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ArrowRebalanceConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("bobotweaks/arrow_rebalance.json");
    
    private static ArrowRebalanceData data = new ArrowRebalanceData();
    
    public static class ArrowRebalanceData {
        public boolean enabled = true;
        public double distanceThreshold = 20.0;
        public double damageReductionPercent = 75.0;
        public Map<String, ProjectileConfig> projectileOverrides = new HashMap<>();
        
        public ArrowRebalanceData() {
            ProjectileConfig arrowConfig = new ProjectileConfig();
            arrowConfig.enabled = true;
            arrowConfig.distanceThreshold = 20.0;
            arrowConfig.damageReductionPercent = 75.0;
            projectileOverrides.put("minecraft:arrow", arrowConfig);
            
            ProjectileConfig spectralArrowConfig = new ProjectileConfig();
            spectralArrowConfig.enabled = true;
            spectralArrowConfig.distanceThreshold = 20.0;
            spectralArrowConfig.damageReductionPercent = 75.0;
            projectileOverrides.put("minecraft:spectral_arrow", spectralArrowConfig);
            
            ProjectileConfig tridentConfig = new ProjectileConfig();
            tridentConfig.enabled = true;
            tridentConfig.distanceThreshold = 15.0;
            tridentConfig.damageReductionPercent = 60.0;
            projectileOverrides.put("minecraft:trident", tridentConfig);
        }
    }
    
    public static class ProjectileConfig {
        public boolean enabled = true;
        public double distanceThreshold = 20.0;
        public double damageReductionPercent = 75.0;
    }
    
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                save();
                return;
            }
            
            String json = Files.readString(CONFIG_PATH);
            data = GSON.fromJson(json, ArrowRebalanceData.class);
            
            if (data == null) {
                data = new ArrowRebalanceData();
                save();
            }
            
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to load arrow rebalance config", e);
            data = new ArrowRebalanceData();
        }
    }
    
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(data);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to save arrow rebalance config", e);
        }
    }
    
    public static boolean isEnabled() {
        return data.enabled;
    }
    
    public static double getDistanceThreshold() {
        return data.distanceThreshold;
    }
    
    public static double getDamageReductionPercent() {
        return data.damageReductionPercent;
    }
    
    public static ProjectileConfig getProjectileConfig(String projectileId) {
        return data.projectileOverrides.getOrDefault(projectileId, null);
    }
    
    public static boolean hasProjectileConfig(String projectileId) {
        return data.projectileOverrides.containsKey(projectileId);
    }
}
