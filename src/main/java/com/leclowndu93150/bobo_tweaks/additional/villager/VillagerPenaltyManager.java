package com.leclowndu93150.bobo_tweaks.additional.villager;

import com.leclowndu93150.bobo_tweaks.additional.villager.config.VillagerModuleConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerPenaltyManager {
    private static final Map<UUID, PenaltyData> penaltyMap = new HashMap<>();
    private static final ThreadLocal<Villager> currentTradingVillager = new ThreadLocal<>();
    
    public static class PenaltyData {
        private int sleepPenalties = 0;
        private int socialPenalties = 0;
        private int shelterPenalties = 0;
        long lastSleepCheck = 0;
        long lastSocialCheck = 0;
        long lastShelterCheck = 0;
        
        public int getTotalPenalties() {
            return sleepPenalties + socialPenalties + shelterPenalties;
        }
        
        public void addSleepPenalty(int amount) {
            sleepPenalties = Math.min(sleepPenalties + amount, VillagerModuleConfig.maxSleepPenaltyStacks);
        }
        
        public void removeSleepPenalty(int amount) {
            sleepPenalties = Math.max(0, sleepPenalties - amount);
        }
        
        public void addSocialPenalty(int amount) {
            socialPenalties = Math.min(socialPenalties + amount, VillagerModuleConfig.maxSocialPenaltyStacks);
        }
        
        public void setSocialPenalty(int amount) {
            socialPenalties = Math.min(amount, VillagerModuleConfig.maxSocialPenaltyStacks);
        }
        
        public void removeSocialPenalty(int amount) {
            socialPenalties = Math.max(0, socialPenalties - amount);
        }
        
        public void addShelterPenalty(int amount) {
            shelterPenalties = Math.min(shelterPenalties + amount, VillagerModuleConfig.maxShelterPenaltyStacks);
        }
        
        public void setShelterPenalty(int amount) {
            shelterPenalties = Math.min(amount, VillagerModuleConfig.maxShelterPenaltyStacks);
        }
        
        public void removeShelterPenalty(int amount) {
            shelterPenalties = Math.max(0, shelterPenalties - amount);
        }
        
        public int getSleepPenalties() {
            return sleepPenalties;
        }
        
        public int getSocialPenalties() {
            return socialPenalties;
        }
        
        public int getShelterPenalties() {
            return shelterPenalties;
        }
        
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("sleepPenalties", sleepPenalties);
            tag.putInt("socialPenalties", socialPenalties);
            tag.putInt("shelterPenalties", shelterPenalties);
            tag.putLong("lastSleepCheck", lastSleepCheck);
            tag.putLong("lastSocialCheck", lastSocialCheck);
            tag.putLong("lastShelterCheck", lastShelterCheck);
            return tag;
        }
        
        public void load(CompoundTag tag) {
            sleepPenalties = tag.getInt("sleepPenalties");
            socialPenalties = tag.getInt("socialPenalties");
            shelterPenalties = tag.getInt("shelterPenalties");
            lastSleepCheck = tag.getLong("lastSleepCheck");
            lastSocialCheck = tag.getLong("lastSocialCheck");
            lastShelterCheck = tag.getLong("lastShelterCheck");
        }
    }
    
    public static PenaltyData getPenaltyData(Villager villager) {
        return penaltyMap.computeIfAbsent(villager.getUUID(), k -> new PenaltyData());
    }
    
    public static void savePenaltyData(Villager villager, CompoundTag tag) {
        PenaltyData data = getPenaltyData(villager);
        tag.put("VillagerPenalties", data.save());
    }
    
    public static void loadPenaltyData(Villager villager, CompoundTag tag) {
        if (tag.contains("VillagerPenalties")) {
            PenaltyData data = getPenaltyData(villager);
            data.load(tag.getCompound("VillagerPenalties"));
        }
    }
    
    public static void clearData(UUID uuid) {
        penaltyMap.remove(uuid);
    }
    
    public static float getPriceModifier(Villager villager) {
        PenaltyData data = getPenaltyData(villager);
        int totalPenalties = data.getTotalPenalties();
        return 1.0f + (totalPenalties * VillagerModuleConfig.priceIncreasePerPenalty / 100.0f);
    }
    
    public static void forceUpdatePrices(Villager villager) {
        villager.getOffers().forEach(MerchantOffer::resetSpecialPriceDiff);

        if (villager.getTradingPlayer() != null) {
            villager.updateSpecialPrices(villager.getTradingPlayer());
        }
    }
    
    public static void setCurrentTradingVillager(Villager villager) {
        currentTradingVillager.set(villager);
    }
    
    public static Villager getCurrentTradingVillager() {
        return currentTradingVillager.get();
    }
    
    public static void clearCurrentTradingVillager() {
        currentTradingVillager.remove();
    }
}