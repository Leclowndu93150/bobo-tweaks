package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class InitiativeEnchantment extends EventHandlingEnchantment {
    
    public InitiativeEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Initiative.category),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 10 + level * 7;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Initiative.maxLevel;
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
    public void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.Initiative.enabled) return;

        if (event.getEntity() instanceof Player player) {
            resetInitiativeTimer(player);
        }
    }

    @Override
    public void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.Initiative.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            handleInitiativeAttack(attacker, event.getEntity());
        }
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.Initiative.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        updateInitiativeTimer(event.player);
    }

    private void handleInitiativeAttack(Player attacker, Entity target) {
        int initiativeLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.Initiative.category);
        
        if (initiativeLevel > 0 && target instanceof LivingEntity livingTarget) {
            UUID attackerId = attacker.getUUID();
            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(attackerId);
            long lastHurt = data.getLong("last_hurt_time");
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastHurt >= EnchantmentModuleConfig.Initiative.notHurtTimer * 50L) {
                double flatDamageBoost = EnchantmentModuleConfig.Initiative.baseFlatDamageBoost +
                        (initiativeLevel - 1) * EnchantmentModuleConfig.Initiative.flatDamageBoostPerLevel;
                
                AttributeInstance armorInstance = attacker.getAttribute(Attributes.ARMOR);
                double armorValue = armorInstance != null ? armorInstance.getValue() : 0.0;
                double scaledDamageBoost = EnchantmentModuleConfig.Initiative.percentDamageBoost + 
                        (armorValue * EnchantmentModuleConfig.Initiative.armorScaleFactor);
                
                double armorBoost = EnchantmentModuleConfig.Initiative.baseArmorBoost +
                        (initiativeLevel - 1) * EnchantmentModuleConfig.Initiative.armorBoostPerLevel;
                
                int duration = EnchantmentModuleConfig.Initiative.baseDuration +
                        (initiativeLevel - 1) * EnchantmentModuleConfig.Initiative.durationPerLevel;
                
                Team attackerTeam = attacker.getTeam();
                if (attackerTeam != null) {
                    for (Player teammate : attacker.level().getEntitiesOfClass(Player.class,
                            attacker.getBoundingBox().inflate(50))) {
                        if (teammate.getTeam() != null && teammate.getTeam().equals(attackerTeam) && !teammate.equals(attacker)) {
                            EnchantmentTracker.applyTimedModifier(teammate, ModAttributes.DAMAGE_AMPLIFIER.get(), 
                                    "initiative_flat_damage", "Initiative Flat Damage", 
                                    flatDamageBoost / 100.0, AttributeModifier.Operation.ADDITION, duration);
                            
                            EnchantmentTracker.applyTimedModifier(teammate, ModAttributes.DAMAGE_AMPLIFIER.get(), 
                                    "initiative_percent_damage", "Initiative Percent Damage", 
                                    scaledDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL, duration);
                        }
                    }
                }
                
                EnchantmentTracker.applyTimedModifier(attacker, Attributes.ARMOR, "initiative_armor",
                        "Initiative Armor", armorBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL, duration);
                
                attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                
                data.putLong("last_hurt_time", currentTime);
            }
        }
    }
    
    private void resetInitiativeTimer(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(playerId);
        data.putLong("last_hurt_time", System.currentTimeMillis());
    }
    
    private void updateInitiativeTimer(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(playerId);
        if (!data.contains("last_hurt_time")) {
            data.putLong("last_hurt_time", 0L);
        }
    }
}