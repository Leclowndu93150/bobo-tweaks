package com.leclowndu93150.bobo_tweaks.additional.exclusiveitems;

import com.leclowndu93150.bobo_tweaks.additional.exclusiveitems.config.ExclusiveItemsConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ExclusiveItemsRegistration {
    
    public static void init() {
        registerConfig();
        MinecraftForge.EVENT_BUS.register(new ExclusiveItemsHandler());
    }
    
    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.COMMON, 
            ExclusiveItemsConfig.SPEC, 
            "bobotweaks/exclusive_items.toml"
        );
    }
}