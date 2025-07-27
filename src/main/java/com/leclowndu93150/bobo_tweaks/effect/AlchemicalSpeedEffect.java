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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.ModList;

import java.util.Objects;
import java.util.UUID;

public class AlchemicalSpeedEffect extends MobEffect {
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("8f6c8b30-4d1e-4b8a-9f8e-2c5d4b3a2f1f");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("9f6c8b30-4d1e-4b8a-9f8e-2c5d4b3a2f20");
    private static final UUID CAST_TIME_UUID = UUID.fromString("af6c8b30-5d1e-6b8a-0f8e-3c5d4b3a2f21");
    
    public AlchemicalSpeedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFF00);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);

        AttributeInstance alchemicalBoostInstance = entity.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
        double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;
        
        // Movement Speed (flat, doesn't scale with alchemical boost)
        AttributeInstance movementSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            double movementBoost = getMovementSpeedBoost(amplifier);
            AttributeModifier movementModifier = new AttributeModifier(MOVEMENT_SPEED_UUID,
                "Alchemical speed movement boost", movementBoost, AttributeModifier.Operation.MULTIPLY_BASE);
            movementSpeed.removeModifier(movementModifier);
            movementSpeed.addTransientModifier(movementModifier);
        }
        
        // Attack Speed (scales with alchemical boost)
        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            double baseAttackSpeed = getAttackSpeedBoost(amplifier);
            double scalingBonus = getAttackSpeedScaling(amplifier) * alchemicalBoost;
            double totalAttackSpeed = baseAttackSpeed + scalingBonus;
            
            AttributeModifier attackModifier = new AttributeModifier(ATTACK_SPEED_UUID,
                "Alchemical speed attack boost", totalAttackSpeed, AttributeModifier.Operation.MULTIPLY_BASE);
            attackSpeed.removeModifier(attackModifier);
            attackSpeed.addTransientModifier(attackModifier);
        }
        
        // Cast Speed (Iron's Spellbooks integration)
        if (ModList.get().isLoaded("irons_spellbooks")) {
            try {
                var castTimeAttr = entity.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(
                                new ResourceLocation("irons_spellbooks", "cast_time_reduction")
                        ))
                );
                
                if (castTimeAttr != null) {
                    double baseCastSpeed = getAttackSpeedBoost(amplifier);
                    double scalingBonus = getAttackSpeedScaling(amplifier) * alchemicalBoost;
                    double totalCastSpeed = baseCastSpeed + scalingBonus;
                    
                    AttributeModifier castModifier = new AttributeModifier(CAST_TIME_UUID,
                        "Alchemical speed cast boost", totalCastSpeed, AttributeModifier.Operation.MULTIPLY_BASE);
                    castTimeAttr.removeModifier(castModifier);
                    castTimeAttr.addTransientModifier(castModifier);
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance movementSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(MOVEMENT_SPEED_UUID);
        }
        
        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.removeModifier(ATTACK_SPEED_UUID);
        }

        if (ModList.get().isLoaded("irons_spellbooks")) {
            try {
                var castTimeAttr = entity.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(
                                new ResourceLocation("irons_spellbooks", "cast_time_reduction")
                        ))
                );
                
                if (castTimeAttr != null) {
                    castTimeAttr.removeModifier(CAST_TIME_UUID);
                }
            } catch (Exception e) {
            }
        }
    }
    
    private double getMovementSpeedBoost(int amplifier) {
        return ModConfig.COMMON.speedMovementBoost.get();
    }
    
    private double getAttackSpeedBoost(int amplifier) {
        return ModConfig.COMMON.speedAttackSpeedBoost.get() * (amplifier + 1);
    }
    
    private double getAttackSpeedScaling(int amplifier) {
        return ModConfig.COMMON.speedAttackSpeedScaling.get() * (amplifier + 1);
    }
}