package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class MagicalAttunementEnchantment extends EventHandlingEnchantment {
    
    public MagicalAttunementEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.MagicalAttunement.category), 
              new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        return EnchantmentModuleConfig.MagicalAttunement.maxLevel;
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
        if (!EnchantmentModuleConfig.MagicalAttunement.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;
        int attunementLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.MagicalAttunement.category);

        if (attunementLevel > 0) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "magical_attunement_periodic";
            if (EnchantmentTracker.isOnCooldown(playerId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.MagicalAttunement.baseCooldown -
                    (attunementLevel - 1) * EnchantmentModuleConfig.MagicalAttunement.cooldownDecreasePerLevel;
            EnchantmentTracker.setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50L));

            EnchantmentTracker.setEnchantmentFlag(playerId, "magical_attunement_ready", true,
                    EnchantmentModuleConfig.MagicalAttunement.duration);
        }
    }

    @Override
    public void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.MagicalAttunement.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            handleMagicalAttunementDamage(attacker, event);
        }
    }

    private void handleMagicalAttunementDamage(Player attacker, LivingHurtEvent event) {
        UUID attackerId = attacker.getUUID();
        if (!EnchantmentTracker.hasEnchantmentFlag(attackerId, "magical_attunement_ready")) return;

        int attunementLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.MagicalAttunement.category);

        if (attunementLevel > 0) {
            double baseDamage = EnchantmentModuleConfig.MagicalAttunement.baseDamagePerLevel * attunementLevel;
            double manaBonus = EnchantmentTracker.getPlayerMaxMana(attacker) * EnchantmentModuleConfig.MagicalAttunement.maxManaPercent;
            double totalDamage = baseDamage + manaBonus;
            
            double damageAmplifier = attacker.getAttributeValue(ModAttributes.DAMAGE_AMPLIFIER.get());
            final float lightningDamage = (float)(totalDamage * damageAmplifier);

            if (attacker.level().getServer() != null) {
                attacker.level().getServer().tell(new TickTask(1, () -> {
                    if (event.getEntity().isAlive()) {
                        event.getEntity().hurt(attacker.damageSources().lightningBolt(), lightningDamage);
                    }
                }));
            }

            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(attackerId);
            data.remove("magical_attunement_ready");
            data.remove("magical_attunement_ready_expire");
        }
    }
}