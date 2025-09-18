package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import com.leclowndu93150.bobo_tweaks.util.ModDamageSources;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class FuryEffect extends MobEffect {
    private static final Map<UUID, FuryData> furyDataMap = new HashMap<>();
    
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
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            furyDataMap.remove(entity.getUUID());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            FuryData data = furyDataMap.get(attacker.getUUID());
            if (data != null && data.remainingAttacks > 0) {
                DamageSource magicDamageCheck = attacker.damageSources().magic();
                if (!event.getSource().type().equals(magicDamageCheck.type())) {
                    LivingEntity target = event.getEntity();
                    if (!target.level().isClientSide()) {
                        DamageSource magicDamage = attacker.damageSources().magic();
                        target.hurt(magicDamage, data.magicDamageBonus);

                        if (attacker.level() instanceof ServerLevel serverLevel) {
                            ModNetworking.playSound(serverLevel, attacker.getX(), attacker.getY(), attacker.getZ(),
                                SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                        
                        data.remainingAttacks--;
                        
                        if (data.remainingAttacks <= 0) {
                            furyDataMap.remove(attacker.getUUID());
                            
                            MobEffectInstance furyInstance = attacker.getEffect(ModPotions.FURY.get());
                            if (furyInstance != null) {
                                attacker.removeEffect(ModPotions.FURY.get());
                            }
                        }
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