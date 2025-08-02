package com.leclowndu93150.bobo_tweaks.additional.effectimmunity;

import com.leclowndu93150.bobo_tweaks.additional.effectimmunity.config.EffectImmunityConfig;
import net.minecraftforge.common.MinecraftForge;

public class EffectImmunityRegistration {
    
    public static void init() {
        EffectImmunityConfig.load();
        
        MinecraftForge.EVENT_BUS.register(EffectImmunityHandler.class);
    }
}