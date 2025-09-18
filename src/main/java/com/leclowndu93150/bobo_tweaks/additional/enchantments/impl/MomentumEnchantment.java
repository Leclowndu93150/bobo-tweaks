package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.UUID;

public class MomentumEnchantment extends EventHandlingEnchantment {
    
    public MomentumEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Momentum.category), 
              new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 12 + (level - 1) * 8;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Momentum.maxLevel;
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
    public void onLivingDeath(LivingDeathEvent event) {
        if (!EnchantmentModuleConfig.Momentum.enabled) return;

        if (event.getSource().getEntity() instanceof Player killer) {
            handleMomentumTrigger(killer, event.getEntity(), true);
        }

        handleTeammateKillEffects(event);
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.Momentum.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        handleMomentumExpiry(event.player);
    }

    private void handleMomentumTrigger(Player killer, LivingEntity victim, boolean isDirectKill) {
        int momentumLevel = getEnchantmentLevelFromCategory(killer, EnchantmentModuleConfig.Momentum.category);

        if (momentumLevel > 0 && (victim instanceof Mob)) {
            UUID killerId = killer.getUUID();

            int stacksToAdd = isDirectKill ?
                    EnchantmentModuleConfig.Momentum.killStacks :
                    EnchantmentModuleConfig.Momentum.allyKillStacks;

            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(killerId);
            int currentStacks = data.getInt("momentum_stacks");
            int maxStacks = EnchantmentModuleConfig.Momentum.baseMaxStacks +
                    (momentumLevel - 1) * EnchantmentModuleConfig.Momentum.maxStackIncreasePerLevel;

            data.putLong("momentum_expire_time",
                    System.currentTimeMillis() + (EnchantmentModuleConfig.Momentum.stackDuration * 50L));

            if (currentStacks < maxStacks) {
                int newStacks = Math.min(currentStacks + stacksToAdd, maxStacks);
                data.putInt("momentum_stacks", newStacks);
                updateMomentumDamage(killer, newStacks, momentumLevel);
            }
        }
    }

    private void handleTeammateKillEffects(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob victim)) return;

        Player directKiller = event.getSource().getEntity() instanceof Player ?
                (Player) event.getSource().getEntity() : null;

        if (directKiller == null) return;

        Team killerTeam = directKiller.getTeam();
        if (killerTeam == null) return;

        for (Player nearbyPlayer : directKiller.level().getEntitiesOfClass(Player.class,
                directKiller.getBoundingBox().inflate(50))) {

            if (nearbyPlayer.equals(directKiller)) continue;

            Team nearbyTeam = nearbyPlayer.getTeam();
            if (nearbyTeam != null && nearbyTeam.equals(killerTeam)) {
                handleMomentumTrigger(nearbyPlayer, victim, false);
            }
        }
    }

    private void updateMomentumDamage(Player player, int stacks, int level) {
        double totalDamageBoost = stacks * (EnchantmentModuleConfig.Momentum.damageBoostPerStack +
                (level - 1) * EnchantmentModuleConfig.Momentum.damageBoostPerLevel) / 100.0;

        EnchantmentTracker.removeModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), "momentum_damage");
        
        if (stacks > 0) {
            EnchantmentTracker.applyTimedModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), "momentum_damage",
                    "Momentum Damage", totalDamageBoost, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Momentum.stackDuration);
        }
    }

    private void handleMomentumExpiry(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(playerId);
        long expireTime = data.getLong("momentum_expire_time");

        if (System.currentTimeMillis() > expireTime && data.getInt("momentum_stacks") > 0) {
            data.putInt("momentum_stacks", 0);
            data.remove("momentum_expire_time");

            EnchantmentTracker.removeModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), "momentum_damage");
        }
    }
}