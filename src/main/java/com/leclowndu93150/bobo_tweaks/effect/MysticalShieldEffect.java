package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MysticalShieldEffect extends MobEffect {
    private final Map<UUID, Float> appliedShields = new HashMap<>();
    private static final Map<UUID, Long> shieldExpiryTimes = new ConcurrentHashMap<>();
    
    public MysticalShieldEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x9370DB);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            UUID entityId = entity.getUUID();

            if (appliedShields.containsKey(entityId)) {
                return;
            }
            
            float manaUsed = 0;
            float currentMana = 0;

            if (ModList.get().isLoaded("irons_spellbooks")) {
                try {
                    var magicData = MagicData.getPlayerMagicData(entity);
                    currentMana = magicData.getMana();
                    float manaPercentage = getManaPercentage(amplifier);
                    manaUsed = currentMana * manaPercentage;
                    
                    if (manaUsed > 0) {
                        magicData.setMana(currentMana - manaUsed);
                    }
                } catch (Exception e) {
                    BoboTweaks.getLogger().warn("Failed to interact with Iron's Spellbooks mana system", e);
                }
            }
            
            AttributeInstance damageAmpInstance = entity.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
            double damageAmplifier = damageAmpInstance != null ? damageAmpInstance.getValue() : 0.0D;
            
            float baseShield = getBaseShield(amplifier);
            float scalingBonus = (float) (getScalingFactor(amplifier) * damageAmplifier);
            float manaBonus = manaUsed;
            float totalShield = baseShield + scalingBonus + manaBonus;

            appliedShields.put(entityId, totalShield);
            
            long shieldDurationTicks = (long) (getShieldDuration() * 20);
            long expiryTime = entity.level().getGameTime() + shieldDurationTicks;
            shieldExpiryTimes.put(entityId, expiryTime);
            
            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(currentAbsorption + totalShield);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            UUID entityId = entity.getUUID();
            Float shieldAmount = appliedShields.remove(entityId);
            
            if (shieldAmount != null) {
                float currentAbsorption = entity.getAbsorptionAmount();
                entity.setAbsorptionAmount(Math.max(0, currentAbsorption - shieldAmount));
            }
        }
    }

    @Override
    public boolean isInstantenous() {
        return false;
    }
    
    private float getBaseShield(int amplifier) {
        return ModConfig.COMMON.mysticalBaseShield.get().floatValue();
    }
    
    private float getScalingFactor(int amplifier) {
        return (float) (ModConfig.COMMON.mysticalScalingFactor.get() * (amplifier + 1));
    }
    
    private float getManaPercentage(int amplifier) {
        return (float) (ModConfig.COMMON.mysticalManaPercentage.get() * (amplifier + 1));
    }
    
    private double getShieldDuration() {
        return ModConfig.COMMON.mysticalShieldDuration.get();
    }
    
    public static void tickShields() {
        long currentTime = System.currentTimeMillis() / 50;
        shieldExpiryTimes.entrySet().removeIf(entry -> {
            if (currentTime >= entry.getValue()) {
                return true;
            }
            return false;
        });
    }
    
    public static Map<UUID, Long> getShieldExpiryTimes() {
        return shieldExpiryTimes;
    }
}