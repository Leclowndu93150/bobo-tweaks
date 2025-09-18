package com.leclowndu93150.bobo_tweaks.util;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.effect.MysticalShieldEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AttributeTickHandler {
    private static final Map<UUID, Integer> lifeDrainTimers = new HashMap<>();
    private static final Map<UUID, Integer> regenerationTimers = new HashMap<>();
    private static final Map<UUID, Integer> hyperthermiaTimers = new HashMap<>();
    private static final Map<UUID, Integer> hypothermiaTimers = new HashMap<>();
    
    private static final UUID POWER_LINK_ALCHEMICAL_UUID = UUID.fromString("c1f8b234-3e2d-4a5c-b789-123456789abc");
    private static final UUID POWER_LINK_ENDER_UUID = UUID.fromString("d2e9c345-4f3e-5b6d-c890-234567890bcd");
    private static final UUID HYPERTHERMIA_DEBUFF_UUID = UUID.fromString("f3a7b8c9-2d4e-5f6a-7b8c-9d0e1f2a3b4c");
    private static final UUID HYPOTHERMIA_DEBUFF_UUID = UUID.fromString("a1b2c3d4-5e6f-7890-abcd-ef1234567890");
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.player instanceof ServerPlayer player) {
                UUID playerId = player.getUUID();
                
                handleLifeDrain(player, playerId);
                
                handleRegeneration(player, playerId);
                
                handleHyperthermia(player, playerId);
                
                handleHypothermia(player, playerId);
                
                handlePowerLink(player);
                
                handleMysticalShields(player, playerId);
            }
        }
    }
    
    private static void handleLifeDrain(ServerPlayer player, UUID playerId) {
        AttributeInstance lifeDrainAttr = player.getAttribute(ModAttributes.LIFE_DRAIN.get());
        if (lifeDrainAttr != null && lifeDrainAttr.getValue() > 0) {
            int timer = lifeDrainTimers.getOrDefault(playerId, 0);
            timer++;
            
            int intervalTicks = (int) (ModConfig.COMMON.lifeDrainInterval.get() * 20);
            if (timer >= intervalTicks) {
                double drainPercentage = lifeDrainAttr.getValue();
                float damage = (float) (player.getMaxHealth() * drainPercentage * ModConfig.COMMON.lifeDrainPercentage.get());
                
                player.hurt(ModDamageSources.lifeDrain(player), damage);
                
                timer = 0;
            }
            
            lifeDrainTimers.put(playerId, timer);
        } else {
            lifeDrainTimers.remove(playerId);
        }
    }
    
    private static void handleRegeneration(ServerPlayer player, UUID playerId) {
        AttributeInstance regenAttr = player.getAttribute(ModAttributes.REGENERATION.get());
        if (regenAttr != null && regenAttr.getValue() > 0) {
            int timer = regenerationTimers.getOrDefault(playerId, 0);
            timer++;
            
            int intervalTicks = (int) (ModConfig.COMMON.regenInterval.get() * 20);
            if (timer >= intervalTicks) {
                double regenLevel = regenAttr.getValue();
                float healAmount = (float) (regenLevel * ModConfig.COMMON.regenerationHealAmount.get());
                
                player.heal(healAmount);
                
                timer = 0;
            }
            
            regenerationTimers.put(playerId, timer);
        } else {
            regenerationTimers.remove(playerId);
        }
    }
    
    private static void handleHyperthermia(ServerPlayer player, UUID playerId) {
        AttributeInstance hyperthermiAttr = player.getAttribute(ModAttributes.HYPERTHERMIA.get());
        AttributeInstance heatResistAttr = player.getAttribute(ModAttributes.HEAT_RESISTANCE.get());
        
        if (hyperthermiAttr != null && heatResistAttr != null) {
            double hyperthermia = hyperthermiAttr.getValue();
            double heatResistance = heatResistAttr.getValue();
            double effectiveHyperthermia = Math.max(0, hyperthermia - heatResistance);
            
            if (effectiveHyperthermia > 0) {
                int timer = hyperthermiaTimers.getOrDefault(playerId, 0);
                timer++;
                
                int intervalTicks = (int) (ModConfig.COMMON.temperatureInterval.get() * 20);
                if (timer >= intervalTicks) {
                    float damage = (float) (player.getMaxHealth() * effectiveHyperthermia);
                    
                    player.hurt(ModDamageSources.hyperthermia(player), damage);
                    
                    timer = 0;
                }
                
                hyperthermiaTimers.put(playerId, timer);
            }
            
            // Apply damage amplifier reduction if hyperthermia > heat resistance
            if (hyperthermia > heatResistance) {
                applyHyperthermiaDebuff(player);
            } else {
                removeHyperthermiaDebuff(player);
            }
        } else {
            hyperthermiaTimers.remove(playerId);
            removeHyperthermiaDebuff(player);
        }
    }
    
    private static void handleHypothermia(ServerPlayer player, UUID playerId) {
        AttributeInstance hypothermiaAttr = player.getAttribute(ModAttributes.HYPOTHERMIA.get());
        AttributeInstance coldResistAttr = player.getAttribute(ModAttributes.COLD_RESISTANCE.get());
        
        if (hypothermiaAttr != null && coldResistAttr != null) {
            double hypothermia = hypothermiaAttr.getValue();
            double coldResistance = coldResistAttr.getValue();
            double effectiveHypothermia = Math.max(0, hypothermia - coldResistance);
            
            if (effectiveHypothermia > 0) {
                int timer = hypothermiaTimers.getOrDefault(playerId, 0);
                timer++;
                
                int intervalTicks = (int) (ModConfig.COMMON.temperatureInterval.get() * 20);
                if (timer >= intervalTicks) {
                    float damage = (float) (player.getMaxHealth() * effectiveHypothermia);
                    
                    player.hurt(ModDamageSources.hypothermia(player), damage);
                    
                    timer = 0;
                }
                
                hypothermiaTimers.put(playerId, timer);
            }
            
            // Apply crit damage and crit rate reduction if hypothermia > cold resistance
            if (hypothermia > coldResistance) {
                applyHypothermiaDebuff(player);
            } else {
                removeHypothermiaDebuff(player);
            }
        } else {
            hypothermiaTimers.remove(playerId);
            removeHypothermiaDebuff(player);
        }
    }
    
    private static void handlePowerLink(ServerPlayer player) {
        AttributeInstance powerLinkAttr = player.getAttribute(ModAttributes.POWER_LINK.get());
        AttributeInstance damageAmpAttr = player.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        AttributeInstance alchemicalBoostAttr = player.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
        
        if (powerLinkAttr != null && damageAmpAttr != null && alchemicalBoostAttr != null) {
            double powerLink = powerLinkAttr.getValue();
            
            if (powerLink > 0) {
                double damageAmplifier = damageAmpAttr.getValue();
                double conversionAmount = Math.max(0, (damageAmplifier - 1) * powerLink);
                
                AttributeModifier alchemicalModifier = new AttributeModifier(
                    POWER_LINK_ALCHEMICAL_UUID,
                    "Power Link Alchemical Conversion",
                    conversionAmount,
                    AttributeModifier.Operation.ADDITION
                );
                
                alchemicalBoostAttr.removeModifier(alchemicalModifier);
                alchemicalBoostAttr.addTransientModifier(alchemicalModifier);
                
                if (ModList.get().isLoaded("irons_spellbooks")) {
                    try {
                        var enderSpellPowerAttr = player.getAttribute(
                                Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(
                                        new ResourceLocation("irons_spellbooks", "ender_spell_power")
                                ))
                        );
                        
                        if (enderSpellPowerAttr != null) {
                            AttributeModifier enderModifier = new AttributeModifier(
                                POWER_LINK_ENDER_UUID,
                                "Power Link Ender Conversion",
                                conversionAmount,
                                AttributeModifier.Operation.ADDITION
                            );
                            
                            enderSpellPowerAttr.removeModifier(enderModifier);
                            enderSpellPowerAttr.addTransientModifier(enderModifier);
                        }
                    } catch (Exception e) {
                    }
                }
            } else {
                alchemicalBoostAttr.removeModifier(POWER_LINK_ALCHEMICAL_UUID);
                
                if (ModList.get().isLoaded("irons_spellbooks")) {
                    try {
                        var enderSpellPowerAttr = player.getAttribute(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("irons_spellbooks", "ender_spell_power")));
                        
                        if (enderSpellPowerAttr != null) {
                            enderSpellPowerAttr.removeModifier(POWER_LINK_ENDER_UUID);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    
    private static void handleMysticalShields(ServerPlayer player, UUID playerId) {
        Map<UUID, Long> shieldExpiryTimes = MysticalShieldEffect.getShieldExpiryTimes();
        Long expiryTime = shieldExpiryTimes.get(playerId);
        
        if (expiryTime != null && player.level().getGameTime() >= expiryTime) {
            shieldExpiryTimes.remove(playerId);
            player.setAbsorptionAmount(0);
        }
    }
    
    private static void applyHyperthermiaDebuff(ServerPlayer player) {
        AttributeInstance damageAmpInstance = player.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            double debuffAmount = -ModConfig.COMMON.hyperthermiaDebuff.get();
            AttributeModifier modifier = new AttributeModifier(HYPERTHERMIA_DEBUFF_UUID,
                "Hyperthermia damage reduction", debuffAmount, AttributeModifier.Operation.MULTIPLY_TOTAL);
            
            damageAmpInstance.removeModifier(modifier);
            damageAmpInstance.addTransientModifier(modifier);
        }
    }
    
    private static void removeHyperthermiaDebuff(ServerPlayer player) {
        AttributeInstance damageAmpInstance = player.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAmpInstance != null) {
            damageAmpInstance.removeModifier(HYPERTHERMIA_DEBUFF_UUID);
        }
    }
    
    private static void applyHypothermiaDebuff(ServerPlayer player) {
        if (ModList.get().isLoaded("attributeslib")) {
            try {
                // Apply crit rate reduction
                var critRateAttr = player.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("attributeslib", "crit_chance")))
                );
                
                if (critRateAttr != null) {
                    double debuffAmount = -ModConfig.COMMON.hypothermiaDebuff.get();
                    AttributeModifier modifier = new AttributeModifier(HYPOTHERMIA_DEBUFF_UUID,
                        "Hypothermia crit rate reduction", debuffAmount, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    critRateAttr.removeModifier(modifier);
                    critRateAttr.addTransientModifier(modifier);
                }
                
                // Apply crit damage reduction
                var critDamageAttr = player.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("attributeslib", "crit_damage")))
                );
                
                if (critDamageAttr != null) {
                    double debuffAmount = -ModConfig.COMMON.hypothermiaDebuff.get();
                    AttributeModifier modifier = new AttributeModifier(UUID.fromString("b1c2d3e4-6f78-90ab-cdef-123456789abc"),
                        "Hypothermia crit damage reduction", debuffAmount, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    critDamageAttr.removeModifier(modifier);
                    critDamageAttr.addTransientModifier(modifier);
                }
            } catch (Exception e) {
                // Silent catch if attributes don't exist
            }
        }
    }
    
    private static void removeHypothermiaDebuff(ServerPlayer player) {
        if (ModList.get().isLoaded("attributeslib")) {
            try {
                // Remove crit rate reduction
                var critRateAttr = player.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("attributeslib", "crit_chance")))
                );
                
                if (critRateAttr != null) {
                    critRateAttr.removeModifier(HYPOTHERMIA_DEBUFF_UUID);
                }
                
                // Remove crit damage reduction
                var critDamageAttr = player.getAttribute(
                        Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("attributeslib", "crit_damage")))
                );
                
                if (critDamageAttr != null) {
                    critDamageAttr.removeModifier(UUID.fromString("b1c2d3e4-6f78-90ab-cdef-123456789abc"));
                }
            } catch (Exception e) {
                // Silent catch if attributes don't exist
            }
        }
    }
}