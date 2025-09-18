package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeadTheChargeEnchantment extends EventHandlingEnchantment {
    
    public LeadTheChargeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.LeadTheCharge.category),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 20 + level * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.LeadTheCharge.maxLevel;
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
        if (!EnchantmentModuleConfig.LeadTheCharge.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            handleLeadTheChargeAttack(attacker, event.getEntity());
        }
    }

    private void handleLeadTheChargeAttack(Player attacker, Entity target) {
        int leadLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.LeadTheCharge.category);
        
        if (leadLevel > 0 && attacker.isSprinting() && target instanceof LivingEntity livingTarget) {
            UUID attackerId = attacker.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "lead_charge_cooldown";
            if (EnchantmentTracker.isOnCooldown(attackerId, cooldownKey, currentTime)) return;
            
            int cooldown = EnchantmentModuleConfig.LeadTheCharge.baseCooldown -
                    (leadLevel - 1) * EnchantmentModuleConfig.LeadTheCharge.cooldownDecreasePerLevel;
            EnchantmentTracker.setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50L));
            
            attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.RAVAGER_ATTACK,
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            int slowDuration = 60;
            double slowAmount = EnchantmentModuleConfig.LeadTheCharge.enemySlowPercent / 100.0;
            livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    slowDuration, (int)(slowAmount * 3), false, true));
            
            AttributeInstance armorInstance = attacker.getAttribute(Attributes.ARMOR);
            double armorValue = armorInstance != null ? armorInstance.getValue() : 0.0;
            double scaledAttackSpeed = EnchantmentModuleConfig.LeadTheCharge.baseAttackSpeed +
                    (armorValue * EnchantmentModuleConfig.LeadTheCharge.armorScaleFactor * 100);
            double scaledCastSpeed = EnchantmentModuleConfig.LeadTheCharge.baseCastSpeed +
                    (armorValue * EnchantmentModuleConfig.LeadTheCharge.armorScaleFactor * 100);
            
            Team attackerTeam = attacker.getTeam();
            List<Player> affectedPlayers = new ArrayList<>();
            affectedPlayers.add(attacker);
            
            if (attackerTeam != null) {
                for (Player teammate : attacker.level().getEntitiesOfClass(Player.class,
                        attacker.getBoundingBox().inflate(50))) {
                    if (teammate.getTeam() != null && teammate.getTeam().equals(attackerTeam) && !teammate.equals(attacker)) {
                        affectedPlayers.add(teammate);
                    }
                }
            }
            
            for (Player player : affectedPlayers) {
                EnchantmentTracker.applyTimedModifier(player, Attributes.ATTACK_SPEED, "lead_charge_attack_speed",
                        "Lead Charge Attack Speed", scaledAttackSpeed / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                        EnchantmentModuleConfig.LeadTheCharge.duration);
                
                var castTimeReduction = EnchantmentTracker.getCastTimeReductionAttribute();
                if (castTimeReduction != null) {
                    EnchantmentTracker.applyTimedModifier(player, castTimeReduction, "lead_charge_cast_speed",
                            "Lead Charge Cast Speed", scaledCastSpeed, AttributeModifier.Operation.ADDITION,
                            EnchantmentModuleConfig.LeadTheCharge.duration);
                }
            }
        }
    }
}