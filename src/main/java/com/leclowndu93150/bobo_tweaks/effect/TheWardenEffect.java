package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class TheWardenEffect extends MobEffect {
    public TheWardenEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x800080);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            float maxHealth = entity.getMaxHealth();
            float baseAbsorption = maxHealth * getBaseAbsorptionPercentage(amplifier);

            AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
            double damageAmplifier = damageAmpInstance != null ? damageAmpInstance.getValue() : 0.0D;

            float scalingBonus = (float) (getScalingFactor(amplifier) * damageAmplifier);
            float totalAbsorption = baseAbsorption + scalingBonus;

            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(currentAbsorption + totalAbsorption);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            float maxHealth = entity.getMaxHealth();
            float baseAbsorption = maxHealth * getBaseAbsorptionPercentage(amplifier);

            AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
            double damageAmplifier = damageAmpInstance != null ? damageAmpInstance.getValue() : 0.0D;

            float scalingBonus = (float) (getScalingFactor(amplifier) * damageAmplifier);
            float totalAbsorption = baseAbsorption + scalingBonus;
            
            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(Math.max(0, currentAbsorption - totalAbsorption));
        }
    }
    
    private float getBaseAbsorptionPercentage(int amplifier) {
        return (float) (ModConfig.COMMON.wardenBasePercentage.get() * (amplifier + 1));
    }
    
    private float getScalingFactor(int amplifier) {
        return (float) (ModConfig.COMMON.wardenScalingFactor.get() * (amplifier + 1));
    }
}