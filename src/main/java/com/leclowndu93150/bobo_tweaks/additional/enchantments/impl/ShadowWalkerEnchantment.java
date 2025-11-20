package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ShadowWalkerEnchantment extends EventHandlingEnchantment {
    
    public ShadowWalkerEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.ShadowWalker.category), 
              new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
    
    @Override
    public int getMinCost(int level) {
        return 26 + (level - 1) * 13;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.ShadowWalker.maxLevel;
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
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;
        
        if (!(event.getEntity() instanceof Mob)) return;

        if (event.getSource().getEntity() instanceof Player killer) {
            int shadowLevel = getEnchantmentLevelFromCategory(killer, EnchantmentModuleConfig.ShadowWalker.category);
            if (shadowLevel > 0) {
                handleShadowWalkerKill(killer, shadowLevel, true);
            }
            
            handleTeammateKillEffects(killer);
        }
    }

    @Override
    public void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            int shadowLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.ShadowWalker.category);
            
            if (shadowLevel > 0) {
                applyInvisibilityDamageBoost(attacker, shadowLevel);
            }
        }
    }

    private void handleShadowWalkerKill(Player killer, int shadowLevel, boolean isDirectKill) {
        EnchantmentTracker.applyTimedModifier(killer, Attributes.MOVEMENT_SPEED, "shadow_walker_speed",
                "Shadow Walker Speed",
                EnchantmentModuleConfig.ShadowWalker.movementSpeedPercent / 100.0,
                AttributeModifier.Operation.MULTIPLY_TOTAL,
                EnchantmentModuleConfig.ShadowWalker.movementSpeedDuration);

        if (isDirectKill && killer.level() instanceof ServerLevel serverLevel) {
            MobEffect trueInvis = MobEffectRegistry.TRUE_INVISIBILITY.get();
            if (trueInvis != null) {
                int delay = EnchantmentModuleConfig.ShadowWalker.invisibilityDelayTicks;
                serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                    serverLevel.getServer().getTickCount() + delay,
                    () -> {
                        if (killer.isAlive()) {
                            killer.addEffect(new MobEffectInstance(trueInvis, 
                                EnchantmentModuleConfig.ShadowWalker.invisibilityDuration, 0, false, true, true));
                        }
                    }
                ));
            }
        }
    }

    private void applyInvisibilityDamageBoost(Player attacker, int shadowLevel) {
        boolean hasInvisibility = attacker.hasEffect(MobEffects.INVISIBILITY);
        MobEffect trueInvisEffect = MobEffectRegistry.TRUE_INVISIBILITY.get();
        boolean hasTrueInvisibility = trueInvisEffect != null && attacker.hasEffect(trueInvisEffect);

        if (hasInvisibility || hasTrueInvisibility) {
            double flatBoost = EnchantmentModuleConfig.ShadowWalker.baseDamageAmplifier +
                    (shadowLevel - 1) * EnchantmentModuleConfig.ShadowWalker.damageAmplifierPerLevel;

            EnchantmentTracker.applyTimedModifier(attacker, ModAttributes.DAMAGE_AMPLIFIER.get(),
                    "shadow_walker_damage_flat", "Shadow Walker Damage (Flat)", flatBoost / 100.0,
                    AttributeModifier.Operation.ADDITION, 1);
            
            if (EnchantmentModuleConfig.ShadowWalker.percentDamageAmplifier > 0) {
                EnchantmentTracker.applyTimedModifier(attacker, ModAttributes.DAMAGE_AMPLIFIER.get(),
                        "shadow_walker_damage_percent", "Shadow Walker Damage (Percent)", 
                        EnchantmentModuleConfig.ShadowWalker.percentDamageAmplifier / 100.0,
                        AttributeModifier.Operation.MULTIPLY_TOTAL, 1);
            }
        }
    }

    private void handleTeammateKillEffects(Player directKiller) {
        Team killerTeam = directKiller.getTeam();
        if (killerTeam == null) return;

        for (Player nearbyPlayer : directKiller.level().getEntitiesOfClass(Player.class,
                directKiller.getBoundingBox().inflate(50))) {

            if (nearbyPlayer.equals(directKiller)) continue;

            Team nearbyTeam = nearbyPlayer.getTeam();
            if (nearbyTeam != null && nearbyTeam.equals(killerTeam)) {
                int shadowLevel = getEnchantmentLevelFromCategory(nearbyPlayer, EnchantmentModuleConfig.ShadowWalker.category);
                if (shadowLevel > 0) {
                    handleShadowWalkerKill(nearbyPlayer, shadowLevel, false);
                }
            }
        }
    }
}