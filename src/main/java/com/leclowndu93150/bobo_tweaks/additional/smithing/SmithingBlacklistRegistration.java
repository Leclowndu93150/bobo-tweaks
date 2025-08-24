package com.leclowndu93150.bobo_tweaks.additional.smithing;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.command.SmithingBlacklistCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SmithingBlacklistRegistration {
    
    public static void init() {
        BoboTweaks.getLogger().info("Initializing Smithing Blacklist module");
        MinecraftForge.EVENT_BUS.register(SmithingBlacklistRegistration.class);
    }
    
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        SmithingBlacklistManager.init();
    }
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SmithingBlacklistCommand.register(event.getDispatcher());
    }
}