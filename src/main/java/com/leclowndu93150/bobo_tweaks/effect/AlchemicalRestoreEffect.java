package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class AlchemicalRestoreEffect extends MobEffect {
    
    public AlchemicalRestoreEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF00);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
            double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
            
            float flatHeal = getFlatHeal(amplifier);
            float scalingBonus = (float) (getScalingFactor(amplifier) * alchemicalBoost);
            float totalHeal = flatHeal + scalingBonus;
            
            entity.heal(totalHeal);
        }
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }
    
    private float getFlatHeal(int amplifier) {
        return (float) (ModConfig.COMMON.restoreFlatHeal.get() * (amplifier + 1));
    }
    
    private float getScalingFactor(int amplifier) {
        return (float) (ModConfig.COMMON.restoreScalingFactor.get() * (amplifier + 1));
    }
}