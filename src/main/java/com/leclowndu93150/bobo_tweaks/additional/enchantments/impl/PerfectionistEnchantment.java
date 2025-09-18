package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class PerfectionistEnchantment extends EventHandlingEnchantment {
    
    public PerfectionistEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Perfectionist.category), 
              new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 25 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Perfectionist.maxLevel;
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

    public void triggerPerfectionist(Player player) {
        if (!EnchantmentModuleConfig.Perfectionist.enabled) return;

        int perfectionistLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.Perfectionist.category);

        if (perfectionistLevel > 0) {
            double attackSpeedBoost = EnchantmentModuleConfig.Perfectionist.baseAttackSpeedBoost +
                    (perfectionistLevel - 1) * EnchantmentModuleConfig.Perfectionist.attackSpeedPerLevel;
            double castSpeedBoost = EnchantmentModuleConfig.Perfectionist.baseCastSpeedBoost +
                    (perfectionistLevel - 1) * EnchantmentModuleConfig.Perfectionist.castSpeedPerLevel;

            EnchantmentTracker.applyTimedModifier(player, Attributes.ATTACK_SPEED, "perfectionist_attack_speed",
                    "Perfectionist Attack Speed", attackSpeedBoost / 100.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Perfectionist.duration);

            var castTimeReduction = EnchantmentTracker.getCastTimeReductionAttribute();
            if (castTimeReduction != null) {
                EnchantmentTracker.applyTimedModifier(player, castTimeReduction, "perfectionist_cast_speed",
                        "Perfectionist Cast Speed", castSpeedBoost,
                        AttributeModifier.Operation.ADDITION,
                        EnchantmentModuleConfig.Perfectionist.duration);
            }
        }
    }
}