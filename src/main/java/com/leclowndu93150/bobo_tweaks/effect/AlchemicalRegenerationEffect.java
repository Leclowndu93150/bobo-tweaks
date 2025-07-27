package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.ModList;

import java.util.Objects;
import java.util.UUID;

public class AlchemicalRegenerationEffect extends MobEffect {
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("8d7e9f2a-1b4c-3e5f-6789-abcdef123456");
    private int tickCounter = 0;
    
    public AlchemicalRegenerationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF7F);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
        
        tickCounter++;
        
        // Heal every 0.4 seconds (8 ticks)
        if (tickCounter >= 8) {
            AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
            double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
            
            // Calculate heal amount: same formula as restore effect
            float flatHeal = getFlatHeal(amplifier);
            float scalingBonus = getScalingFactor(amplifier) * (float) alchemicalBoost;
            float totalHeal = flatHeal + scalingBonus;
            
            entity.heal(totalHeal);
            tickCounter = 0;
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        // Add crit damage boost if Apothic Attributes is loaded
        if (ModList.get().isLoaded("attributeslib")) {
            try {
                var critDamageAttr = entity.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("attributeslib", "crit_damage")))
                );
                
                if (critDamageAttr != null) {
                    double critDamageBoost = getCritDamageBoost();
                    AttributeModifier modifier = new AttributeModifier(CRIT_DAMAGE_UUID,
                        "Alchemical regeneration crit boost", critDamageBoost, AttributeModifier.Operation.ADDITION);
                    critDamageAttr.removeModifier(modifier);
                    critDamageAttr.addTransientModifier(modifier);
                }
            } catch (Exception e) {
                // Silent catch in case attribute doesn't exist
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        // Remove crit damage boost
        if (ModList.get().isLoaded("attributeslib")) {
            try {
                var critDamageAttr = entity.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("attributeslib", "crit_damage")))
                );
                
                if (critDamageAttr != null) {
                    critDamageAttr.removeModifier(CRIT_DAMAGE_UUID);
                }
            } catch (Exception e) {
                // Silent catch in case attribute doesn't exist
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    private float getFlatHeal(int amplifier) {
        return (float) (ModConfig.COMMON.regenerationFlatHeal.get() * (amplifier + 1));
    }
    
    private float getScalingFactor(int amplifier) {
        return (float) (ModConfig.COMMON.regenerationScalingFactor.get() * (amplifier + 1));
    }
    
    private double getCritDamageBoost() {
        return ModConfig.COMMON.regenerationCritDamageBoost.get();
    }
}