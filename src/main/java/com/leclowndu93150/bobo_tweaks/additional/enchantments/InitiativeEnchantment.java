package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InitiativeEnchantment extends Enchantment {
    
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
}