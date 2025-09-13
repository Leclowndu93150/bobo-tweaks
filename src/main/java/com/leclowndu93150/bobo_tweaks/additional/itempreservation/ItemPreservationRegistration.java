package com.leclowndu93150.bobo_tweaks.additional.itempreservation;

import com.leclowndu93150.bobo_tweaks.additional.itempreservation.config.ItemPreservationConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ItemPreservationRegistration {
    
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ItemPreservationConfig.SPEC, "bobotweaks/itempreservation.toml");
        
        MinecraftForge.EVENT_BUS.register(ItemPreservationHandler.class);
    }
}