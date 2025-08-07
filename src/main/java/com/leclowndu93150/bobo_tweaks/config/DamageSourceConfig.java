package com.leclowndu93150.bobo_tweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class DamageSourceConfig extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FOLDER = "bobo_tweaks/damage_multipliers";
    
    private static Map<ResourceLocation, DamageSourceEntry> damageSourceMultipliers = new HashMap<>();
    
    public DamageSourceConfig() {
        super(GSON, FOLDER);
    }
    
    public static class DamageSourceEntry {
        public boolean affects_players = true;
        public boolean affects_mobs = true;
        public double damage_mult = 1.0;
        
        public DamageSourceEntry() {}
        
        public DamageSourceEntry(boolean affectsPlayers, boolean affectsMobs, double damageMult) {
            this.affects_players = affectsPlayers;
            this.affects_mobs = affectsMobs;
            this.damage_mult = damageMult;
        }
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        damageSourceMultipliers.clear();
        
        pObject.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                
                String damageSource = json.get("damage_source").getAsString();
                ResourceLocation damageSourceId = new ResourceLocation(damageSource);
                
                DamageSourceEntry entry = new DamageSourceEntry();
                entry.affects_players = json.has("affects_players") ? json.get("affects_players").getAsBoolean() : true;
                entry.affects_mobs = json.has("affects_mobs") ? json.get("affects_mobs").getAsBoolean() : true;
                entry.damage_mult = json.has("damage_multiplier") ? json.get("damage_multiplier").getAsDouble() : 1.0;
                
                damageSourceMultipliers.put(damageSourceId, entry);
                BoboTweaks.getLogger().debug("Loaded damage multiplier for {}: {}x", damageSourceId, entry.damage_mult);
            } catch (Exception e) {
                BoboTweaks.getLogger().error("Failed to load damage multiplier from {}", location, e);
            }
        });
        
        BoboTweaks.getLogger().info("Loaded {} damage source multipliers", damageSourceMultipliers.size());
    }
    
    
    public static DamageSourceEntry getMultiplier(ResourceLocation damageSource) {
        return damageSourceMultipliers.getOrDefault(damageSource, new DamageSourceEntry());
    }
    
    public static DamageSourceEntry getMultiplier(String damageSource) {
        // Try to parse as ResourceLocation, fallback to minecraft namespace
        ResourceLocation location;
        if (damageSource.contains(":")) {
            location = new ResourceLocation(damageSource);
        } else {
            location = new ResourceLocation("minecraft", damageSource);
        }
        return getMultiplier(location);
    }
    
    public static Map<ResourceLocation, DamageSourceEntry> getAllMultipliers() {
        return new HashMap<>(damageSourceMultipliers);
    }
}