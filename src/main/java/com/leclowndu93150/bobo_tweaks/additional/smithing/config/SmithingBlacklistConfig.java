package com.leclowndu93150.bobo_tweaks.additional.smithing.config;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.additional.smithing.SmithingBlacklistManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmithingBlacklistConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;
    
    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new CommonConfig(commonBuilder);
        COMMON_SPEC = commonBuilder.build();
    }
    
    public static class CommonConfig {
        public final ForgeConfigSpec.ConfigValue<List<String>> smithingBlacklistedItems;
        public final ForgeConfigSpec.BooleanValue enableSmithingBlacklist;
        public final ForgeConfigSpec.BooleanValue preventBlacklistedTemplates;
        public final ForgeConfigSpec.BooleanValue preventBlacklistedBase;
        public final ForgeConfigSpec.BooleanValue preventBlacklistedAddition;
        
        CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("smithing_blacklist");
            
            enableSmithingBlacklist = builder
                .comment("Enable smithing table blacklist functionality")
                .define("enable_smithing_blacklist", true);
            
            smithingBlacklistedItems = builder
                .comment("List of items that cannot be used in smithing table (format: 'modid:itemname')",
                        "Example: ['minecraft:diamond_sword', 'minecraft:netherite_ingot']")
                .define("blacklisted_items", Arrays.asList(
                    "minecraft:bedrock",
                    "minecraft:command_block",
                    "minecraft:structure_block"
                ));
            
            preventBlacklistedTemplates = builder
                .comment("Prevent blacklisted items from being used as templates")
                .define("prevent_blacklisted_templates", true);
            
            preventBlacklistedBase = builder
                .comment("Prevent blacklisted items from being used as base items")
                .define("prevent_blacklisted_base", true);
            
            preventBlacklistedAddition = builder
                .comment("Prevent blacklisted items from being used as addition materials")
                .define("prevent_blacklisted_addition", true);
            
            builder.pop();
        }
    }
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
       SmithingBlacklistManager.clearCache();
    }
}