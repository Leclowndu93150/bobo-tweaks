package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SaintsPledgeEnchantment extends Enchantment {
    
    public SaintsPledgeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.SaintsPledge.category),
                new EquipmentSlot[]{EquipmentSlot.OFFHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + level * 8;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.SaintsPledge.maxLevel;
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
}