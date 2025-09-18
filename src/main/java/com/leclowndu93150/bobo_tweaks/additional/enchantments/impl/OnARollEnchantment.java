package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.world.entity.EquipmentSlot;

public class OnARollEnchantment extends EventHandlingEnchantment {
    
    public OnARollEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.OnARoll.category),
                new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.OnARoll.maxLevel;
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
}