package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;

import java.util.UUID;

public class InvigoratingDefensesEnchantment extends EventHandlingEnchantment {
    
    public InvigoratingDefensesEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.InvigoratingDefenses.category), 
              new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
    
    @Override
    public int getMinCost(int level) {
        return 22 + (level - 1) * 11;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.InvigoratingDefenses.maxLevel;
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
    public void onShieldBlock(ShieldBlockEvent event) {
        if (!EnchantmentModuleConfig.InvigoratingDefenses.enabled) return;

        if (event.getEntity() instanceof Player player) {
            handleInvigoratingDefensesTrigger(player);
        }
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.InvigoratingDefenses.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        handleInvigoratingDefensesHealing(event.player);
    }

    private void handleInvigoratingDefensesTrigger(Player player) {
        int invigoratingLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.InvigoratingDefenses.category);

        if (invigoratingLevel > 0) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "invigorating_defenses_cooldown";
            if (EnchantmentTracker.isOnCooldown(playerId, cooldownKey, currentTime)) return;

            EnchantmentTracker.setCooldown(playerId, cooldownKey, currentTime + (EnchantmentModuleConfig.InvigoratingDefenses.cooldown * 50L));

            double speedBoost = EnchantmentModuleConfig.InvigoratingDefenses.baseMovementSpeedBoost +
                    (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.speedBoostPerLevel;
            int duration = EnchantmentModuleConfig.InvigoratingDefenses.baseDuration +
                    (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.durationPerLevel;

            EnchantmentTracker.applyTimedModifier(player, Attributes.MOVEMENT_SPEED, "invigorating_speed",
                    "Invigorating Speed", speedBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    duration);

            double healthToRestore = EnchantmentModuleConfig.InvigoratingDefenses.baseHealthRestored +
                    (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.healthRestoredPerLevel;

            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(playerId);
            data.putDouble("invigorating_healing", healthToRestore);
            data.putLong("invigorating_healing_end", System.currentTimeMillis() + (duration * 50L));
            data.putLong("invigorating_last_heal", System.currentTimeMillis());
        }
    }

    private void handleInvigoratingDefensesHealing(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(playerId);

        if (data.contains("invigorating_healing")) {
            long healingEnd = data.getLong("invigorating_healing_end");
            long currentTime = System.currentTimeMillis();

            if (currentTime > healingEnd) {
                data.remove("invigorating_healing");
                data.remove("invigorating_healing_end");
                data.remove("invigorating_last_heal");
            } else {
                long lastHealTime = data.getLong("invigorating_last_heal");

                if (currentTime - lastHealTime >= 1000) {
                    double healAmount = data.getDouble("invigorating_healing");
                    double percentHealing = healAmount / 100.0;
                    float healingAmount = (float) (player.getMaxHealth() * percentHealing);
                    player.heal(healingAmount);

                    data.putLong("invigorating_last_heal", currentTime);
                }
            }
        }
    }
}