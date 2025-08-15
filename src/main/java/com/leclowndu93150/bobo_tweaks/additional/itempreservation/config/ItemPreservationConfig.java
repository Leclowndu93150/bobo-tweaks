package com.leclowndu93150.bobo_tweaks.additional.itempreservation.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public class ItemPreservationConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ItemPreservationConfigValues VALUES;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        VALUES = new ItemPreservationConfigValues(builder);
        SPEC = builder.build();
    }
    
    public static class ItemPreservationConfigValues {
        public final ForgeConfigSpec.BooleanValue enabled;
        public final ForgeConfigSpec.DoubleValue damageThreshold;
        public final ForgeConfigSpec.ConfigValue<String> unusableText;
        public final ForgeConfigSpec.ConfigValue<String> textColor;
        public final ForgeConfigSpec.BooleanValue preserveArmor;
        public final ForgeConfigSpec.BooleanValue preserveTools;
        public final ForgeConfigSpec.BooleanValue preventToolUse;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalTools;
        public final ForgeConfigSpec.BooleanValue useVanillaTags;
        public final ForgeConfigSpec.BooleanValue useForgeTags;
        
        ItemPreservationConfigValues(ForgeConfigSpec.Builder builder) {
            builder.push("item_preservation");
            
            enabled = builder
                .comment("Enable item preservation feature - prevents items from breaking (default: true)")
                .define("enabled", true);
                
            damageThreshold = builder
                .comment("Percentage of durability remaining before item becomes unusable (0.05 = 5% durability left)")
                .defineInRange("damage_threshold", 0.05, 0.01, 0.5);
                
            unusableText = builder
                .comment("Text displayed when item cannot be equipped/used due to low durability")
                .define("unusable_text", "NEEDS REPAIR");
                
            textColor = builder
                .comment("Color for the unusable text tooltip",
                        "Available colors: DARK_RED, RED, GOLD, YELLOW, DARK_GREEN, GREEN,",
                        "AQUA, DARK_AQUA, DARK_BLUE, BLUE, LIGHT_PURPLE, DARK_PURPLE,",
                        "WHITE, GRAY, DARK_GRAY, BLACK")
                .define("text_color", "DARK_RED");
            
            builder.push("armor");
            preserveArmor = builder
                .comment("Preserve armor items and move to inventory when damaged (default: true)")
                .define("preserve_armor", true);
            builder.pop();
            
            builder.push("tools");
            preserveTools = builder
                .comment("Preserve tool items when damaged (default: true)")
                .define("preserve_tools", true);
                
            preventToolUse = builder
                .comment("Prevent using tools when they need repair (default: true)")
                .define("prevent_tool_use", true);
                
            additionalTools = builder
                .comment("Additional items to treat as tools (use registry names like 'minecraft:fishing_rod')")
                .defineList("additional_tools", 
                    List.of("minecraft:fishing_rod", "minecraft:flint_and_steel", "minecraft:shears"),
                    obj -> obj instanceof String);
                    
            useVanillaTags = builder
                .comment("Use vanilla tool tags for tool detection (default: true)")
                .define("use_vanilla_tags", true);
                
            useForgeTags = builder
                .comment("Use Forge tool tags for tool detection (default: true)")
                .define("use_forge_tags", true);
            builder.pop();
            
            builder.pop();
        }
    }
}