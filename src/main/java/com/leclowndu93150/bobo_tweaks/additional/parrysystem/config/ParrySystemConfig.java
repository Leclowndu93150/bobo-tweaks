package com.leclowndu93150.bobo_tweaks.additional.parrysystem.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ParrySystemConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/bobotweaks/parry_system.json");
    
    public static boolean enableParrySystem = true;
    public static double parryTimingWindowSeconds = 0.5;
    
    public static int energizedEffectDuration = 100;
    public static double energizedDamageAmplifier = 0.15;
    public static double energizedArmorBonus = 4.0;
    
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                
                if (json.has("enabled")) {
                    enableParrySystem = json.get("enabled").getAsBoolean();
                }
                
                if (json.has("parry_timing")) {
                    JsonObject parryTiming = json.getAsJsonObject("parry_timing");
                    if (parryTiming.has("window_seconds")) {
                        parryTimingWindowSeconds = parryTiming.get("window_seconds").getAsDouble();
                    }
                }
                
                if (json.has("energized_effect")) {
                    JsonObject energized = json.getAsJsonObject("energized_effect");
                    if (energized.has("duration_ticks")) {
                        energizedEffectDuration = energized.get("duration_ticks").getAsInt();
                    }
                    if (energized.has("damage_amplifier")) {
                        energizedDamageAmplifier = energized.get("damage_amplifier").getAsDouble();
                    }
                    if (energized.has("armor_bonus")) {
                        energizedArmorBonus = energized.get("armor_bonus").getAsDouble();
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }
    
    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            
            json.addProperty("enabled", enableParrySystem);
            json.addProperty("_comment_enabled", "Enable or disable the entire Parry System Module");
            
            JsonObject parryTiming = new JsonObject();
            parryTiming.addProperty("window_seconds", parryTimingWindowSeconds);
            parryTiming.addProperty("_comment_window", "Time window (in seconds) before getting hit to trigger a parry");
            json.add("parry_timing", parryTiming);
            
            JsonObject energized = new JsonObject();
            energized.addProperty("duration_ticks", energizedEffectDuration);
            energized.addProperty("_comment_duration", "Duration of the Energized effect in ticks (20 ticks = 1 second)");
            energized.addProperty("damage_amplifier", energizedDamageAmplifier);
            energized.addProperty("_comment_damage", "Damage amplifier bonus (0.15 = 15% increase)");
            energized.addProperty("armor_bonus", energizedArmorBonus);
            energized.addProperty("_comment_armor", "Armor points bonus");
            json.add("energized_effect", energized);
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(json, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
