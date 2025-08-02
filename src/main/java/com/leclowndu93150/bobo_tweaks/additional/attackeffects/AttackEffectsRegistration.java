package com.leclowndu93150.bobo_tweaks.additional.attackeffects;

import com.leclowndu93150.bobo_tweaks.additional.attackeffects.config.AttackEffectsConfig;
import net.minecraftforge.common.MinecraftForge;

public class AttackEffectsRegistration {
    
    public static void init() {
        AttackEffectsConfig.load();
        
        MinecraftForge.EVENT_BUS.register(AttackEffectsHandler.class);
    }
}