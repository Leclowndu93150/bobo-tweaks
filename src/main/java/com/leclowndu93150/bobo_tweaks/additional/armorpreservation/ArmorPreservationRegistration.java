package com.leclowndu93150.bobo_tweaks.additional.armorpreservation;

import com.leclowndu93150.bobo_tweaks.additional.armorpreservation.config.ArmorPreservationConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ArmorPreservationRegistration {
    
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArmorPreservationConfig.SPEC, "bobo_tweaks-armorpreservation.toml");
        
        MinecraftForge.EVENT_BUS.register(ArmorPreservationHandler.class);
    }
}