package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class ReprisalEnchantment extends EventHandlingEnchantment {
    
    public ReprisalEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Reprisal.category), 
              new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Reprisal.maxLevel;
    }
    
    @Override
    public boolean isTreasureOnly() {
        return false;
    }
    
    @Override
    public boolean isCurse() {
        return false;
    }
    
    @Override
    public boolean isTradeable() {
        return true;
    }
    
    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.Reprisal.enabled) return;

        if (event.getEntity() instanceof Player player) {
            handleReprisalTrigger(player, event.getSource());
        }

        // Handle sound and message for active reprisal
        if (event.getSource().getEntity() instanceof Player attacker) {
            if (EnchantmentTracker.hasEnchantmentFlag(attacker.getUUID(), "reprisal_active")) {
                if (!attacker.level().isClientSide()) {
                    attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.ELDER_GUARDIAN_HURT_LAND, 
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    attacker.sendSystemMessage(Component.literal("Reprisal: +15% Damage!"));
                }
            }
        }
    }

    private void handleReprisalTrigger(Player player, DamageSource source) {
        int reprisalLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.Reprisal.category);

        if (reprisalLevel > 0 && source.getEntity() instanceof Mob) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "reprisal_cooldown";
            if (EnchantmentTracker.isOnCooldown(playerId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.Reprisal.baseCooldown -
                    (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.cooldownReductionPerLevel;
            EnchantmentTracker.setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50L));

            double damageBoost = EnchantmentModuleConfig.Reprisal.baseDamageBoost +
                    (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.damageBoostPerLevel;
            int duration = EnchantmentModuleConfig.Reprisal.baseDuration +
                    (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.durationPerLevel;

            EnchantmentTracker.applyTimedModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), "reprisal_damage",
                    "Reprisal Damage", damageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL, duration);

            EnchantmentTracker.setEnchantmentFlag(playerId, "reprisal_active", true, duration);
        }
    }
}