package com.leclowndu93150.bobo_tweaks.additional.armorpreservation.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ArmorPreservationConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ArmorPreservationConfigValues VALUES;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        VALUES = new ArmorPreservationConfigValues(builder);
        SPEC = builder.build();
    }
    
    public static class ArmorPreservationConfigValues {
        public final ForgeConfigSpec.BooleanValue enabled;
        public final ForgeConfigSpec.DoubleValue damageThreshold;
        public final ForgeConfigSpec.ConfigValue<String> unusableText;
        public final ForgeConfigSpec.ConfigValue<String> textColor;
        
        ArmorPreservationConfigValues(ForgeConfigSpec.Builder builder) {
            builder.push("armor_preservation");
            
            enabled = builder
                .comment("Enable armor preservation feature - prevents armor from breaking and sends it to inventory instead (default: true)")
                .define("enabled", true);
                
            damageThreshold = builder
                .comment("Percentage of durability remaining before armor becomes unusable and goes to inventory (0.05 = 5% durability left)")
                .defineInRange("damage_threshold", 0.05, 0.01, 0.5);
                
            unusableText = builder
                .comment("Text displayed when armor cannot be equipped due to low durability")
                .define("unusable_text", "NEEDS REPAIR");
                
            textColor = builder
                .comment("Color for the unusable text tooltip",
                        "Available colors: DARK_RED, RED, GOLD, YELLOW, DARK_GREEN, GREEN,",
                        "AQUA, DARK_AQUA, DARK_BLUE, BLUE, LIGHT_PURPLE, DARK_PURPLE,",
                        "WHITE, GRAY, DARK_GRAY, BLACK")
                .define("text_color", "DARK_RED");
            
            builder.pop();
        }
    }
}