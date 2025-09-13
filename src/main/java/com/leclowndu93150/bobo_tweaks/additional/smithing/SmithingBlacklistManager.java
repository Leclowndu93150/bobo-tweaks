package com.leclowndu93150.bobo_tweaks.additional.smithing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class SmithingBlacklistManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("bobotweaks/smithing_blacklist.json");
    private static Set<String> blacklistedItemIds = new HashSet<>();
    private static Set<Item> blacklistedItems = new HashSet<>();
    private static boolean loaded = false;
    
    // Configuration options
    private static boolean enableBlacklist = true;
    private static boolean preventBlacklistedTemplates = true;
    private static boolean preventBlacklistedBase = true;
    private static boolean preventBlacklistedAddition = true;
    
    public static void init() {
        loadBlacklist();
    }
    
    public static boolean isItemBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return isItemBlacklisted(stack.getItem());
    }
    
    public static boolean isItemBlacklisted(Item item) {
        if (!loaded) {
            loadBlacklist();
        }
        return enableBlacklist && blacklistedItems.contains(item);
    }
    
    public static boolean isEnabled() {
        if (!loaded) {
            loadBlacklist();
        }
        return enableBlacklist;
    }
    
    public static boolean shouldCheckTemplates() {
        return preventBlacklistedTemplates;
    }
    
    public static boolean shouldCheckBase() {
        return preventBlacklistedBase;
    }
    
    public static boolean shouldCheckAddition() {
        return preventBlacklistedAddition;
    }
    
    public static void addItem(Item item) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        if (itemId != null) {
            String itemIdStr = itemId.toString();
            blacklistedItemIds.add(itemIdStr);
            blacklistedItems.add(item);
            saveBlacklist();
            BoboTweaks.getLogger().info("Added {} to smithing blacklist", itemIdStr);
        }
    }
    
    public static void removeItem(Item item) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        if (itemId != null) {
            String itemIdStr = itemId.toString();
            blacklistedItemIds.remove(itemIdStr);
            blacklistedItems.remove(item);
            saveBlacklist();
            BoboTweaks.getLogger().info("Removed {} from smithing blacklist", itemIdStr);
        }
    }
    
    public static Set<Item> getBlacklistedItems() {
        if (!loaded) {
            loadBlacklist();
        }
        return new HashSet<>(blacklistedItems);
    }
    
    private static void loadBlacklist() {
        blacklistedItemIds.clear();
        blacklistedItems.clear();
        
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                JsonObject root = GSON.fromJson(json, JsonObject.class);
                
                // Load configuration options
                if (root.has("enable_smithing_blacklist")) {
                    enableBlacklist = root.get("enable_smithing_blacklist").getAsBoolean();
                }
                if (root.has("prevent_blacklisted_templates")) {
                    preventBlacklistedTemplates = root.get("prevent_blacklisted_templates").getAsBoolean();
                }
                if (root.has("prevent_blacklisted_base")) {
                    preventBlacklistedBase = root.get("prevent_blacklisted_base").getAsBoolean();
                }
                if (root.has("prevent_blacklisted_addition")) {
                    preventBlacklistedAddition = root.get("prevent_blacklisted_addition").getAsBoolean();
                }
                
                // Load blacklisted items
                if (root.has("blacklisted_items")) {
                    JsonArray items = root.getAsJsonArray("blacklisted_items");
                    for (var element : items) {
                        String itemId = element.getAsString();
                        blacklistedItemIds.add(itemId);
                        
                        try {
                            ResourceLocation resLoc = new ResourceLocation(itemId);
                            Item item = ForgeRegistries.ITEMS.getValue(resLoc);
                            if (item != null) {
                                blacklistedItems.add(item);
                            } else {
                                BoboTweaks.getLogger().warn("Unknown item in smithing blacklist: {}", itemId);
                            }
                        } catch (Exception e) {
                            BoboTweaks.getLogger().warn("Invalid item ID in smithing blacklist: {}", itemId);
                        }
                    }
                }
                
                BoboTweaks.getLogger().info("Loaded smithing blacklist: {} items, enabled={}", 
                    blacklistedItems.size(), enableBlacklist);
            } catch (IOException e) {
                BoboTweaks.getLogger().error("Failed to load smithing blacklist", e);
                createDefaultConfig();
            }
        } else {
            createDefaultConfig();
        }
        
        loaded = true;
    }
    
    private static void createDefaultConfig() {
        // Create default config with some example items
        blacklistedItemIds.add("minecraft:bedrock");
        blacklistedItemIds.add("minecraft:command_block");
        blacklistedItemIds.add("minecraft:structure_block");
        
        // Set default configuration values
        enableBlacklist = true;
        preventBlacklistedTemplates = true;
        preventBlacklistedBase = true;
        preventBlacklistedAddition = true;
        
        saveBlacklist();
    }
    
    private static void saveBlacklist() {
        JsonObject root = new JsonObject();
        
        // Save configuration options
        root.addProperty("enable_smithing_blacklist", enableBlacklist);
        root.addProperty("prevent_blacklisted_templates", preventBlacklistedTemplates);
        root.addProperty("prevent_blacklisted_base", preventBlacklistedBase);
        root.addProperty("prevent_blacklisted_addition", preventBlacklistedAddition);
        
        // Save blacklisted items
        JsonArray items = new JsonArray();
        for (String itemId : blacklistedItemIds) {
            items.add(itemId);
        }
        root.add("blacklisted_items", items);
        
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
            BoboTweaks.getLogger().info("Saved smithing blacklist with {} items", blacklistedItemIds.size());
        } catch (IOException e) {
            BoboTweaks.getLogger().error("Failed to save smithing blacklist", e);
        }
    }
    
    public static void reload() {
        loaded = false;
        loadBlacklist();
    }
}