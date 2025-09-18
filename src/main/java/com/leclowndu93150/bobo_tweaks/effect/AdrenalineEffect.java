package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class AdrenalineEffect extends MobEffect {
    private static final UUID DAMAGE_AMPLIFIER_UUID = UUID.fromString("b8c5f7a2-9e3d-4f82-a5c6-8f7d9e3b2a1c");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("c9d6e8b3-af4e-5093-b6d7-9a8eaf4c3b2d");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("d0e7f9c4-ba5f-6104-c7e8-0a9fba5d4c3e");
    
    public AdrenalineEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6600);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            AttributeModifier damageAmpModifier = new AttributeModifier(
                DAMAGE_AMPLIFIER_UUID,
                "Adrenaline damage amplifier boost",
                0.05 * (amplifier + 1),
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            damageAmpInstance.addTransientModifier(damageAmpModifier);
        }
        
        AttributeInstance critDamageInstance = entity.getAttribute(ALObjects.Attributes.CRIT_DAMAGE.get());
        if (critDamageInstance != null) {
            AttributeModifier critDamageModifier = new AttributeModifier(
                CRIT_DAMAGE_UUID,
                "Adrenaline crit damage boost",
                0.02 * (amplifier + 1),
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            critDamageInstance.addTransientModifier(critDamageModifier);
        }
        
        AttributeInstance critChanceInstance = entity.getAttribute(ALObjects.Attributes.CRIT_CHANCE.get());
        if (critChanceInstance != null) {
            AttributeModifier critChanceModifier = new AttributeModifier(
                CRIT_CHANCE_UUID,
                "Adrenaline crit chance boost",
                0.03 * (amplifier + 1),
                AttributeModifier.Operation.ADDITION
            );
            critChanceInstance.addTransientModifier(critChanceModifier);
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            damageAmpInstance.removeModifier(DAMAGE_AMPLIFIER_UUID);
        }
        
        AttributeInstance critDamageInstance = entity.getAttribute(ALObjects.Attributes.CRIT_DAMAGE.get());
        if (critDamageInstance != null) {
            critDamageInstance.removeModifier(CRIT_DAMAGE_UUID);
        }
        
        AttributeInstance critChanceInstance = entity.getAttribute(ALObjects.Attributes.CRIT_CHANCE.get());
        if (critChanceInstance != null) {
            critChanceInstance.removeModifier(CRIT_CHANCE_UUID);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}