package com.leclowndu93150.bobo_tweaks.handler;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class LifeLeechHandler {
    private static boolean isSpellHealing = false;
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            AttributeInstance lifestealInstance = attacker.getAttribute(ModAttributes.LIFESTEAL.get());
            AttributeInstance lifestealCapInstance = attacker.getAttribute(ModAttributes.LIFESTEAL_CAP.get());
            
            if (lifestealInstance != null && lifestealInstance.getValue() > 0) {
                double lifestealPercent = lifestealInstance.getValue();
                double lifestealCap = lifestealCapInstance != null ? lifestealCapInstance.getValue() : 0.0D;
                
                float damageDealt = event.getAmount();
                float healAmount = (float) (damageDealt * lifestealPercent);
                
                if (lifestealCap > 0) {
                    healAmount = Math.min(healAmount, (float) lifestealCap);
                }
                
                if (healAmount > 0) {
                    attacker.heal(healAmount);
                }
            }
        }
        
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow) {
            if (arrow.getOwner() instanceof LivingEntity attacker) {
                AttributeInstance lifestealInstance = attacker.getAttribute(ModAttributes.LIFESTEAL.get());
                AttributeInstance lifestealCapInstance = attacker.getAttribute(ModAttributes.LIFESTEAL_CAP.get());
                
                if (lifestealInstance != null && lifestealInstance.getValue() > 0) {
                    double lifestealPercent = lifestealInstance.getValue();
                    double lifestealCap = lifestealCapInstance != null ? lifestealCapInstance.getValue() : 0.0D;
                    
                    float damageDealt = event.getAmount();
                    float healAmount = (float) (damageDealt * lifestealPercent);
                    
                    if (lifestealCap > 0) {
                        healAmount = Math.min(healAmount, (float) lifestealCap);
                    }
                    
                    if (healAmount > 0) {
                        attacker.heal(healAmount);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSpellHeal(SpellHealEvent event) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return;
        }
        isSpellHealing = true;
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHeal(LivingHealEvent event) {
        if (isSpellHealing) {
            isSpellHealing = false;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onSpellDamage(SpellDamageEvent event) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return;
        }
        
        if (event.getSpellDamageSource() != null && event.getSpellDamageSource().getEntity() instanceof LivingEntity attacker) {
            AttributeInstance spellLeechInstance = attacker.getAttribute(ModAttributes.SPELL_LEECH.get());
            AttributeInstance spellLeechCapInstance = attacker.getAttribute(ModAttributes.SPELL_LEECH_CAP.get());
            
            if (spellLeechInstance != null && spellLeechInstance.getValue() > 0) {
                double spellLeechPercent = spellLeechInstance.getValue();
                double spellLeechCap = spellLeechCapInstance != null ? spellLeechCapInstance.getValue() : 0.0D;
                
                float damageDealt = event.getAmount();
                float healAmount = (float) (damageDealt * spellLeechPercent);
                
                if (spellLeechCap > 0) {
                    healAmount = Math.min(healAmount, (float) spellLeechCap);
                }
                
                if (healAmount > 0) {
                    attacker.heal(healAmount);
                }
            }
        }
    }

}