package com.leclowndu93150.bobo_tweaks.additional.villager.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

public class VillagerModuleConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/bobotweaks/villager.json");
    
    // Config values
    public static boolean enableVillagerModule = true;
    
    // Penalty System
    public static int priceIncreasePerPenalty = 5;
    public static int maxPenaltyStacks = 20;
    
    // Beds Feature
    public static boolean enableBedPenalties = true;
    public static int penaltiesPerMissedSleep = 1;
    public static int penaltiesRemovedOnSleep = 1;
    public static int maxSleepPenaltyStacks = 10;
    public static boolean blockNightTrading = true;
    
    // Social Feature
    public static boolean enableSocialPenalties = true;
    public static int socialPenaltyStacks = 3;
    public static int maxSocialPenaltyStacks = 5;
    public static int socialVillagerRadius = 10;
    public static int minVillagersRequired = 2;
    public static int socialCheckInterval = 200;
    
    // Shelter Feature
    public static boolean enableShelterPenalties = true;
    public static int shelterPenaltyStacks = 2;
    public static int maxShelterPenaltyStacks = 5;
    public static int shelterCheckRadius = 3;
    public static int shelterCheckInterval = 200;
    public static boolean requireBothHeadAndFeetShelter = true;
    
    // Shelter blocks list with wildcard support
    private static List<String> shelterBlockPatterns = new ArrayList<>();
    private static final Set<Block> shelterBlockCache = ConcurrentHashMap.newKeySet();
    private static final Map<String, Pattern> compiledPatterns = new ConcurrentHashMap<>();
    private static volatile boolean cacheInitialized = false;
    
    static {
        // Default shelter blocks
        shelterBlockPatterns.addAll(Arrays.asList(
            "minecraft:*_planks",
            "minecraft:*_log",
            "minecraft:stripped_*_log",
            "minecraft:*_wood",
            "minecraft:stripped_*_wood",
            "minecraft:stone",
            "minecraft:cobblestone",
            "minecraft:stone_bricks",
            "minecraft:*_stone_bricks",
            "minecraft:bricks",
            "minecraft:deepslate",
            "minecraft:cobbled_deepslate",
            "minecraft:deepslate_bricks",
            "minecraft:*_deepslate_bricks",
            "minecraft:terracotta",
            "minecraft:*_terracotta",
            "minecraft:*_concrete",
            "minecraft:*_wool",
            "minecraft:bamboo_block",
            "minecraft:stripped_bamboo_block",
            "minecraft:quartz_block",
            "minecraft:*_quartz_block",
            "minecraft:purpur_block",
            "minecraft:prismarine",
            "minecraft:*_prismarine",
            "minecraft:blackstone",
            "minecraft:polished_blackstone",
            "minecraft:*_blackstone_bricks",
            "minecraft:basalt",
            "minecraft:polished_basalt",
            "minecraft:smooth_basalt",
            "minecraft:end_stone",
            "minecraft:end_stone_bricks",
            "minecraft:obsidian",
            "minecraft:crying_obsidian",
            "minecraft:netherrack",
            "minecraft:nether_bricks",
            "minecraft:*_nether_bricks",
            "minecraft:*_copper",
            "minecraft:copper_block"
        ));
    }
    
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                
                // General settings
                if (json.has("enabled")) {
                    enableVillagerModule = json.get("enabled").getAsBoolean();
                }
                
                // Penalty System
                if (json.has("penalty_system")) {
                    JsonObject penaltySystem = json.getAsJsonObject("penalty_system");
                    if (penaltySystem.has("price_increase_per_penalty")) {
                        priceIncreasePerPenalty = penaltySystem.get("price_increase_per_penalty").getAsInt();
                    }
                    if (penaltySystem.has("max_penalty_stacks")) {
                        maxPenaltyStacks = penaltySystem.get("max_penalty_stacks").getAsInt();
                    }
                }
                
                // Beds Feature
                if (json.has("beds")) {
                    JsonObject beds = json.getAsJsonObject("beds");
                    if (beds.has("enabled")) {
                        enableBedPenalties = beds.get("enabled").getAsBoolean();
                    }
                    if (beds.has("penalties_per_missed_sleep")) {
                        penaltiesPerMissedSleep = beds.get("penalties_per_missed_sleep").getAsInt();
                    }
                    if (beds.has("penalties_removed_on_sleep")) {
                        penaltiesRemovedOnSleep = beds.get("penalties_removed_on_sleep").getAsInt();
                    }
                    if (beds.has("max_sleep_penalty_stacks")) {
                        maxSleepPenaltyStacks = beds.get("max_sleep_penalty_stacks").getAsInt();
                    }
                    if (beds.has("block_night_trading")) {
                        blockNightTrading = beds.get("block_night_trading").getAsBoolean();
                    }
                }
                
                // Social Feature
                if (json.has("social")) {
                    JsonObject social = json.getAsJsonObject("social");
                    if (social.has("enabled")) {
                        enableSocialPenalties = social.get("enabled").getAsBoolean();
                    }
                    if (social.has("penalty_stacks")) {
                        socialPenaltyStacks = social.get("penalty_stacks").getAsInt();
                    }
                    if (social.has("max_penalty_stacks")) {
                        maxSocialPenaltyStacks = social.get("max_penalty_stacks").getAsInt();
                    }
                    if (social.has("check_radius")) {
                        socialVillagerRadius = social.get("check_radius").getAsInt();
                    }
                    if (social.has("min_villagers")) {
                        minVillagersRequired = social.get("min_villagers").getAsInt();
                    }
                    if (social.has("check_interval")) {
                        socialCheckInterval = social.get("check_interval").getAsInt();
                    }
                }
                
                // Shelter Feature
                if (json.has("shelter")) {
                    JsonObject shelter = json.getAsJsonObject("shelter");
                    if (shelter.has("enabled")) {
                        enableShelterPenalties = shelter.get("enabled").getAsBoolean();
                    }
                    if (shelter.has("penalty_stacks")) {
                        shelterPenaltyStacks = shelter.get("penalty_stacks").getAsInt();
                    }
                    if (shelter.has("max_penalty_stacks")) {
                        maxShelterPenaltyStacks = shelter.get("max_penalty_stacks").getAsInt();
                    }
                    if (shelter.has("check_radius")) {
                        shelterCheckRadius = shelter.get("check_radius").getAsInt();
                    }
                    if (shelter.has("check_interval")) {
                        shelterCheckInterval = shelter.get("check_interval").getAsInt();
                    }
                    if (shelter.has("require_both_head_and_feet")) {
                        requireBothHeadAndFeetShelter = shelter.get("require_both_head_and_feet").getAsBoolean();
                    }
                    if (shelter.has("shelter_blocks")) {
                        shelterBlockPatterns.clear();
                        JsonArray blocks = shelter.getAsJsonArray("shelter_blocks");
                        for (JsonElement element : blocks) {
                            shelterBlockPatterns.add(element.getAsString());
                        }
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
            
            // General settings
            json.addProperty("enabled", enableVillagerModule);
            json.addProperty("_comment_enabled", "Enable or disable the entire Villager Module");
            
            // Penalty System
            JsonObject penaltySystem = new JsonObject();
            penaltySystem.addProperty("price_increase_per_penalty", priceIncreasePerPenalty);
            penaltySystem.addProperty("_comment_price_increase", "Percentage price increase per penalty stack");
            penaltySystem.addProperty("max_penalty_stacks", maxPenaltyStacks);
            penaltySystem.addProperty("_comment_max_stacks", "Maximum penalty stacks a villager can have");
            json.add("penalty_system", penaltySystem);
            
            // Beds Feature
            JsonObject beds = new JsonObject();
            beds.addProperty("enabled", enableBedPenalties);
            beds.addProperty("_comment_enabled", "Enable penalties for villagers not sleeping");
            beds.addProperty("penalties_per_missed_sleep", penaltiesPerMissedSleep);
            beds.addProperty("_comment_penalties", "Penalty stacks gained per night without sleep");
            beds.addProperty("penalties_removed_on_sleep", penaltiesRemovedOnSleep);
            beds.addProperty("_comment_removed", "Penalty stacks removed when villager successfully sleeps");
            beds.addProperty("max_sleep_penalty_stacks", maxSleepPenaltyStacks);
            beds.addProperty("_comment_max", "Maximum sleep penalty stacks a villager can have");
            beds.addProperty("block_night_trading", blockNightTrading);
            beds.addProperty("_comment_night_trading", "Block trading with villagers at night");
            json.add("beds", beds);
            
            // Social Feature
            JsonObject social = new JsonObject();
            social.addProperty("enabled", enableSocialPenalties);
            social.addProperty("_comment_enabled", "Enable penalties for isolated villagers");
            social.addProperty("penalty_stacks", socialPenaltyStacks);
            social.addProperty("_comment_penalty", "Penalty stacks for isolated villagers");
            social.addProperty("max_penalty_stacks", maxSocialPenaltyStacks);
            social.addProperty("_comment_max", "Maximum social penalty stacks");
            social.addProperty("check_radius", socialVillagerRadius);
            social.addProperty("_comment_radius", "Radius to check for other villagers");
            social.addProperty("min_villagers", minVillagersRequired);
            social.addProperty("_comment_min", "Minimum villagers required nearby to avoid penalty");
            social.addProperty("check_interval", socialCheckInterval);
            social.addProperty("_comment_interval", "Ticks between social checks");
            json.add("social", social);
            
            // Shelter Feature
            JsonObject shelter = new JsonObject();
            shelter.addProperty("enabled", enableShelterPenalties);
            shelter.addProperty("_comment_enabled", "Enable penalties for villagers without shelter");
            shelter.addProperty("penalty_stacks", shelterPenaltyStacks);
            shelter.addProperty("_comment_penalty", "Penalty stacks for villagers without shelter");
            shelter.addProperty("max_penalty_stacks", maxShelterPenaltyStacks);
            shelter.addProperty("_comment_max", "Maximum shelter penalty stacks");
            shelter.addProperty("check_radius", shelterCheckRadius);
            shelter.addProperty("_comment_radius", "Radius above villager to check for shelter blocks");
            shelter.addProperty("check_interval", shelterCheckInterval);
            shelter.addProperty("_comment_interval", "Ticks between shelter checks");
            shelter.addProperty("require_both_head_and_feet", requireBothHeadAndFeetShelter);
            shelter.addProperty("_comment_both", "Require shelter above both head and feet positions");
            
            JsonArray shelterBlocks = new JsonArray();
            shelter.addProperty("_comment_blocks", "Blocks that count as shelter. Supports wildcards: * matches any characters, ? matches single character");
            for (String pattern : shelterBlockPatterns) {
                shelterBlocks.add(pattern);
            }
            shelter.add("shelter_blocks", shelterBlocks);
            json.add("shelter", shelter);
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(json, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void addShelterBlock(String pattern) {
        if (!shelterBlockPatterns.contains(pattern)) {
            shelterBlockPatterns.add(pattern);
            save();
        }
    }
    
    public static void removeShelterBlock(String pattern) {
        if (shelterBlockPatterns.remove(pattern)) {
            save();
        }
    }
    
    public static void clearShelterBlocks() {
        shelterBlockPatterns.clear();
        save();
    }
    
    public static List<String> getShelterBlockPatterns() {
        return new ArrayList<>(shelterBlockPatterns);
    }
    
    public static Set<Block> getShelterBlocks() {
        if (!cacheInitialized) {
            initializeShelterCache();
        }
        return shelterBlockCache;
    }
    
    private static void initializeShelterCache() {
        if (cacheInitialized) return;
        
        synchronized (VillagerModuleConfig.class) {
            if (cacheInitialized) return;
            
            shelterBlockCache.clear();
            
            compiledPatterns.clear();
            for (String pattern : shelterBlockPatterns) {
                String regex = pattern
                    .replace(".", "\\.")
                    .replace("*", ".*")
                    .replace("?", ".");
                compiledPatterns.put(pattern, Pattern.compile(regex));
            }

            for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
                String blockId = entry.getKey().location().toString();
                for (Pattern compiledPattern : compiledPatterns.values()) {
                    if (compiledPattern.matcher(blockId).matches()) {
                        shelterBlockCache.add(entry.getValue());
                        break;
                    }
                }
            }
            
            cacheInitialized = true;
        }
    }
    
}