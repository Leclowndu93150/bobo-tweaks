package com.leclowndu93150.bobo_tweaks.additional.effectimmunity;

import com.leclowndu93150.bobo_tweaks.additional.effectimmunity.config.EffectImmunityConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EffectImmunityHandler {
    
    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        
        String effectId = BuiltInRegistries.MOB_EFFECT.getKey(event.getEffectInstance().getEffect()).toString();
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).toString();

        System.out.println("Checking effect immunity for entity: " + entityId + ", effect: " + effectId);

        // Check mob-specific immunities first (highest priority)
        List<String> mobImmunities = EffectImmunityConfig.getImmunitiesForMob(entityId);
        if (mobImmunities.contains(effectId)) {
            event.setResult(Event.Result.DENY);
            return;
        }
        
        // Check effect-specific immunities (medium priority)
        List<String> immuneMobs = EffectImmunityConfig.getImmuneMobsForEffect(effectId);
        if (immuneMobs.contains(entityId)) {
            event.setResult(Event.Result.DENY);
            return;
        }

        // Check global immunity (lowest priority)
        if (EffectImmunityConfig.isGlobalImmunityEnabled() && 
            EffectImmunityConfig.getGlobalImmunityEffects().contains(effectId)) {
            event.setResult(Event.Result.DENY);
        }
    }
}