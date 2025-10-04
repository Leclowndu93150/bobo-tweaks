package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class GlassSpiritEnchantment extends EventHandlingEnchantment {
    
    public GlassSpiritEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.GlassSpirit.category), 
              new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
    
    @Override
    public int getMinCost(int level) {
        return 18 + (level - 1) * 9;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.GlassSpirit.maxLevel;
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
        if (!EnchantmentModuleConfig.GlassSpirit.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;
        int glassSpiritLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.GlassSpirit.category);

        if (glassSpiritLevel > 0) {
            handlePassiveA(player, glassSpiritLevel);
        }
    }

    @Override
    public void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.GlassSpirit.enabled) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSource().getEntity() == null) return;

        int glassSpiritLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.GlassSpirit.category);

        if (glassSpiritLevel > 0) {
            handlePassiveB(player, glassSpiritLevel);
        }
    }

    private void handlePassiveA(Player player, int level) {
        if (player.getHealth() >= player.getMaxHealth()) {
            double damageAmpBoost = EnchantmentModuleConfig.GlassSpirit.PassiveA.baseDamageAmplifierBoost +
                    (level - 1) * EnchantmentModuleConfig.GlassSpirit.PassiveA.amplifierBoostPerLevel;

            EnchantmentTracker.applyTimedModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), "glass_spirit_damage_amp",
                    "Glass Spirit Damage Amplifier", damageAmpBoost / 100.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL, 20);
        }
    }

    private void handlePassiveB(Player player, int level) {
        double critRateReduction = EnchantmentModuleConfig.GlassSpirit.PassiveB.critRateReduction;
        double critDamageReduction = EnchantmentModuleConfig.GlassSpirit.PassiveB.critDamageReduction;
        
        int duration = EnchantmentModuleConfig.GlassSpirit.PassiveB.baseDuration -
                (level - 1) * EnchantmentModuleConfig.GlassSpirit.PassiveB.durationDecreasePerLevel;

        if (duration < 20) duration = 20;

        EnchantmentTracker.applyTimedModifier(player, ALObjects.Attributes.CRIT_CHANCE.get(), "glass_spirit_fragile_crit_chance",
                "Glass Spirit Fragile - Crit Rate", -critRateReduction / 100.0,
                AttributeModifier.Operation.MULTIPLY_TOTAL, duration);

        EnchantmentTracker.applyTimedModifier(player, ALObjects.Attributes.CRIT_DAMAGE.get(), "glass_spirit_fragile_crit_damage",
                "Glass Spirit Fragile - Crit Damage", -critDamageReduction / 100.0,
                AttributeModifier.Operation.MULTIPLY_TOTAL, duration);
    }
}
