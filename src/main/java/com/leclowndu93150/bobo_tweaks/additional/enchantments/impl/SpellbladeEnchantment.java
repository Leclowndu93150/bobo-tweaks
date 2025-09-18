package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.UUID;

public class SpellbladeEnchantment extends EventHandlingEnchantment {

    public SpellbladeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Spellblade.category),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Spellblade.maxLevel;
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
    public void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            handleSpellbladePassiveA(attacker, event.getSource());
        }
    }

    @Override
    public void onSpellCast(SpellOnCastEvent event) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;

        handleSpellbladeSpellCast(event.getEntity());
    }

    private void handleSpellbladePassiveA(Player attacker, DamageSource source) {
        ItemStack weapon = getEnchantedItemStackFromCategory(attacker, EnchantmentModuleConfig.Spellblade.category);
        int spellbladeLevel = EnchantmentHelper.getItemEnchantmentLevel(this, weapon);

        if (spellbladeLevel > 0) {
            UUID attackerId = attacker.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "spellblade_passive_a_cooldown";
            if (EnchantmentTracker.isOnCooldown(attackerId, cooldownKey, currentTime)) return;

            boolean isMeleeAttack = !source.isIndirect() && source.getDirectEntity() == attacker;
            boolean isRangedAttack = source.isIndirect() ||
                    source.getDirectEntity() instanceof AbstractArrow ||
                    weapon.getItem() instanceof BowItem ||
                    weapon.getItem() instanceof CrossbowItem ||
                    weapon.getItem() instanceof TridentItem;

            if (isMeleeAttack || isRangedAttack) {
                int cooldown = EnchantmentModuleConfig.Spellblade.PassiveA.baseCooldown -
                        (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.cooldownDecreasePerLevel;
                EnchantmentTracker.setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50L));

                double spellPowerBoost;
                if (isMeleeAttack) {
                    spellPowerBoost = EnchantmentModuleConfig.Spellblade.PassiveA.baseMeleeSpellPowerBoost +
                            (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.meleeBoostPerLevel;
                } else {
                    spellPowerBoost = EnchantmentModuleConfig.Spellblade.PassiveA.baseRangedSpellPowerBoost +
                            (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.rangedBoostPerLevel;
                }

                var spellPower = EnchantmentTracker.getSpellPowerAttribute();
                if (spellPower != null) {
                    EnchantmentTracker.applyTimedModifier(attacker, spellPower, "spellblade_spell_power",
                            "Spellblade Spell Power", spellPowerBoost,
                            AttributeModifier.Operation.ADDITION,
                            EnchantmentModuleConfig.Spellblade.PassiveA.duration);
                }
            }
        }
    }

    private void handleSpellbladeSpellCast(Player caster) {
        int spellbladeLevel = getEnchantmentLevelFromCategory(caster, EnchantmentModuleConfig.Spellblade.category);

        if (spellbladeLevel > 0) {
            UUID casterId = caster.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "spellblade_passive_b_cooldown";
            if (EnchantmentTracker.isOnCooldown(casterId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.Spellblade.PassiveB.baseCooldown -
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.cooldownDecreasePerLevel;
            EnchantmentTracker.setCooldown(casterId, cooldownKey, currentTime + (cooldown * 50L));

            double arrowDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseArrowDamageBoost +
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.arrowBoostPerLevel;
            double attackDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseAttackDamageBoost +
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.attackBoostPerLevel;

            EnchantmentTracker.applyTimedModifier(caster, ALObjects.Attributes.ARROW_DAMAGE.get(), "spellblade_arrow_damage",
                    "Spellblade Arrow Damage", arrowDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Spellblade.PassiveB.duration);

            EnchantmentTracker.applyTimedModifier(caster, Attributes.ATTACK_DAMAGE, "spellblade_attack_damage",
                    "Spellblade Attack Damage", attackDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Spellblade.PassiveB.duration);
        }
    }
}