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

public class AlchemicalLeechingEffect extends MobEffect {
    private static final UUID LIFE_LEECH_UUID = UUID.fromString("8c2f4d6e-1a3b-4c5d-9e7f-2b8a4d6c8e9f");
    
    public AlchemicalLeechingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8B0000);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance lifeLeechInstance = entity.getAttribute(ModAttributes.LIFE_LEECH.get());
        AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
        
        if (lifeLeechInstance != null) {
            double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
            
            float baseLeech = getBaseLifeLeech(amplifier);
            float alchemicalBonus = (float) (getAlchemicalScaling(amplifier) * alchemicalBoost);
            float totalLeech = baseLeech + alchemicalBonus;
            
            lifeLeechInstance.removeModifier(LIFE_LEECH_UUID);
            lifeLeechInstance.addTransientModifier(new AttributeModifier(
                LIFE_LEECH_UUID,
                "Alchemical leeching",
                totalLeech,
                AttributeModifier.Operation.ADDITION
            ));
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance lifeLeechInstance = entity.getAttribute(ModAttributes.LIFE_LEECH.get());
        if (lifeLeechInstance != null) {
            lifeLeechInstance.removeModifier(LIFE_LEECH_UUID);
        }
    }
    
    private float getBaseLifeLeech(int amplifier) {
        return ModConfig.COMMON.alchemicalLeechingBase.get().floatValue() * (amplifier + 1);
    }
    
    private float getAlchemicalScaling(int amplifier) {
        return ModConfig.COMMON.alchemicalLeechingScaling.get().floatValue() * (amplifier + 1);
    }
    
    @Override
    public boolean isInstantenous() {
        return false;
    }
}