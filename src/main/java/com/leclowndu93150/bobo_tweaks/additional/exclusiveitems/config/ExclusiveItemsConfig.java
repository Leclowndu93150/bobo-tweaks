package com.leclowndu93150.bobo_tweaks.additional.exclusiveitems.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ExclusiveItemsConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUSIVE_TAGS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MODULE;
    public static final ForgeConfigSpec.BooleanValue NOTIFY_PLAYER;
    public static final ForgeConfigSpec.BooleanValue CHECK_CURIOS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("exclusive_items");
        
        ENABLE_MODULE = builder
            .comment("Enable the Exclusive Items module")
            .define("enable_module", true);
            
        EXCLUSIVE_TAGS = builder
            .comment("List of item tags that should be exclusive (only one item from each tag allowed in inventory)",
                     "Examples: forge:bows, bobo_tweaks:spellbooks, forge:tools/pickaxes")
            .defineList("exclusive_tags", 
                Arrays.asList("forge:bows", "bobo_tweaks:spellbooks"), 
                obj -> obj instanceof String);
                
        NOTIFY_PLAYER = builder
            .comment("Show notification to player when item is dropped due to exclusivity")
            .define("notify_player", true);
            
        CHECK_CURIOS = builder
            .comment("Also check Curios slots for exclusive items (requires Curios mod)")
            .define("check_curios", true);

        builder.pop();
        SPEC = builder.build();
    }
}