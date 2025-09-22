package com.leclowndu93150.bobo_tweaks.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;
    
    public static final CommonConfig COMMON;
    public static final ClientConfig CLIENT;
    
    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new CommonConfig(commonBuilder);
        COMMON_SPEC = commonBuilder.build();
        
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT = new ClientConfig(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.DoubleValue lifeDrainInterval;
        public final ForgeConfigSpec.DoubleValue lifeDrainPercentage;
        public final ForgeConfigSpec.DoubleValue regenInterval;
        public final ForgeConfigSpec.DoubleValue regenerationHealAmount;
        public final ForgeConfigSpec.DoubleValue temperatureInterval;
        public final ForgeConfigSpec.DoubleValue hyperthermiaDebuff;
        public final ForgeConfigSpec.DoubleValue hypothermiaDebuff;
        public final ForgeConfigSpec.DoubleValue guardPointPercentage;
        public final ForgeConfigSpec.DoubleValue wardenBasePercentage;
        public final ForgeConfigSpec.DoubleValue wardenScalingFactor;
        public final ForgeConfigSpec.DoubleValue mysticalManaPercentage;
        public final ForgeConfigSpec.DoubleValue mysticalBaseShield;
        public final ForgeConfigSpec.DoubleValue mysticalScalingFactor;
        public final ForgeConfigSpec.DoubleValue mysticalShieldDuration;
        public final ForgeConfigSpec.DoubleValue powerUpBasePercentage;
        public final ForgeConfigSpec.DoubleValue powerUpScalingFactor;
        public final ForgeConfigSpec.DoubleValue restoreFlatHeal;
        public final ForgeConfigSpec.DoubleValue restoreScalingFactor;
        public final ForgeConfigSpec.DoubleValue regenerationFlatHeal;
        public final ForgeConfigSpec.DoubleValue regenerationScalingFactor;
        public final ForgeConfigSpec.DoubleValue regenerationCritDamageBoost;
        public final ForgeConfigSpec.DoubleValue shieldingFlatShield;
        public final ForgeConfigSpec.DoubleValue shieldingScalingFactor;
        public final ForgeConfigSpec.DoubleValue speedMovementBoost;
        public final ForgeConfigSpec.DoubleValue speedAttackSpeedBoost;
        public final ForgeConfigSpec.DoubleValue speedAttackSpeedScaling;
        public final ForgeConfigSpec.DoubleValue conduitSharePercentage;
        public final ForgeConfigSpec.DoubleValue airJumpVelocity;
        public final ForgeConfigSpec.DoubleValue lifeLeechSpellMultiplier;
        
        // Fury Effect
        public final ForgeConfigSpec.DoubleValue furyHealthDrainPercentage;
        public final ForgeConfigSpec.DoubleValue furyFireDamageMultiplier;
        public final ForgeConfigSpec.DoubleValue furyAttackScaling;
        
        // Rebuke Effect
        public final ForgeConfigSpec.DoubleValue rebukeBaseDamage;
        public final ForgeConfigSpec.DoubleValue rebukeMaxHpPercentage;
        
        // Status Offline Effect
        public final ForgeConfigSpec.DoubleValue statusOfflineBaseDamage;
        public final ForgeConfigSpec.DoubleValue statusOfflineStatusBonus;
        
        // Rejuvenating Shots Effect
        public final ForgeConfigSpec.DoubleValue rejuvenatingShotsFlatHeal;
        public final ForgeConfigSpec.DoubleValue rejuvenatingShotsManaPercentage;
        public final ForgeConfigSpec.DoubleValue rejuvenatingShotsManaCost;
        public final ForgeConfigSpec.DoubleValue rejuvenatingShotsHealCap;
        
        // Alchemical Leeching Effect
        public final ForgeConfigSpec.DoubleValue alchemicalLeechingBase;
        public final ForgeConfigSpec.DoubleValue alchemicalLeechingScaling;
        public final ForgeConfigSpec.DoubleValue alchemicalLifestealScaling;
        
        // Mana Drain Effect
        public final ForgeConfigSpec.DoubleValue manaDrainAmount;
        
        // Woodward Sentinel Effect
        public final ForgeConfigSpec.DoubleValue woodwardSentinelHealingReduction;
        public final ForgeConfigSpec.DoubleValue woodwardSentinelDistributedHealingMultiplier;
        public final ForgeConfigSpec.DoubleValue woodwardSentinelHealCap;

        // Penumbral Mark Effect
        public final ForgeConfigSpec.DoubleValue penumbralMarkDamageIncreaseBase;
        public final ForgeConfigSpec.DoubleValue penumbralMarkDamageIncreasePerLevel;
        public final ForgeConfigSpec.DoubleValue penumbralMarkFlatHealPerLevel;
        public final ForgeConfigSpec.DoubleValue penumbralMarkAlchemicalBoostScaleFactorPerLevel;

        CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("life_drain");
            lifeDrainInterval = builder
                .comment("Time interval in seconds between life drain damage ticks")
                .defineInRange("interval", 6.0, 0.1, 600.0);
            lifeDrainPercentage = builder
                .comment("Percentage of max health drained per tick (0.06 = 6%)")
                .defineInRange("percentage", 0.06, 0.001, 10.0);
            builder.pop();
            
            builder.push("regeneration");
            regenInterval = builder
                .comment("Time interval in seconds between regeneration heal ticks")
                .defineInRange("interval", 5.0, 0.1, 600.0);
            regenerationHealAmount = builder
                .comment("Amount of health healed per regeneration level per tick (1.0 = 0.5 hearts)")
                .defineInRange("heal_amount", 1.0, 0.0, 1000.0);
            builder.pop();
            
            builder.push("temperature");
            temperatureInterval = builder
                .comment("Time interval in seconds between temperature damage ticks")
                .defineInRange("interval", 3.0, 1.0, 60.0);
            hyperthermiaDebuff = builder
                .comment("Damage amplifier reduction when hyperthermia > heat resistance (0.25 = 25% reduction)")
                .defineInRange("hyperthermia_damage_debuff", 0.25, 0.0, 1.0);
            hypothermiaDebuff = builder
                .comment("Critical hit reduction when hypothermia > cold resistance (0.25 = 25% reduction)")
                .defineInRange("hypothermia_crit_debuff", 0.25, 0.0, 1.0);
            builder.pop();
            
            builder.push("guard_point");
            guardPointPercentage = builder
                .comment("Percentage of max health converted to absorption per level (0.2 = 20%)")
                .defineInRange("absorption_percentage", 0.2, 0.001, 100.0);
            builder.pop();
            
            builder.push("the_warden");
            wardenBasePercentage = builder
                .comment("Base percentage of max health converted to absorption (0.25 = 25%)")
                .defineInRange("base_absorption_percentage", 0.25, 0.1, 2.0);
            wardenScalingFactor = builder
                .comment("Additional absorption per point of damage amplifier (5.0 = 5 absorption per point)")
                .defineInRange("scaling_factor", 5.0, 1.0, 200.0);
            builder.pop();
            
            builder.push("mystical_shield");
            mysticalManaPercentage = builder
                .comment("Percentage of current mana consumed per level (0.2 = 20%)")
                .defineInRange("mana_percentage", 0.2, 0.01, 1.0);
            mysticalBaseShield = builder
                .comment("Base shield amount granted (independent of level)")
                .defineInRange("base_shield", 10.0, 1.0, 50.0);
            mysticalScalingFactor = builder
                .comment("Additional shield per point of damage amplifier per level")
                .defineInRange("scaling_factor", 5.0, 1.0, 20.0);
            mysticalShieldDuration = builder
                .comment("Duration in seconds for the mystical shield absorption (independent of effect duration)")
                .defineInRange("shield_duration", 20.0, 5.0, 120.0);
            builder.pop();
            
            builder.push("alchemical_power_up");
            powerUpBasePercentage = builder
                .comment("Base damage amplifier boost per level (0.1 = 10%)")
                .defineInRange("base_percentage", 0.1, 0.01, 1.0);
            powerUpScalingFactor = builder
                .comment("Additional damage boost per alchemical boost point per level (0.05 = 5%)")
                .defineInRange("scaling_factor", 0.05, 0.01, 0.5);
            builder.pop();
            
            builder.push("alchemical_restore");
            restoreFlatHeal = builder
                .comment("Base heal amount per level (4.0 = 2 hearts)")
                .defineInRange("flat_heal", 4.0, 1.0, 20.0);
            restoreScalingFactor = builder
                .comment("Additional heal per alchemical boost point per level")
                .defineInRange("scaling_factor", 2.0, 0.5, 10.0);
            builder.pop();
            
            builder.push("alchemical_regeneration");
            regenerationFlatHeal = builder
                .comment("Base heal amount per level every 0.4s (2.0 = 1 heart)")
                .defineInRange("flat_heal", 2.0, 0.5, 10.0);
            regenerationScalingFactor = builder
                .comment("Additional heal per alchemical boost point per level every 0.4s")
                .defineInRange("scaling_factor", 1.0, 0.25, 5.0);
            regenerationCritDamageBoost = builder
                .comment("Flat critical damage boost while effect is active (does not scale with level)")
                .defineInRange("crit_damage_boost", 0.25, 0.1, 1.0);
            builder.pop();
            
            builder.push("alchemical_shielding");
            shieldingFlatShield = builder
                .comment("Base shield amount per level (8.0 = 4 hearts)")
                .defineInRange("flat_shield", 8.0, 1.0, 40.0);
            shieldingScalingFactor = builder
                .comment("Additional shield per alchemical boost point per level")
                .defineInRange("scaling_factor", 4.0, 1.0, 20.0);
            builder.pop();
            
            builder.push("alchemical_speed");
            speedMovementBoost = builder
                .comment("Movement speed boost (0.2 = 20%, does not scale with level)")
                .defineInRange("movement_boost", 0.2, 0.1, 1.0);
            speedAttackSpeedBoost = builder
                .comment("Base attack speed boost per level (0.1 = 10%)")
                .defineInRange("attack_speed_boost", 0.1, 0.01, 1.0);
            speedAttackSpeedScaling = builder
                .comment("Additional attack speed per alchemical boost point per level (0.05 = 5%)")
                .defineInRange("attack_speed_scaling", 0.05, 0.01, 0.5);
            builder.pop();
            
            builder.push("conduit");
            conduitSharePercentage = builder
                .comment("Percentage of alchemical boost shared with team per level (0.5 = 50%)")
                .defineInRange("share_percentage", 0.5, 0.1, 1.0);
            builder.pop();
            
            builder.push("air_jumps");
            airJumpVelocity = builder
                .comment("Velocity for air jumps (0.55 = stronger than normal, 0.42 = normal jump strength)")
                .defineInRange("air_jump_velocity", 0.55, 0.3, 1.0);
            builder.pop();
            
            builder.push("life_leech");
            lifeLeechSpellMultiplier = builder
                .comment("Multiplier for life leech when using spell attacks (0.5 = 50% effectiveness)")
                .defineInRange("spell_multiplier", 0.5, 0.0, 100.0);
            builder.pop();
            
            builder.push("fury_effect");
            furyHealthDrainPercentage = builder
                .comment("Percentage of current health drained per level (0.1 = 10%)")
                .defineInRange("health_drain_percentage", 0.1, 0.001, 10.0);
            furyFireDamageMultiplier = builder
                .comment("Percentage of lost HP added to fire damage (0.5 = 50%)")
                .defineInRange("fire_damage_multiplier", 0.5, 0.01, 100.0);
            furyAttackScaling = builder
                .comment("Scaling factor for number of attacks (0.5 = 0.5 attacks per damage amplifier)")
                .defineInRange("attack_scaling", 0.5, 0.01, 100.0);
            builder.pop();
            
            builder.push("rebuke_effect");
            rebukeBaseDamage = builder
                .comment("Base flat damage dealt to attacker per level")
                .defineInRange("base_damage", 5.0, 0.1, 10000.0);
            rebukeMaxHpPercentage = builder
                .comment("Percentage of attacker's max HP dealt as damage per level (0.1 = 10%)")
                .defineInRange("max_hp_percentage", 0.1, 0.001, 100.0);
            builder.pop();
            
            builder.push("status_offline_effect");
            statusOfflineBaseDamage = builder
                .comment("Base magic damage per level")
                .defineInRange("base_damage", 10.0, 1.0, 50.0);
            statusOfflineStatusBonus = builder
                .comment("Additional damage per negative status cleared")
                .defineInRange("status_bonus", 5.0, 1.0, 20.0);
            builder.pop();
            
            builder.push("rejuvenating_shots_effect");
            rejuvenatingShotsFlatHeal = builder
                .comment("Base heal amount per level (multiplied by crit rate)")
                .defineInRange("flat_heal", 5.0, 0.001, 50.0);
            rejuvenatingShotsManaPercentage = builder
                .comment("Percentage of max mana added to healing per level (0.01 = 1%)")
                .defineInRange("mana_percentage", 0.01, 0.001, 0.1);
            rejuvenatingShotsManaCost = builder
                .comment("Flat mana cost per shot per level")
                .defineInRange("mana_cost", 10.0, 1.0, 100.0);
            rejuvenatingShotsHealCap = builder
                .comment("Maximum healing per shot per level (0 = no cap)")
                .defineInRange("heal_cap", 50.0, 0.0, 200.0);
            builder.pop();
            
            builder.push("alchemical_leeching_effect");
            alchemicalLeechingBase = builder
                .comment("Base spell leech and lifesteal percentage per level (0.05 = 5%)")
                .defineInRange("base_leech", 0.05, 0.01, 0.5);
            alchemicalLeechingScaling = builder
                .comment("Additional spell leech per alchemical boost point per level (0.01 = 1%)")
                .defineInRange("spell_leech_scaling", 0.01, 0.001, 0.1);
            alchemicalLifestealScaling = builder
                .comment("Additional lifesteal per alchemical boost point per level (0.01 = 1%)")
                .defineInRange("lifesteal_scaling", 0.01, 0.001, 0.1);
            builder.pop();
            
            builder.push("mana_drain_effect");
            manaDrainAmount = builder
                .comment("Amount of mana drained per level")
                .defineInRange("mana_drain_amount", 10.0, 1.0, 1000.0);
            builder.pop();
            
            builder.push("woodward_sentinel_effect");
            woodwardSentinelHealingReduction = builder
                .comment("Percentage of healing reduced per level (0.5 = 50%)")
                .defineInRange("healing_reduction", 0.5, 0.1, 1.0);
            woodwardSentinelDistributedHealingMultiplier = builder
                .comment("Multiplier for distributed healing per level (2.5 = 2.5x multiplier)")
                .defineInRange("distributed_healing_multiplier", 2.5, 1.0, 10.0);
            woodwardSentinelHealCap = builder
                .comment("Maximum healing that can be distributed per teammate (0 = no cap)")
                .defineInRange("heal_cap", 0.0, 0.0, 100.0);
            builder.pop();

            builder.push("penumbral_mark_effect");
            penumbralMarkDamageIncreaseBase = builder
                .comment("Base percentage damage increase for the mark (0.1 = 10%)")
                .defineInRange("damage_increase_base", 0.1, 0.0, 10.0);
            penumbralMarkDamageIncreasePerLevel = builder
                .comment("Additional percentage damage increase per level of the effect (0.05 = 5%)")
                .defineInRange("damage_increase_per_level", 0.05, 0.0, 10.0);
            penumbralMarkFlatHealPerLevel = builder
                .comment("Flat amount the attacker heals per level of the effect")
                .defineInRange("flat_heal_per_level", 2.0, 0.0, 100.0);
            penumbralMarkAlchemicalBoostScaleFactorPerLevel = builder
                .comment("Additional healing per point of the attacker's Alchemical Boost attribute, per level of the effect")
                .defineInRange("alchemical_boost_scale_factor_per_level", 0.5, 0.0, 50.0);
            builder.pop();
        }
    }
    
    public static class ClientConfig {
        public final ForgeConfigSpec.BooleanValue showAttributeTooltips;
        public final ForgeConfigSpec.BooleanValue showJumpCounter;
        
        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("display");
            showAttributeTooltips = builder
                .comment("Show detailed tooltips for custom attributes on items")
                .define("show_attribute_tooltips", true);
            showJumpCounter = builder
                .comment("Display remaining air jumps on screen")
                .define("show_jump_counter", true);
            builder.pop();
        }
    }
}