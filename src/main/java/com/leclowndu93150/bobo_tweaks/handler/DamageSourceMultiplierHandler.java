package com.leclowndu93150.bobo_tweaks.handler;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.DamageSourceConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class DamageSourceMultiplierHandler {
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        
        try {

            var damageTypeHolder = event.getSource().typeHolder();
            var resourceKeyOptional = damageTypeHolder.unwrapKey();
            
            if (resourceKeyOptional.isPresent()) {
                var resourceLocation = resourceKeyOptional.get().location();
                DamageSourceConfig.DamageSourceEntry multiplier = DamageSourceConfig.getMultiplier(resourceLocation);

                if (multiplier.damage_mult != 1.0) {
                    boolean isPlayer = target instanceof Player;

                    if ((isPlayer && multiplier.affects_players) || (!isPlayer && multiplier.affects_mobs)) {
                        float originalDamage = event.getAmount();
                        float modifiedDamage = originalDamage * (float) multiplier.damage_mult;
                        event.setAmount(modifiedDamage);
                    }
                }
            }
        } catch (Exception e) {
            BoboTweaks.getLogger().error("Error processing damage multiplier", e);
        }
    }
}