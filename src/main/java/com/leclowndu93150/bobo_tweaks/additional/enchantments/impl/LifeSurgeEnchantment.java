package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class LifeSurgeEnchantment extends EventHandlingEnchantment {
    
    public LifeSurgeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.LifeSurge.category), 
              new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
    
    @Override
    public int getMinCost(int level) {
        return 24 + (level - 1) * 12;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.LifeSurge.maxLevel;
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
        if (!EnchantmentModuleConfig.LifeSurge.enabled) return;

        if (event.getEntity() instanceof Player player) {
            handleLifeSurgeTrigger(player);
        }
    }

    private void handleLifeSurgeTrigger(Player player) {
        int lifeSurgeLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.LifeSurge.category);

        if (lifeSurgeLevel > 0 &&
                player.getHealth() / player.getMaxHealth() <= EnchantmentModuleConfig.LifeSurge.healthThreshold) {

            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "life_surge_cooldown";
            if (EnchantmentTracker.isOnCooldown(playerId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.LifeSurge.baseCooldown -
                    (lifeSurgeLevel - 1) * EnchantmentModuleConfig.LifeSurge.cooldownDecreasePerLevel;
            EnchantmentTracker.setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50L));

            double flatArmor = EnchantmentModuleConfig.LifeSurge.flatArmorPerLevel * lifeSurgeLevel;
            double percentArmor = EnchantmentModuleConfig.LifeSurge.percentArmorPerLevel * lifeSurgeLevel;

            EnchantmentTracker.applyTimedModifier(player, Attributes.ARMOR, "life_surge_armor_flat",
                    "Life Surge Flat Armor", flatArmor, AttributeModifier.Operation.ADDITION,
                    EnchantmentModuleConfig.LifeSurge.duration);

            AttributeInstance armorInstance = player.getAttribute(Attributes.ARMOR);
            if (armorInstance != null) {
                double currentArmor = armorInstance.getBaseValue();
                EnchantmentTracker.applyTimedModifier(player, Attributes.ARMOR, "life_surge_armor_percent",
                        "Life Surge Percent Armor", currentArmor * (percentArmor / 100.0),
                        AttributeModifier.Operation.ADDITION,
                        EnchantmentModuleConfig.LifeSurge.duration);
            }

            double lifestealBoost = EnchantmentModuleConfig.LifeSurge.flatLifestealPerLevel * lifeSurgeLevel;
            double spellLeechBoost = EnchantmentModuleConfig.LifeSurge.flatSpellStealPerLevel * lifeSurgeLevel;

            EnchantmentTracker.applyTimedModifier(player, ALObjects.Attributes.LIFE_STEAL.get(), "life_surge_lifesteal",
                    "Life Surge Lifesteal", lifestealBoost / 100.0, AttributeModifier.Operation.ADDITION,
                    EnchantmentModuleConfig.LifeSurge.duration);

            EnchantmentTracker.applyTimedModifier(player, ModAttributes.SPELL_LEECH.get(), "life_surge_spell_leech",
                    "Life Surge Spell Leech", spellLeechBoost / 100.0, AttributeModifier.Operation.ADDITION,
                    EnchantmentModuleConfig.LifeSurge.duration);
        }
    }
}