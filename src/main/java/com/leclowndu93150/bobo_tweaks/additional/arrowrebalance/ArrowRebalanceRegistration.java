package com.leclowndu93150.bobo_tweaks.additional.arrowrebalance;

import com.leclowndu93150.bobo_tweaks.additional.arrowrebalance.config.ArrowRebalanceConfig;
import net.minecraftforge.common.MinecraftForge;

public class ArrowRebalanceRegistration {
    
    public static void init() {
        ArrowRebalanceConfig.load();
        
        MinecraftForge.EVENT_BUS.register(ArrowRebalanceHandler.class);
    }
}
