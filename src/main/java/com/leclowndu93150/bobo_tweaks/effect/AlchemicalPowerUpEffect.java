package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class AlchemicalPowerUpEffect extends MobEffect {
    private static final UUID DAMAGE_AMP_UUID = UUID.fromString("7f6c8b30-4d1e-4b8a-9f8e-2c5d4b3a2f1e");
    
    public AlchemicalPowerUpEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
            double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
            
            // Calculate boost: base percentage + (scaling factor * alchemical boost)
            double baseBoost = getBasePercentage(amplifier);
            double scalingBonus = getScalingFactor(amplifier) * alchemicalBoost;
            double totalBoost = baseBoost + scalingBonus;
            
            AttributeModifier modifier = new AttributeModifier(DAMAGE_AMP_UUID, 
                "Alchemical power up boost", totalBoost, AttributeModifier.Operation.MULTIPLY_TOTAL);
            damageAmpInstance.removeModifier(modifier);
            damageAmpInstance.addTransientModifier(modifier);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            damageAmpInstance.removeModifier(DAMAGE_AMP_UUID);
        }
    }
    
    private double getBasePercentage(int amplifier) {
        return ModConfig.COMMON.powerUpBasePercentage.get() * (amplifier + 1);
    }
    
    private double getScalingFactor(int amplifier) {
        return ModConfig.COMMON.powerUpScalingFactor.get() * (amplifier + 1);
    }
}