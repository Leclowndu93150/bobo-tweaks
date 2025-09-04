package com.leclowndu93150.bobo_tweaks.handler;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class WoodwardSentinelHandler {
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (entity.hasEffect(ModPotions.WOODWARD_SENTINEL.get())) {
            int amplifier = entity.getEffect(ModPotions.WOODWARD_SENTINEL.get()).getAmplifier();
            
            float originalHealAmount = event.getAmount();
            float healingReduction = getHealingReduction(amplifier);
            float reducedAmount = originalHealAmount * healingReduction;
            float remainingHeal = originalHealAmount - reducedAmount;
            
            event.setAmount(remainingHeal);
            
            if (reducedAmount > 0) {
                distributeHealingToTeam(entity, reducedAmount, amplifier);
            }
        }
    }
    
    private static void distributeHealingToTeam(LivingEntity entity, float healAmount, int amplifier) {
        if (entity instanceof Player player && !entity.level().isClientSide()) {
            List<Player> teamMembers = getTeamMembers(player);
            
            if (!teamMembers.isEmpty()) {
                float multiplier = getDistributedHealingMultiplier(amplifier);
                float totalDistributedHealing = healAmount * multiplier;
                float healPerPlayer = totalDistributedHealing / teamMembers.size();
                
                float healCap = getHealCap(amplifier);
                if (healCap > 0) {
                    healPerPlayer = Math.min(healPerPlayer, healCap);
                }
                
                for (Player teamMember : teamMembers) {
                    if (teamMember != player) {
                        teamMember.heal(healPerPlayer);
                    }
                }
            }
        }
    }
    
    private static List<Player> getTeamMembers(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return List.of();
        }
        
        Team team = serverPlayer.getTeam();
        if (team == null) {
            return List.of();
        }
        
        return serverPlayer.getServer().getPlayerList().getPlayers().stream()
            .filter(p -> p.getTeam() == team && p != player)
            .map(p -> (Player) p)
            .toList();
    }
    
    private static float getHealingReduction(int amplifier) {
        return ModConfig.COMMON.woodwardSentinelHealingReduction.get().floatValue();
    }
    
    private static float getDistributedHealingMultiplier(int amplifier) {
        return ModConfig.COMMON.woodwardSentinelDistributedHealingMultiplier.get().floatValue() * (amplifier + 1);
    }
    
    private static float getHealCap(int amplifier) {
        return ModConfig.COMMON.woodwardSentinelHealCap.get().floatValue();
    }
}