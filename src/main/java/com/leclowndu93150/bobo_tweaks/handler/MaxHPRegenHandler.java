package com.leclowndu93150.bobo_tweaks.handler;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class MaxHPRegenHandler {
    private static final Map<UUID, Integer> regenTimers = new HashMap<>();
    private static final int REGEN_INTERVAL_TICKS = 100; // 5 seconds
    
    @SubscribeEvent
    public static void onLivingTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            handleRegen(event.player);
        }
    }
    
    @SubscribeEvent
    public static void onEntityTick(net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide() && !(event.getEntity() instanceof net.minecraft.world.entity.player.Player)) {
            handleRegen(event.getEntity());
        }
    }
    
    public static void handleRegen(LivingEntity entity) {
        double regenRate = entity.getAttributeValue(ModAttributes.MAX_HP_REGEN.get());
        
        if (regenRate > 0) {
            UUID entityId = entity.getUUID();
            int currentTimer = regenTimers.getOrDefault(entityId, 0);
            
            currentTimer++;
            
            int adjustedInterval = Math.max(1, (int)(REGEN_INTERVAL_TICKS * (1.0 - regenRate)));
            
            if (currentTimer >= adjustedInterval) {
                float maxHealth = (float) entity.getAttributeValue(Attributes.MAX_HEALTH);
                float currentHealth = entity.getHealth();
                
                if (currentHealth < maxHealth) {
                    float healAmount = maxHealth * (float)regenRate;
                    entity.heal(healAmount);
                }
                
                regenTimers.put(entityId, 0);
            } else {
                regenTimers.put(entityId, currentTimer);
            }
        }
    }


}