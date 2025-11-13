package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class RisingEdgeEnchantment extends EventHandlingEnchantment {
    
    public RisingEdgeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.RisingEdge.category),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 25 + level * 12;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.RisingEdge.maxLevel;
    }
    
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    
    @Override
    public boolean isTradeable() {
        return false;
    }
    
    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.RisingEdge.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            LivingEntity target = event.getEntity();
            handleRisingEdgeAttack(attacker, target);
        }
    }

    @Override
    public void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.RisingEdge.enabled) return;
        
        if (event.getSource().getEntity() instanceof Player attacker) {
            handleRisingEdgeDamage(attacker, event);
        }
    }

    private void handleRisingEdgeAttack(Player attacker, LivingEntity target) {
        int risingLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.RisingEdge.category);
        
        if (risingLevel > 0 && attacker.isSprinting()) {
            UUID attackerId = attacker.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "rising_edge_cooldown";
            if (!EnchantmentTracker.isOnCooldown(attackerId, cooldownKey, currentTime)) {
                target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,
                        EnchantmentModuleConfig.RisingEdge.PassiveA.slowFallingDuration, 0, false, true, true));
                
                if (!attacker.level().isClientSide && attacker.level() instanceof ServerLevel serverLevel) {
                    ModNetworking.playSound(serverLevel, target.getX(), target.getY(), target.getZ(), 
                            SoundEvents.RAVAGER_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    private void handleRisingEdgeDamage(Player attacker, LivingHurtEvent event) {
        int risingLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.RisingEdge.category);
        
        if (risingLevel > 0) {
            LivingEntity target = event.getEntity();
            UUID attackerId = attacker.getUUID();
            
            if (attacker.isSprinting()) {
                long currentTime = System.currentTimeMillis();
                String cooldownKey = "rising_edge_cooldown";
                
                if (!EnchantmentTracker.isOnCooldown(attackerId, cooldownKey, currentTime)) {
                    AttributeInstance damageAmpInstance = attacker.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
                    double totalBoost = getTotalBoost(damageAmpInstance, risingLevel);
                    event.setAmount(event.getAmount() * (float)(1 + totalBoost));
                }
            }
            
            if (isEntityAirborne(target)) {
                double airborneDamageBoost = (EnchantmentModuleConfig.RisingEdge.PassiveB.baseAirborneDamage +
                        (risingLevel - 1) * EnchantmentModuleConfig.RisingEdge.PassiveB.airborneDamagePerLevel) / 100.0;
                event.setAmount(event.getAmount() * (float)(1 + airborneDamageBoost));
            }
        }
    }

    private static double getTotalBoost(AttributeInstance damageAmpInstance, int risingLevel) {
        double damageAmp = damageAmpInstance != null ? damageAmpInstance.getValue() : 0.0;

        double baseDamageBoost = EnchantmentModuleConfig.RisingEdge.PassiveA.baseAttackDamageBoost / 100.0;
        double scaleFactor = EnchantmentModuleConfig.RisingEdge.PassiveA.baseScaleFactor +
                (risingLevel - 1) * EnchantmentModuleConfig.RisingEdge.PassiveA.scaleFactorPerLevel;
        return baseDamageBoost * (1 + damageAmp * scaleFactor);
    }

    private boolean isEntityAirborne(LivingEntity entity) {
        if (entity.hasEffect(MobEffects.LEVITATION) || entity.isFallFlying()) {
            return true;
        }
        
        BlockPos posBelow = entity.blockPosition().below();
        BlockState blockBelow = entity.level().getBlockState(posBelow);
        
        return blockBelow.isAir() || blockBelow.canBeReplaced();
    }
}