package com.leclowndu93150.bobo_tweaks.additional.exclusiveitems;

import net.minecraftforge.common.MinecraftForge;

public class ExclusiveItemsRegistration {
    
    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ExclusiveItemsHandler());
    }
}