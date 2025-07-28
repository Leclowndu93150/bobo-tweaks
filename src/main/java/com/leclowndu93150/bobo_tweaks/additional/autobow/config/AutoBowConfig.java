package com.leclowndu93150.bobo_tweaks.additional.autobow.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class AutoBowConfig {
    public static final ForgeConfigSpec SPEC;
    public static final AutoBowConfigValues VALUES;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        VALUES = new AutoBowConfigValues(builder);
        SPEC = builder.build();
    }
    
    public static class AutoBowConfigValues {
        public final ForgeConfigSpec.BooleanValue autoBowEnabled;
        public final ForgeConfigSpec.IntValue crossbowChargeTime;
        
        AutoBowConfigValues(ForgeConfigSpec.Builder builder) {
            builder.push("auto_bow");
            
            autoBowEnabled = builder
                .comment("Enable auto bow/crossbow feature (default: false)")
                .define("auto_bow_enabled", false);
                
            crossbowChargeTime = builder
                .comment("Charge time for crossbow auto-reload in ticks (default: 25)")
                .defineInRange("crossbow_charge_time", 25, 10, 100);
            
            builder.pop();
        }
    }
}