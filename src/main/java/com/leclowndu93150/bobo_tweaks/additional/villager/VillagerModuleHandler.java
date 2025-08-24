package com.leclowndu93150.bobo_tweaks.additional.villager;

import com.leclowndu93150.bobo_tweaks.additional.villager.config.VillagerModuleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VillagerModuleHandler {
    
    public static void handleVillagerTick(Villager villager) {
        if (!VillagerModuleConfig.enableVillagerModule) {
            return;
        }
        
        if (!(villager.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        VillagerPenaltyManager.PenaltyData data = VillagerPenaltyManager.getPenaltyData(villager);
        long gameTime = serverLevel.getGameTime();
        
        // Check sleep penalties (once per day)
        if (VillagerModuleConfig.enableBedPenalties) {
            handleSleepPenalties(villager, data, gameTime);
        }
        
        // Check social penalties
        if (VillagerModuleConfig.enableSocialPenalties && gameTime % VillagerModuleConfig.socialCheckInterval == 0) {
            if (gameTime - data.lastSocialCheck >= VillagerModuleConfig.socialCheckInterval) {
                handleSocialPenalties(villager, data);
                data.lastSocialCheck = gameTime;
            }
        }
        
        // Check shelter penalties
        if (VillagerModuleConfig.enableShelterPenalties && gameTime % VillagerModuleConfig.shelterCheckInterval == 0) {
            if (gameTime - data.lastShelterCheck >= VillagerModuleConfig.shelterCheckInterval) {
                handleShelterPenalties(villager, data);
                data.lastShelterCheck = gameTime;
            }
        }
    }
    
    private static void handleSleepPenalties(Villager villager, VillagerPenaltyManager.PenaltyData data, long gameTime) {
        long dayTime = villager.level().getDayTime() % 24000;
        
        // Check if it's morning (time 0-1000)
        if (dayTime >= 0 && dayTime < 1000) {
            // Check if we haven't already processed this morning
            if (gameTime - data.lastSleepCheck > 24000) {
                data.lastSleepCheck = gameTime;
                
                // Check if villager slept last night
                Optional<Long> lastSlept = villager.getBrain().getMemory(MemoryModuleType.LAST_SLEPT);
                
                if (lastSlept.isPresent()) {
                    long timeSinceSlept = gameTime - lastSlept.get();
                    
                    // If villager slept recently (within last day)
                    if (timeSinceSlept < 24000) {
                        // Remove penalties for successful sleep
                        data.removeSleepPenalty(VillagerModuleConfig.penaltiesRemovedOnSleep);
                        // Force price update
                        VillagerPenaltyManager.forceUpdatePrices(villager);
                    } else {
                        // Add penalty for not sleeping
                        data.addSleepPenalty(VillagerModuleConfig.penaltiesPerMissedSleep);
                        // Force price update
                        VillagerPenaltyManager.forceUpdatePrices(villager);
                    }
                } else {
                    // No sleep record, add penalty
                    data.addSleepPenalty(VillagerModuleConfig.penaltiesPerMissedSleep);
                    // Force price update
                    VillagerPenaltyManager.forceUpdatePrices(villager);
                }
            }
        }
    }
    
    private static void handleSocialPenalties(Villager villager, VillagerPenaltyManager.PenaltyData data) {
        int radius = VillagerModuleConfig.socialVillagerRadius;
        BlockPos pos = villager.blockPosition();
        
        List<Villager> nearbyVillagers = villager.level().getEntitiesOfClass(
            Villager.class,
            villager.getBoundingBox().inflate(radius),
            v -> v != villager && v.isAlive()
        );
        
        if (nearbyVillagers.size() < VillagerModuleConfig.minVillagersRequired) {
            data.setSocialPenalty(VillagerModuleConfig.socialPenaltyStacks);
        } else {
            data.setSocialPenalty(0);
        }
        
        // Force price update
        VillagerPenaltyManager.forceUpdatePrices(villager);
    }
    
    private static void handleShelterPenalties(Villager villager, VillagerPenaltyManager.PenaltyData data) {
        Set<Block> shelterBlocks = VillagerModuleConfig.getShelterBlocks();
        
        // Ensure shelter blocks are initialized
        if (shelterBlocks.isEmpty()) {
            VillagerModuleConfig.resetCache();
            shelterBlocks = VillagerModuleConfig.getShelterBlocks();
        }
        
        BlockPos villagerPos = villager.blockPosition();
        Level level = villager.level();
        int checkRadius = VillagerModuleConfig.shelterCheckRadius;
        
        boolean hasHeadShelter = false;
        boolean hasFeetShelter = false;
        
        // Check for shelter above head (villager height is about 2 blocks)
        BlockPos headPos = villagerPos.above(2);
        for (int y = 1; y <= checkRadius; y++) {
            BlockPos checkPos = headPos.above(y);
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && (shelterBlocks.contains(state.getBlock()) || state.isSolidRender(level, checkPos))) {
                hasHeadShelter = true;
                break;
            }
        }
        
        // Check for shelter above feet
        for (int y = 1; y <= checkRadius; y++) {
            BlockPos checkPos = villagerPos.above(y);
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && (shelterBlocks.contains(state.getBlock()) || state.isSolidRender(level, checkPos))) {
                hasFeetShelter = true;
                break;
            }
        }
        
        // Apply penalty based on configuration
        boolean needsPenalty = VillagerModuleConfig.requireBothHeadAndFeetShelter 
            ? (!hasHeadShelter || !hasFeetShelter)
            : (!hasHeadShelter && !hasFeetShelter);
        
        if (needsPenalty) {
            data.setShelterPenalty(VillagerModuleConfig.shelterPenaltyStacks);
        } else {
            data.setShelterPenalty(0);
        }
        
        // Force price update
        VillagerPenaltyManager.forceUpdatePrices(villager);
    }
    
    public static boolean canTradeWithVillager(Villager villager) {
        if (!VillagerModuleConfig.enableVillagerModule) {
            return true;
        }
        
        if (!VillagerModuleConfig.blockNightTrading) {
            return true;
        }
        
        // Check if it's night time (12542-23459)
        long dayTime = villager.level().getDayTime() % 24000;
        if (dayTime >= 12542 && dayTime <= 23459) {
            return false;
        }
        
        // Also check if villager is in REST activity
        Activity currentActivity = villager.getBrain().getSchedule()
            .getActivityAt((int)(dayTime));
        return currentActivity != Activity.REST;
    }
}