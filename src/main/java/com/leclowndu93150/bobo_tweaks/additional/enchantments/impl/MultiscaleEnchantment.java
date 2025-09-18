package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public class MultiscaleEnchantment extends EventHandlingEnchantment {
    
    public MultiscaleEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Multiscale.category), 
              new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
    
    @Override
    public int getMinCost(int level) {
        return 14 + (level - 1) * 7;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Multiscale.maxLevel;
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
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.Multiscale.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        handleMultiscaleContinuous(event.player);
    }

    private void handleMultiscaleContinuous(Player player) {
        int multiscaleLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.Multiscale.category);

        boolean isFullHealth = player.getHealth() >= player.getMaxHealth() - 0.01f;
        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);

        if (armorAttr != null) {
            boolean hasMultiscaleFlat = EnchantmentTracker.getOrCreateNamedUUID(player.getUUID(), "multiscale_flat_armor") != null;
            boolean hasMultiscalePercent = EnchantmentTracker.getOrCreateNamedUUID(player.getUUID(), "multiscale_percent_armor") != null;

            if (multiscaleLevel > 0 && isFullHealth && !hasMultiscaleFlat) {
                double flatArmor = EnchantmentModuleConfig.Multiscale.flatArmorPerLevel * multiscaleLevel;
                double percentArmor = EnchantmentModuleConfig.Multiscale.percentArmorPerLevel * multiscaleLevel;
                double currentArmor = armorAttr.getBaseValue();
                double percentBonus = currentArmor * (percentArmor / 100.0);

                EnchantmentTracker.applyTimedModifier(player, Attributes.ARMOR, "multiscale_flat_armor",
                        "Multiscale Flat Armor", flatArmor, AttributeModifier.Operation.ADDITION, Integer.MAX_VALUE);
                
                EnchantmentTracker.applyTimedModifier(player, Attributes.ARMOR, "multiscale_percent_armor",
                        "Multiscale Percent Armor", percentBonus, AttributeModifier.Operation.ADDITION, Integer.MAX_VALUE);
            } else if ((!isFullHealth || multiscaleLevel == 0) && (hasMultiscaleFlat || hasMultiscalePercent)) {
                EnchantmentTracker.removeModifier(player, Attributes.ARMOR, "multiscale_flat_armor");
                EnchantmentTracker.removeModifier(player, Attributes.ARMOR, "multiscale_percent_armor");
            }
        }
    }
}