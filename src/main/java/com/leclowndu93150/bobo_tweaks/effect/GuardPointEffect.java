package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class GuardPointEffect extends MobEffect {
    public GuardPointEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x4169E1);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            float maxHealth = entity.getMaxHealth();
            float absorptionAmount = maxHealth * getAbsorptionPercentage(amplifier);

            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(currentAbsorption + absorptionAmount);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            float maxHealth = entity.getMaxHealth();
            float absorptionAmount = maxHealth * getAbsorptionPercentage(amplifier);
            
            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(Math.max(0, currentAbsorption - absorptionAmount));
        }
    }
    
    private float getAbsorptionPercentage(int amplifier) {
        return (float) (ModConfig.COMMON.guardPointPercentage.get() * (amplifier + 1));
    }
}