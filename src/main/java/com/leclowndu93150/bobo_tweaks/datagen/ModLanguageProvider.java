package com.leclowndu93150.bobo_tweaks.datagen;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModCreativeTabs;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, BoboTweaks.MODID, locale);
    }
    
    @Override
    protected void addTranslations() {
        // Add mod name
        add("mod." + BoboTweaks.MODID + ".name", "Bobo Tweaks");

        add("itemGroup." + BoboTweaks.MODID + ".bobo_tab", "Bobo Tweaks");

        addConfig();

        addPotionEffects();

        addAttributes();
    }
    
    private void addConfig() {
        // Life Drain
        add("config.bobo_tweaks.life_drain", "Life Drain");
        add("config.bobo_tweaks.life_drain.interval", "Damage Interval (seconds)");
        add("config.bobo_tweaks.life_drain.percentage", "Damage Percentage per Tick");
        
        // Regeneration
        add("config.bobo_tweaks.regeneration", "Regeneration");
        add("config.bobo_tweaks.regeneration.interval", "Heal Interval (seconds)");
        add("config.bobo_tweaks.regeneration.percentage", "Percentage of Max Health Healed per Level per Tick");
        
        // Temperature
        add("config.bobo_tweaks.temperature", "Temperature Effects");
        add("config.bobo_tweaks.temperature.interval", "Damage Interval (seconds)");
        add("config.bobo_tweaks.temperature.hyperthermia_damage_debuff", "Hyperthermia Damage Debuff");
        add("config.bobo_tweaks.temperature.hypothermia_crit_debuff", "Hypothermia Crit Debuff");
        
        // Guard Point
        add("config.bobo_tweaks.guard_point", "Guard Point Effect");
        add("config.bobo_tweaks.guard_point.absorption_percentage", "Absorption Percentage per Level");
        
        // The Warden
        add("config.bobo_tweaks.the_warden", "The Warden Effect");
        add("config.bobo_tweaks.the_warden.base_absorption_percentage", "Base Absorption Percentage");
        add("config.bobo_tweaks.the_warden.scaling_factor", "Damage Amplifier Scaling Factor");
        
        // Mystical Shield
        add("config.bobo_tweaks.mystical_shield", "Mystical Shield Effect");
        add("config.bobo_tweaks.mystical_shield.mana_percentage", "Mana Drain Percentage");
        add("config.bobo_tweaks.mystical_shield.base_shield", "Base Shield Amount");
        add("config.bobo_tweaks.mystical_shield.scaling_factor", "Damage Amplifier Scaling");
        
        // Alchemical Effects
        add("config.bobo_tweaks.alchemical_power_up", "Alchemical Power Up");
        add("config.bobo_tweaks.alchemical_power_up.base_percentage", "Base Damage Boost");
        add("config.bobo_tweaks.alchemical_power_up.scaling_factor", "Alchemical Boost Scaling");
        
        add("config.bobo_tweaks.alchemical_restore", "Alchemical Restore");
        add("config.bobo_tweaks.alchemical_restore.flat_heal", "Base Heal Amount");
        add("config.bobo_tweaks.alchemical_restore.scaling_factor", "Alchemical Boost Scaling");
        
        add("config.bobo_tweaks.alchemical_regeneration", "Alchemical Regeneration");
        add("config.bobo_tweaks.alchemical_regeneration.flat_heal", "Base Heal Amount per Level every 0.4s");
        add("config.bobo_tweaks.alchemical_regeneration.scaling_factor", "Alchemical Boost Scaling per Level every 0.4s");
        add("config.bobo_tweaks.alchemical_regeneration.crit_damage_boost", "Flat Critical Damage Boost while Active");
        
        add("config.bobo_tweaks.alchemical_shielding", "Alchemical Shielding");
        add("config.bobo_tweaks.alchemical_shielding.flat_shield", "Base Shield Amount");
        add("config.bobo_tweaks.alchemical_shielding.scaling_factor", "Alchemical Boost Scaling");
        
        add("config.bobo_tweaks.alchemical_speed", "Alchemical Speed");
        add("config.bobo_tweaks.alchemical_speed.movement_boost", "Movement Speed Boost");
        add("config.bobo_tweaks.alchemical_speed.attack_speed_boost", "Base Attack Speed Boost");
        add("config.bobo_tweaks.alchemical_speed.attack_speed_scaling", "Attack Speed Scaling");
        
        // Conduit
        add("config.bobo_tweaks.conduit", "Conduit Effect");
        add("config.bobo_tweaks.conduit.share_percentage", "Share Percentage per Level");
        
        // Client Config
        add("config.bobo_tweaks.display", "Display Settings");
        add("config.bobo_tweaks.display.show_attribute_tooltips", "Show Attribute Tooltips");
        add("config.bobo_tweaks.display.show_jump_counter", "Show Jump Counter");
    }
    
    private void addPotionEffects() {
        // Shield Effects
        add(ModPotions.GUARD_POINT.get(), "Guard Point");
        add(ModPotions.THE_WARDEN.get(), "The Warden");
        
        // Alchemical Protocol Effects
        add(ModPotions.ALCHEMICAL_POWER_UP.get(), "Alchemical Protocol: Power Up");
        add(ModPotions.ALCHEMICAL_RESTORE.get(), "Alchemical Protocol: Restore");
        add(ModPotions.ALCHEMICAL_SHIELDING.get(), "Alchemical Protocol: Shielding");
        add(ModPotions.ALCHEMICAL_SPEED.get(), "Alchemical Protocol: Speed");
        
        // Team Effects
        add(ModPotions.CONDUIT.get(), "Conduit");
        
        // Special Effects
        add(ModPotions.MYSTICAL_SHIELD.get(), "Mystical Shield");
    }
    
    private void addAttributes() {
        // Combat Attributes
        add(ModAttributes.DAMAGE_AMPLIFIER.get().getDescriptionId(), "Damage Amplifier");
        
        // Health & Regen Attributes
        add(ModAttributes.LIFE_DRAIN.get().getDescriptionId(), "Life Drain");
        add(ModAttributes.REGENERATION.get().getDescriptionId(), "Regeneration");
        
        // Temperature Attributes
        add(ModAttributes.HYPERTHERMIA.get().getDescriptionId(), "Hyperthermia");
        add(ModAttributes.HEAT_RESISTANCE.get().getDescriptionId(), "Heat Resistance");
        add(ModAttributes.HYPOTHERMIA.get().getDescriptionId(), "Hypothermia");
        add(ModAttributes.COLD_RESISTANCE.get().getDescriptionId(), "Cold Resistance");
        
        // Alchemical Attributes
        add(ModAttributes.ALCHEMICAL_BOOST.get().getDescriptionId(), "Alchemical Boost");
        add(ModAttributes.POWER_LINK.get().getDescriptionId(), "Power Link");
        
        // Movement Attributes
        add(ModAttributes.JUMP_COUNT.get().getDescriptionId(), "Jump Count");
        add("tooltip.bobo_tweaks.jump_count", "Allows %s air jumps");
        
        // Damage Types
        add("death.attack.life_drain", "%1$s withered away");
        add("death.attack.hyperthermia", "%1$s succumbed to extreme heat");
        add("death.attack.hypothermia", "%1$s froze to death");
    }
}