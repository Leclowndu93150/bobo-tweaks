package com.leclowndu93150.bobo_tweaks.additional.autobow.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AutoBowClientEvents {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (AutoBowKeyBinding.AUTO_BOW_TOGGLE.consumeClick()) {
            AutoBowKeyBinding.toggleAutoBow();
        }
    }
}