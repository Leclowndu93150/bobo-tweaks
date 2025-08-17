package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import com.leclowndu93150.bobo_tweaks.util.ModDamageSources;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class FuryEffect extends MobEffect {
    private static final Map<UUID, FuryData> furyDataMap = new HashMap<>();
    private static final UUID MAGIC_DAMAGE_UUID = UUID.fromString("7a3f7c4e-9f1b-4c2d-8e3a-1b9c8d7e6f5a");
    
    public FuryEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF4500);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            float currentHealth = entity.getHealth();
            float healthDrainPercentage = getHealthDrainPercentage(amplifier);
            float healthToDrain = currentHealth * healthDrainPercentage;
            
            if (healthToDrain > 0 && currentHealth > healthToDrain) {
                DamageSource furyDamage = ModDamageSources.lifeDrain(entity);
                entity.hurt(furyDamage, healthToDrain);
                
                double damageAmplifier = 0.0;
                AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
                if (damageAmpInstance != null) {
                    damageAmplifier = damageAmpInstance.getValue();
                }
                
                float magicDamageBonus = healthToDrain * getMagicDamageMultiplier();
                int attackCount = calculateAttackCount(damageAmplifier);
                
                FuryData data = new FuryData(magicDamageBonus, attackCount);
                furyDataMap.put(entity.getUUID(), data);
                
                if (ModList.get().isLoaded("attributeslib")) {
                    try {
                        applyMagicDamageAttribute(entity, magicDamageBonus);
                    } catch (Exception e) {
                        BoboTweaks.getLogger().warn("Failed to apply magic damage attribute", e);
                    }
                }
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            furyDataMap.remove(entity.getUUID());
            
            if (ModList.get().isLoaded("attributeslib")) {
                try {
                    removeMagicDamageAttribute(entity);
                } catch (Exception e) {
                    BoboTweaks.getLogger().warn("Failed to remove magic damage attribute", e);
                }
            }
        }
    }
    
    private void applyMagicDamageAttribute(LivingEntity entity, float magicDamageBonus) {
        try {
            var magicDamageAttribute = net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES
                .getValue(new net.minecraft.resources.ResourceLocation("attributeslib", "magic_damage"));
            if (magicDamageAttribute != null) {
                AttributeInstance instance = entity.getAttribute(magicDamageAttribute);
                if (instance != null) {
                    instance.removeModifier(MAGIC_DAMAGE_UUID);
                    instance.addTransientModifier(new AttributeModifier(
                        MAGIC_DAMAGE_UUID,
                        "Fury magic damage",
                        magicDamageBonus,
                        AttributeModifier.Operation.ADDITION
                    ));
                }
            }
        } catch (Exception e) {
            BoboTweaks.getLogger().warn("AttributesLib not available or magic damage attribute not found", e);
        }
    }
    
    private static void removeMagicDamageAttribute(LivingEntity entity) {
        try {
            var magicDamageAttribute = net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES
                .getValue(new net.minecraft.resources.ResourceLocation("attributeslib", "magic_damage"));
            if (magicDamageAttribute != null) {
                AttributeInstance instance = entity.getAttribute(magicDamageAttribute);
                if (instance != null) {
                    instance.removeModifier(MAGIC_DAMAGE_UUID);
                }
            }
        } catch (Exception e) {
            BoboTweaks.getLogger().warn("AttributesLib not available or magic damage attribute not found", e);
        }
    }
    
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            FuryData data = furyDataMap.get(attacker.getUUID());
            if (data != null && data.remainingAttacks > 0) {
                data.remainingAttacks--;
                
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                
                if (data.remainingAttacks <= 0) {
                    furyDataMap.remove(attacker.getUUID());
                    if (ModList.get().isLoaded("attributeslib")) {
                        try {
                            removeMagicDamageAttribute(attacker);
                        } catch (Exception e) {
                            BoboTweaks.getLogger().warn("Failed to remove magic damage attribute", e);
                        }
                    }
                    
                    MobEffectInstance furyInstance = attacker.getEffect(ModPotions.FURY.get());
                    if (furyInstance != null) {
                        attacker.removeEffect(ModPotions.FURY.get());
                    }
                }
            }
        }
    }
    
    private int calculateAttackCount(double damageAmplifier) {
        double scalingFactor = ModConfig.COMMON.furyAttackScaling.get();
        return Math.max(1, (int) Math.floor(1 + (scalingFactor * damageAmplifier)));
    }
    
    private float getHealthDrainPercentage(int amplifier) {
        return (float) (ModConfig.COMMON.furyHealthDrainPercentage.get() * (amplifier + 1));
    }
    
    private float getMagicDamageMultiplier() {
        return ModConfig.COMMON.furyFireDamageMultiplier.get().floatValue();
    }
    
    @Override
    public boolean isInstantenous() {
        return false;
    }
    
    private static class FuryData {
        public float magicDamageBonus;
        public int remainingAttacks;
        
        public FuryData(float magicDamageBonus, int remainingAttacks) {
            this.magicDamageBonus = magicDamageBonus;
            this.remainingAttacks = remainingAttacks;
        }
    }
}