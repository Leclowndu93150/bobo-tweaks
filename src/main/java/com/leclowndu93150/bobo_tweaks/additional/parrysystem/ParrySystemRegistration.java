package com.leclowndu93150.bobo_tweaks.additional.parrysystem;

import com.leclowndu93150.bobo_tweaks.additional.parrysystem.command.ParryCommand;
import com.leclowndu93150.bobo_tweaks.additional.parrysystem.config.ParrySystemConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ParrySystemRegistration {
    
    public static void register() {
        ParrySystemConfig.load();
        ParryData.load();
        
        MinecraftForge.EVENT_BUS.register(ParrySystemHandler.class);
    }
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ParryCommand.register(event.getDispatcher());
    }
}
