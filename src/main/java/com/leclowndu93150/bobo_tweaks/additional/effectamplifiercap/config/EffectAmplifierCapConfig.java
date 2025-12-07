package com.leclowndu93150.bobo_tweaks.additional.effectamplifiercap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EffectAmplifierCapConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("bobotweaks/effect_amplifier_cap.json");

    private static EffectAmplifierCapData data = new EffectAmplifierCapData();

    public static class EffectAmplifierCapData {
        public List<CapRule> rules = new ArrayList<>();

        public EffectAmplifierCapData() {
            // Default example rules
            // Cap applies to ALL entities EXCEPT those in the blacklist
            CapRule strengthRule = new CapRule();
            strengthRule.effect = "minecraft:strength";
            strengthRule.maxAmplifier = 3;
            strengthRule.blacklistedEntities = Arrays.asList("minecraft:wither", "minecraft:ender_dragon");
            rules.add(strengthRule);

            CapRule poisonRule = new CapRule();
            poisonRule.effect = "minecraft:poison";
            poisonRule.maxAmplifier = 1;
            poisonRule.blacklistedEntities = new ArrayList<>(); // Applies to all entities
            rules.add(poisonRule);
        }
    }

    public static class CapRule {
        public String effect;
        public int maxAmplifier;
        public List<String> blacklistedEntities = new ArrayList<>(); // Entities exempt from the cap
    }

    public static void load() {
        try {
            Path parentDir = CONFIG_PATH.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            if (!Files.exists(CONFIG_PATH)) {
                save();
                return;
            }

            String json = Files.readString(CONFIG_PATH);
            data = GSON.fromJson(json, EffectAmplifierCapData.class);

            if (data == null) {
                data = new EffectAmplifierCapData();
                save();
            }

        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to load effect amplifier cap config", e);
            data = new EffectAmplifierCapData();
        }
    }

    public static void save() {
        try {
            Path parentDir = CONFIG_PATH.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            String json = GSON.toJson(data);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            BoboTweaks.LOGGER.error("Failed to save effect amplifier cap config", e);
        }
    }

    public static Integer getCapForEntityAndEffect(String entityId, String effectId) {
        for (CapRule rule : data.rules) {
            if (rule.effect.equals(effectId) && !rule.blacklistedEntities.contains(entityId)) {
                return rule.maxAmplifier;
            }
        }
        return null;
    }

    public static List<CapRule> getRules() {
        return data.rules;
    }
}
