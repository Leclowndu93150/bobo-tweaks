package com.leclowndu93150.bobo_tweaks.additional.smithing;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.additional.smithing.config.SmithingBlacklistConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.stream.Collectors;

public class SmithingBlacklistManager {
    private static Set<Item> blacklistedItems = null;
    
    public static boolean isItemBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        updateBlacklistCache();
        return blacklistedItems.contains(stack.getItem());
    }
    
    public static boolean isItemBlacklisted(Item item) {
        updateBlacklistCache();
        return blacklistedItems.contains(item);
    }
    
    private static void updateBlacklistCache() {
        if (blacklistedItems == null) {
            blacklistedItems = SmithingBlacklistConfig.COMMON.smithingBlacklistedItems.get()
                .stream()
                .map(itemName -> {
                    try {
                        ResourceLocation itemId = new ResourceLocation(itemName);
                        return ForgeRegistries.ITEMS.getValue(itemId);
                    } catch (Exception e) {
                        BoboTweaks.getLogger().warn("Invalid item name in smithing blacklist: " + itemName);
                        return null;
                    }
                })
                .filter(item -> item != null)
                .collect(Collectors.toSet());
        }
    }
    
    public static void clearCache() {
        blacklistedItems = null;
    }
    
    public static Set<Item> getBlacklistedItems() {
        updateBlacklistCache();
        return blacklistedItems;
    }
}