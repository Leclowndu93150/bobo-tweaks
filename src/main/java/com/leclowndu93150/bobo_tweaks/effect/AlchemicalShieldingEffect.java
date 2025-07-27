package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class AlchemicalShieldingEffect extends MobEffect {
    
    public AlchemicalShieldingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x87CEEB);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
            double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
            
            // Calculate shield amount: flat amount + (scaling factor * alchemical boost)
            float flatShield = getFlatShield(amplifier);
            float scalingBonus = (float) (getScalingFactor(amplifier) * alchemicalBoost);
            float totalShield = flatShield + scalingBonus;

            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(currentAbsorption + totalShield);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
            double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
            
            // Calculate shield amount that was added
            float flatShield = getFlatShield(amplifier);
            float scalingBonus = (float) (getScalingFactor(amplifier) * alchemicalBoost);
            float totalShield = flatShield + scalingBonus;
            
            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(Math.max(0, currentAbsorption - totalShield));
        }
    }
    
    private float getFlatShield(int amplifier) {
        return (float) (ModConfig.COMMON.shieldingFlatShield.get() * (amplifier + 1));
    }
    
    private float getScalingFactor(int amplifier) {
        return (float) (ModConfig.COMMON.shieldingScalingFactor.get() * (amplifier + 1));
    }
}