package com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentTracker {
    private static final Map<UUID, CompoundTag> playerEnchantmentData = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, Long>> activeModifiers = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, UUID>> namedUUIDs = new ConcurrentHashMap<>();

    public static CompoundTag getOrCreateEnchantmentData(UUID playerId) {
        return playerEnchantmentData.computeIfAbsent(playerId, k -> new CompoundTag());
    }

    public static void cleanupPlayerData(UUID playerId) {
        playerEnchantmentData.remove(playerId);
        activeModifiers.remove(playerId);
        namedUUIDs.remove(playerId);
    }

    public static UUID getOrCreateNamedUUID(UUID playerId, String name) {
        Map<String, UUID> playerUUIDs = namedUUIDs.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        return playerUUIDs.computeIfAbsent(name, k -> UUID.randomUUID());
    }

    public static void applyTimedModifier(LivingEntity entity, Attribute attribute, String modifierName,
                                          String displayName, double amount, AttributeModifier.Operation operation,
                                          int durationTicks) {
        if (attribute == null) return;

        UUID entityId = entity.getUUID();
        UUID modifierUUID = getOrCreateNamedUUID(entityId, modifierName);

        AttributeInstance attrInstance = entity.getAttribute(attribute);
        if (attrInstance != null) {
            attrInstance.removeModifier(modifierUUID);

            AttributeModifier modifier = new AttributeModifier(modifierUUID, displayName, amount, operation);
            attrInstance.addTransientModifier(modifier);

            trackModifier(entityId, modifierName, System.currentTimeMillis() + (durationTicks * 50L));
        }
    }

    public static void removeModifier(LivingEntity entity, Attribute attribute, String modifierName) {
        UUID entityId = entity.getUUID();
        Map<String, UUID> playerUUIDs = namedUUIDs.get(entityId);
        if (playerUUIDs == null) return;

        UUID modifierUUID = playerUUIDs.get(modifierName);
        if (modifierUUID == null) return;

        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierUUID);
        }

        Map<String, Long> modifiers = activeModifiers.get(entityId);
        if (modifiers != null) {
            modifiers.remove(modifierName);
        }
    }

    private static void trackModifier(UUID playerId, String modifierName, long expireTime) {
        activeModifiers.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(modifierName, expireTime);
    }

    public static void cleanupExpiredModifiers(LivingEntity entity) {
        UUID entityId = entity.getUUID();
        Map<String, Long> modifiers = activeModifiers.get(entityId);
        if (modifiers == null) return;

        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = modifiers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime > entry.getValue()) {
                String modifierName = entry.getKey();
                
                for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
                    removeModifier(entity, attr, modifierName);
                }

                iterator.remove();
            }
        }
    }

    public static long getModifierTimeRemaining(UUID playerId, String modifierName) {
        Map<String, Long> modifiers = activeModifiers.get(playerId);
        if (modifiers == null) return 0;
        
        Long expireTime = modifiers.get(modifierName);
        if (expireTime == null) return 0;
        
        long remaining = expireTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public static int getModifierTicksRemaining(UUID playerId, String modifierName) {
        return (int)(getModifierTimeRemaining(playerId, modifierName) / 50L);
    }

    public static double getModifierSecondsRemaining(UUID playerId, String modifierName) {
        return getModifierTimeRemaining(playerId, modifierName) / 1000.0;
    }

    public static boolean hasActiveModifier(UUID playerId, String modifierName) {
        Map<String, Long> modifiers = activeModifiers.get(playerId);
        if (modifiers == null) return false;
        
        Long expireTime = modifiers.get(modifierName);
        if (expireTime == null) return false;
        
        return System.currentTimeMillis() < expireTime;
    }

    public static Map<String, Long> getAllActiveModifiers(UUID playerId) {
        Map<String, Long> modifiers = activeModifiers.get(playerId);
        if (modifiers == null) return Collections.emptyMap();
        
        Map<String, Long> result = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        
        for (Map.Entry<String, Long> entry : modifiers.entrySet()) {
            long remaining = entry.getValue() - currentTime;
            if (remaining > 0) {
                result.put(entry.getKey(), remaining);
            }
        }
        
        return result;
    }

    public static Map<String, Double> getAllActiveModifiersInSeconds(UUID playerId) {
        Map<String, Long> modifiers = activeModifiers.get(playerId);
        if (modifiers == null) return Collections.emptyMap();
        
        Map<String, Double> result = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        
        for (Map.Entry<String, Long> entry : modifiers.entrySet()) {
            long remaining = entry.getValue() - currentTime;
            if (remaining > 0) {
                result.put(entry.getKey(), remaining / 1000.0);
            }
        }
        
        return result;
    }

    public static void cleanupAllModifiers(LivingEntity entity) {
        UUID entityId = entity.getUUID();
        Map<String, UUID> entityUUIDs = namedUUIDs.get(entityId);
        if (entityUUIDs == null) return;

        for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
            AttributeInstance instance = entity.getAttribute(attr);
            if (instance != null) {
                for (UUID uuid : entityUUIDs.values()) {
                    instance.removeModifier(uuid);
                }
            }
        }
        
        activeModifiers.remove(entityId);
        namedUUIDs.remove(entityId);
    }

    public static boolean isOnCooldown(UUID playerId, String cooldownKey, long currentTime) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long cooldownEnd = data.getLong(cooldownKey);
        return currentTime < cooldownEnd;
    }

    public static void setCooldown(UUID playerId, String cooldownKey, long endTime) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putLong(cooldownKey, endTime);
    }

    public static long getCooldownTimeRemaining(UUID playerId, String cooldownKey) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long cooldownEnd = data.getLong(cooldownKey);
        long remaining = cooldownEnd - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public static int getCooldownTicksRemaining(UUID playerId, String cooldownKey) {
        return (int)(getCooldownTimeRemaining(playerId, cooldownKey) / 50L);
    }

    public static double getCooldownSecondsRemaining(UUID playerId, String cooldownKey) {
        return getCooldownTimeRemaining(playerId, cooldownKey) / 1000.0;
    }

    public static void clearCooldown(UUID playerId, String cooldownKey) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.remove(cooldownKey);
    }

    public static void setEnchantmentFlag(UUID playerId, String key, boolean value, int duration) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putBoolean(key, value);
        data.putLong(key + "_expire", System.currentTimeMillis() + (duration * 50L));
    }

    public static void setEnchantmentFlagIndefinite(UUID playerId, String key, boolean value) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putBoolean(key, value);
        data.putLong(key + "_expire", -1L);
    }

    public static boolean hasEnchantmentFlag(UUID playerId, String key) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long expireTime = data.getLong(key + "_expire");
        if (expireTime != -1L && System.currentTimeMillis() > expireTime) {
            data.remove(key);
            data.remove(key + "_expire");
            return false;
        }
        return data.getBoolean(key);
    }

    public static long getEnchantmentFlagTimeRemaining(UUID playerId, String key) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long expireTime = data.getLong(key + "_expire");
        if (expireTime == -1L) return -1L;
        long remaining = expireTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public static int getEnchantmentFlagTicksRemaining(UUID playerId, String key) {
        long remaining = getEnchantmentFlagTimeRemaining(playerId, key);
        if (remaining == -1L) return -1;
        return (int)(remaining / 50L);
    }

    public static double getEnchantmentFlagSecondsRemaining(UUID playerId, String key) {
        long remaining = getEnchantmentFlagTimeRemaining(playerId, key);
        if (remaining == -1L) return -1.0;
        return remaining / 1000.0;
    }

    public static void clearEnchantmentFlag(UUID playerId, String key) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.remove(key);
        data.remove(key + "_expire");
    }

    public static Attribute getAttributeByName(String modId, String attributeName) {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(modId, attributeName));
    }

    public static Attribute getSpellPowerAttribute() {
        return getAttributeByName("irons_spellbooks", "spell_power");
    }

    public static Attribute getMaxManaAttribute() {
        return getAttributeByName("irons_spellbooks", "max_mana");
    }

    public static Attribute getCastTimeReductionAttribute() {
        return getAttributeByName("irons_spellbooks", "cast_time_reduction");
    }

    public static double getPlayerMaxMana(Player player) {
        Attribute manaAttr = getMaxManaAttribute();
        if (manaAttr != null) {
            AttributeInstance manaInstance = player.getAttribute(manaAttr);
            return manaInstance != null ? manaInstance.getValue() : 100.0;
        }
        return 100.0;
    }
}