package com.leclowndu93150.bobo_tweaks.handler;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class LifeLeechHandler {
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            AttributeInstance lifeLeechInstance = attacker.getAttribute(ModAttributes.LIFE_LEECH.get());
            AttributeInstance leechCapInstance = attacker.getAttribute(ModAttributes.LEECH_CAP.get());
            
            if (lifeLeechInstance != null && lifeLeechInstance.getValue() > 0) {
                double lifeLeechPercent = lifeLeechInstance.getValue();
                double leechCap = leechCapInstance != null ? leechCapInstance.getValue() : 0.0D;
                
                float damageDealt = event.getAmount();
                float healAmount = (float) (damageDealt * lifeLeechPercent);
                
                if (leechCap > 0) {
                    healAmount = Math.min(healAmount, (float) leechCap);
                }
                
                if (healAmount > 0) {
                    attacker.heal(healAmount);
                }
            }
        }
        
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow) {
            if (arrow.getOwner() instanceof LivingEntity attacker) {
                AttributeInstance lifeLeechInstance = attacker.getAttribute(ModAttributes.LIFE_LEECH.get());
                AttributeInstance leechCapInstance = attacker.getAttribute(ModAttributes.LEECH_CAP.get());
                
                if (lifeLeechInstance != null && lifeLeechInstance.getValue() > 0) {
                    double lifeLeechPercent = lifeLeechInstance.getValue();
                    double leechCap = leechCapInstance != null ? leechCapInstance.getValue() : 0.0D;
                    
                    float damageDealt = event.getAmount();
                    float healAmount = (float) (damageDealt * lifeLeechPercent);
                    
                    if (leechCap > 0) {
                        healAmount = Math.min(healAmount, (float) leechCap);
                    }
                    
                    if (healAmount > 0) {
                        attacker.heal(healAmount);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onSpellDamage(SpellDamageEvent event) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return;
        }
        
        if (event.getSpellDamageSource() != null && event.getSpellDamageSource().getEntity() instanceof LivingEntity attacker) {
            AttributeInstance lifeLeechInstance = attacker.getAttribute(ModAttributes.LIFE_LEECH.get());
            AttributeInstance leechCapInstance = attacker.getAttribute(ModAttributes.LEECH_CAP.get());
            
            if (lifeLeechInstance != null && lifeLeechInstance.getValue() > 0) {
                double lifeLeechPercent = lifeLeechInstance.getValue();
                double leechCap = leechCapInstance != null ? leechCapInstance.getValue() : 0.0D;
                
                float damageDealt = event.getAmount();
                double spellMultiplier = ModConfig.COMMON.lifeLeechSpellMultiplier.get();
                float healAmount = (float) (damageDealt * lifeLeechPercent * spellMultiplier);
                
                if (leechCap > 0) {
                    healAmount = Math.min(healAmount, (float) leechCap);
                }
                
                if (healAmount > 0) {
                    attacker.heal(healAmount);
                }
            }
        }
    }
}