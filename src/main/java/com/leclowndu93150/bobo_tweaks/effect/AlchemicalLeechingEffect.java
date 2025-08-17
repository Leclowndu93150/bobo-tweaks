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
    private static final UUID SPELL_LEECH_UUID = UUID.fromString("8c2f4d6e-1a3b-4c5d-9e7f-2b8a4d6c8e9f");
    private static final UUID LIFESTEAL_UUID = UUID.fromString("9d3e5f7a-2c4b-5d6e-8f9a-3c5b7d9e1f2a");
    
    public AlchemicalLeechingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8B0000);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance spellLeechInstance = entity.getAttribute(ModAttributes.SPELL_LEECH.get());
        AttributeInstance lifestealInstance = entity.getAttribute(ModAttributes.LIFESTEAL.get());
        AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
        
        double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
        
        if (spellLeechInstance != null) {
            float baseSpellLeech = getBaseSpellLeech(amplifier);
            float spellAlchemicalBonus = (float) (getSpellLeechAlchemicalScaling(amplifier) * alchemicalBoost);
            float totalSpellLeech = baseSpellLeech + spellAlchemicalBonus;
            
            spellLeechInstance.removeModifier(SPELL_LEECH_UUID);
            spellLeechInstance.addTransientModifier(new AttributeModifier(
                SPELL_LEECH_UUID,
                "Alchemical spell leeching",
                totalSpellLeech,
                AttributeModifier.Operation.ADDITION
            ));
        }
        
        if (lifestealInstance != null) {
            float baseLifesteal = getBaseLifesteal(amplifier);
            float lifestealAlchemicalBonus = (float) (getLifestealAlchemicalScaling(amplifier) * alchemicalBoost);
            float totalLifesteal = baseLifesteal + lifestealAlchemicalBonus;
            
            lifestealInstance.removeModifier(LIFESTEAL_UUID);
            lifestealInstance.addTransientModifier(new AttributeModifier(
                LIFESTEAL_UUID,
                "Alchemical lifesteal",
                totalLifesteal,
                AttributeModifier.Operation.ADDITION
            ));
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance spellLeechInstance = entity.getAttribute(ModAttributes.SPELL_LEECH.get());
        if (spellLeechInstance != null) {
            spellLeechInstance.removeModifier(SPELL_LEECH_UUID);
        }
        
        AttributeInstance lifestealInstance = entity.getAttribute(ModAttributes.LIFESTEAL.get());
        if (lifestealInstance != null) {
            lifestealInstance.removeModifier(LIFESTEAL_UUID);
        }
    }
    
    private float getBaseSpellLeech(int amplifier) {
        return ModConfig.COMMON.alchemicalLeechingBase.get().floatValue() * (amplifier + 1);
    }
    
    private float getSpellLeechAlchemicalScaling(int amplifier) {
        return ModConfig.COMMON.alchemicalLeechingScaling.get().floatValue() * (amplifier + 1);
    }
    
    private float getBaseLifesteal(int amplifier) {
        return ModConfig.COMMON.alchemicalLeechingBase.get().floatValue() * (amplifier + 1);
    }
    
    private float getLifestealAlchemicalScaling(int amplifier) {
        return ModConfig.COMMON.alchemicalLifestealScaling.get().floatValue() * (amplifier + 1);
    }
    
    @Override
    public boolean isInstantenous() {
        return false;
    }
}