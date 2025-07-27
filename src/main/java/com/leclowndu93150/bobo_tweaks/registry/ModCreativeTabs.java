package com.leclowndu93150.bobo_tweaks.registry;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoboTweaks.MODID);
    
    public static final RegistryObject<CreativeModeTab> BOBO_TAB = CREATIVE_MODE_TABS.register("bobo_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + BoboTweaks.MODID + ".bobo_tab"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.POTION::getDefaultInstance)
            .displayItems((parameters, output) -> {

            })
            .build());
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}