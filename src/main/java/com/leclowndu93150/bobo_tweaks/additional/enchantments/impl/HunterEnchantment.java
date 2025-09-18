package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HunterEnchantment extends EventHandlingEnchantment {

    private static final Map<UUID, UUID> hunterMarks = new ConcurrentHashMap<>();

    public HunterEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Hunter.category),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 16 + (level - 1) * 8;
    }
    
    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Hunter.maxLevel;
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
        if (!EnchantmentModuleConfig.Hunter.enabled) return;

        if (event.getSource().getEntity() instanceof Player attacker) {
            LivingEntity target = event.getEntity();
            ItemStack hunterWeapon = getEnchantedItemStackFromCategory(attacker, EnchantmentModuleConfig.Hunter.category);
            handleHunterMarking(attacker, target, hunterWeapon);
            handleHunterDamageBonus(attacker, target, hunterWeapon);
        }
    }

    @Override
    public void onLivingDeath(LivingDeathEvent event) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;

        handleHunterMarkRemoval(event.getEntity());
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        handleHunterEffect(event.player);
    }

    @Override
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        hunterMarks.values().removeIf(markerId -> markerId.equals(playerId));
    }

    private void handleHunterMarking(Player attacker, LivingEntity target, ItemStack weapon) {
        int hunterLevel = EnchantmentHelper.getItemEnchantmentLevel(this, weapon);

        if (hunterLevel > 0 && attacker.hasEffect(ModPotions.HUNTER.get())) {
            UUID attackerId = attacker.getUUID();
            UUID targetId = target.getUUID();

            UUID currentMarkId = hunterMarks.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(attackerId))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            if (currentMarkId == null || currentMarkId.equals(targetId)) {
                hunterMarks.put(targetId, attackerId);
                
                int markDuration;
                if (EnchantmentModuleConfig.Hunter.markDuration < 0) {
                    markDuration = 20 * 60 * 60 * 24;
                } else {
                    markDuration = EnchantmentModuleConfig.Hunter.markDuration * 20;
                }
                target.addEffect(new MobEffectInstance(ModPotions.MARKED.get(), markDuration, 0, false, true));
            }
        }
    }

    private void handleHunterDamageBonus(Player attacker, LivingEntity target, ItemStack weapon) {
        int hunterLevel = EnchantmentHelper.getItemEnchantmentLevel(this, weapon);

        if (hunterLevel > 0 && attacker.hasEffect(ModPotions.HUNTER.get()) && 
                target.hasEffect(ModPotions.MARKED.get())) {
            
            UUID attackerId = attacker.getUUID();
            UUID markedBy = hunterMarks.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(target.getUUID()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);

            if (attackerId.equals(markedBy)) {
                double critDamageBoost = EnchantmentModuleConfig.Hunter.baseCritDamageBoost +
                        (hunterLevel - 1) * EnchantmentModuleConfig.Hunter.critDamagePerLevel;
                double critChanceBoost = EnchantmentModuleConfig.Hunter.flatCritChance;

                EnchantmentTracker.applyTimedModifier(attacker, ALObjects.Attributes.CRIT_DAMAGE.get(), "hunter_crit_damage",
                        "Hunter Crit Damage", critDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_BASE, 10);

                EnchantmentTracker.applyTimedModifier(attacker, ALObjects.Attributes.CRIT_CHANCE.get(), "hunter_crit_chance",
                        "Hunter Crit Chance", critChanceBoost / 100.0, AttributeModifier.Operation.ADDITION, 10);
            }
        }
    }

    private void handleHunterMarkRemoval(LivingEntity deadEntity) {
        UUID deadId = deadEntity.getUUID();
        hunterMarks.remove(deadId);
        deadEntity.removeEffect(ModPotions.MARKED.get());
    }

    private void handleHunterEffect(Player player) {
        int hunterLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.Hunter.category);
        
        if (hunterLevel > 0) {
            if (!player.hasEffect(ModPotions.HUNTER.get()) || 
                    (player.getEffect(ModPotions.HUNTER.get()) != null && 
                     player.getEffect(ModPotions.HUNTER.get()).getDuration() < 100)) {
                player.addEffect(new MobEffectInstance(ModPotions.HUNTER.get(), 200, 0, false, false));
            }
        } else {
            if (player.hasEffect(ModPotions.HUNTER.get())) {
                player.removeEffect(ModPotions.HUNTER.get());
                
                UUID playerId = player.getUUID();
                List<UUID> marksToRemove = hunterMarks.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(playerId))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                
                for (UUID markId : marksToRemove) {
                    hunterMarks.remove(markId);
                    for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(100))) {
                        if (entity.getUUID().equals(markId) && entity instanceof LivingEntity le) {
                            le.removeEffect(ModPotions.MARKED.get());
                            break;
                        }
                    }
                }
            }
        }
    }
}