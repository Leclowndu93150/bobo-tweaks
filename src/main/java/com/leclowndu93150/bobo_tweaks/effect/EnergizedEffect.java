package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.additional.parrysystem.config.ParrySystemConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class EnergizedEffect extends MobEffect {
    private static final UUID DAMAGE_AMPLIFIER_UUID = UUID.fromString("e3f5a8b2-9c4d-4e81-b7d6-1f8e9a5c3b2d");
    private static final UUID ARMOR_UUID = UUID.fromString("f4a6b9c3-0d5e-5f92-c8e7-2a9f0b6d4c3e");
    
    public EnergizedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFF00);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            AttributeModifier damageAmpModifier = new AttributeModifier(
                DAMAGE_AMPLIFIER_UUID,
                "Energized damage amplifier boost",
                ParrySystemConfig.energizedDamageAmplifier * (amplifier + 1),
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            damageAmpInstance.addTransientModifier(damageAmpModifier);
        }
        
        AttributeInstance armorInstance = entity.getAttribute(Attributes.ARMOR);
        if (armorInstance != null) {
            AttributeModifier armorModifier = new AttributeModifier(
                ARMOR_UUID,
                "Energized armor boost",
                ParrySystemConfig.energizedArmorBonus * (amplifier + 1),
                AttributeModifier.Operation.ADDITION
            );
            armorInstance.addTransientModifier(armorModifier);
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            damageAmpInstance.removeModifier(DAMAGE_AMPLIFIER_UUID);
        }
        
        AttributeInstance armorInstance = entity.getAttribute(Attributes.ARMOR);
        if (armorInstance != null) {
            armorInstance.removeModifier(ARMOR_UUID);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
