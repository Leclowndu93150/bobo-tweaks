package com.leclowndu93150.bobo_tweaks.additional.villager;

import com.leclowndu93150.bobo_tweaks.additional.villager.command.VillagerShelterCommand;
import com.leclowndu93150.bobo_tweaks.command.VillagerPenaltyCommand;
import com.leclowndu93150.bobo_tweaks.additional.villager.config.VillagerModuleConfig;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerModuleRegistration {
    
    public static void register() {
        VillagerModuleConfig.load();
    }
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        VillagerShelterCommand.register(event.getDispatcher());
        VillagerPenaltyCommand.register(event.getDispatcher());
    }
}