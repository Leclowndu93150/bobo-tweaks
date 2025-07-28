package com.leclowndu93150.bobo_tweaks.additional.autobow;

import com.leclowndu93150.bobo_tweaks.additional.autobow.client.AutoBowClientHandler;
import com.leclowndu93150.bobo_tweaks.additional.autobow.config.AutoBowConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class AutoBowRegistration {
    
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AutoBowConfig.SPEC, "bobo_tweaks-autobow.toml");

        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(AutoBowClientHandler.class);
        }
    }
}