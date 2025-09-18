package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
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

        if (event.getSource().getEntity() instanceof Player killer) {
            handleShadowWalkerTrigger(killer, true);
        }

        handleTeammateKillEffects(event);
    }

    @Override
    public void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            handleShadowWalkerInvisibilityDamage(attacker);
        }
    }

    private void handleShadowWalkerTrigger(Player killer, boolean isDirectKill) {
        int shadowLevel = getEnchantmentLevelFromCategory(killer, EnchantmentModuleConfig.ShadowWalker.category);

        if (shadowLevel > 0) {
            if (isDirectKill) {
                MobEffect trueInvis = MobEffectRegistry.TRUE_INVISIBILITY.get();
                if (trueInvis != null) {
                    killer.addEffect(new MobEffectInstance(trueInvis, EnchantmentModuleConfig.ShadowWalker.invisibilityDuration, 0));
                }
            }

            EnchantmentTracker.applyTimedModifier(killer, Attributes.MOVEMENT_SPEED, "shadow_walker_speed",
                    "Shadow Walker Speed",
                    EnchantmentModuleConfig.ShadowWalker.movementSpeedPercent / 100.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.ShadowWalker.movementSpeedDuration);
        }
    }

    private void handleShadowWalkerInvisibilityDamage(Player attacker) {
        int shadowLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.ShadowWalker.category);

        if (shadowLevel > 0) {
            boolean hasInvisibility = attacker.hasEffect(MobEffects.INVISIBILITY);
            MobEffect trueInvisEffect = MobEffectRegistry.TRUE_INVISIBILITY.get();
            boolean hasTrueInvisibility = trueInvisEffect != null && attacker.hasEffect(trueInvisEffect);

            if (hasInvisibility || hasTrueInvisibility) {
                double damageBoost = EnchantmentModuleConfig.ShadowWalker.baseDamageAmplifier +
                        (shadowLevel - 1) * EnchantmentModuleConfig.ShadowWalker.damageAmplifierPerLevel;

                EnchantmentTracker.applyTimedModifier(attacker, ModAttributes.DAMAGE_AMPLIFIER.get(),
                        "shadow_walker_damage", "Shadow Walker Damage", damageBoost / 100.0,
                        AttributeModifier.Operation.ADDITION, 1);
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
                handleShadowWalkerTrigger(nearbyPlayer, false);
            }
        }
    }
}