package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SaintsPledgeEnchantment extends EventHandlingEnchantment {
    
    public SaintsPledgeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.SaintsPledge.category),
                new EquipmentSlot[]{EquipmentSlot.OFFHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + level * 8;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }
    
    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.SaintsPledge.maxLevel;
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

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.SaintsPledge.enabled) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        handleSaintsPledgeCrouch(event.player);
    }

    private void handleSaintsPledgeCrouch(Player player) {
        int saintsLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.SaintsPledge.category);
        
        if (saintsLevel > 0) {
            UUID playerId = player.getUUID();
            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(playerId);
            
            boolean isCrouchingWithShield = player.isCrouching() && player.isBlocking();
            
            if (isCrouchingWithShield) {
                long crouchStartTime = data.getLong("saints_crouch_start");
                if (crouchStartTime == 0) {
                    data.putLong("saints_crouch_start", System.currentTimeMillis());
                } else {
                    long crouchDuration = System.currentTimeMillis() - crouchStartTime;
                    if (crouchDuration >= EnchantmentModuleConfig.SaintsPledge.crouchTime * 50L) {
                        String cooldownKey = "saints_pledge_cooldown";
                        long currentTime = System.currentTimeMillis();
                        if (!EnchantmentTracker.isOnCooldown(playerId, cooldownKey, currentTime)) {
                            if (player.level() instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 3; i++) {
                                    ModNetworking.playSound(serverLevel, player.getX(), player.getY(), player.getZ(),
                                            SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F + (i * 0.1F));
                                }
                            }
                            
                            float healthDrain = (float)(player.getMaxHealth() * EnchantmentModuleConfig.SaintsPledge.maxHealthDrained);
                            player.hurt(player.damageSources().magic(), healthDrain);
                            
                            double healScale = EnchantmentModuleConfig.SaintsPledge.baseHealScale +
                                    (saintsLevel - 1) * EnchantmentModuleConfig.SaintsPledge.healScalePerLevel;
                            float healAmount = healthDrain * (float)healScale;
                            
                            Team playerTeam = player.getTeam();
                            if (playerTeam != null) {
                                List<Player> teammates = player.level().getEntitiesOfClass(Player.class,
                                        player.getBoundingBox().inflate(50)).stream()
                                        .filter(p -> p.getTeam() != null && p.getTeam().equals(playerTeam) && !p.equals(player))
                                        .collect(Collectors.toList());
                                
                                if (!teammates.isEmpty()) {
                                    float healPerTeammate = healAmount / teammates.size();
                                    for (Player teammate : teammates) {
                                        teammate.heal(healPerTeammate);
                                    }
                                }
                            }
                            
                            double lifestealAmount = EnchantmentModuleConfig.SaintsPledge.baseLifesteal +
                                    (saintsLevel - 1) * EnchantmentModuleConfig.SaintsPledge.lifestealPerLevel;
                            double lifestealCap = saintsLevel * EnchantmentModuleConfig.SaintsPledge.lifestealCapPerLevel;
                            lifestealAmount = Math.min(lifestealAmount, lifestealCap);
                            
                            EnchantmentTracker.applyTimedModifier(player, ALObjects.Attributes.LIFE_STEAL.get(), 
                                    "saints_pledge_lifesteal", "Saints Pledge Lifesteal", 
                                    lifestealAmount / 100.0, AttributeModifier.Operation.ADDITION,
                                    EnchantmentModuleConfig.SaintsPledge.duration);
                            
                            EnchantmentTracker.applyTimedModifier(player, ModAttributes.SPELL_LEECH.get(), 
                                    "saints_pledge_spell_leech", "Saints Pledge Spell Leech", 
                                    lifestealAmount / 100.0, AttributeModifier.Operation.ADDITION,
                                    EnchantmentModuleConfig.SaintsPledge.duration);
                            
                            EnchantmentTracker.setCooldown(playerId, cooldownKey, currentTime + (EnchantmentModuleConfig.SaintsPledge.duration * 50L * 2));
                            data.remove("saints_crouch_start");
                        }
                    }
                }
            } else {
                data.remove("saints_crouch_start");
            }
        }
    }
}