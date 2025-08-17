package com.leclowndu93150.bobo_tweaks.additional.smithing;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.additional.smithing.config.SmithingBlacklistConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class SmithingBlacklistRegistration {
    
    public static void init() {
        BoboTweaks.getLogger().info("Initializing Smithing Blacklist module");
        registerConfig();
    }
    
    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.COMMON, 
            SmithingBlacklistConfig.COMMON_SPEC, 
            "bobo_tweaks-smithing_blacklist.toml"
        );
    }
}