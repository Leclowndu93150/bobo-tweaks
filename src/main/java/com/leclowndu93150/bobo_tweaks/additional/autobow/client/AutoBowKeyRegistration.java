package com.leclowndu93150.bobo_tweaks.additional.autobow.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AutoBowKeyRegistration {
    
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(AutoBowKeyBinding.AUTO_BOW_TOGGLE);
    }
}