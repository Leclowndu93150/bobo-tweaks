package com.leclowndu93150.bobo_tweaks;

import com.leclowndu93150.bobo_tweaks.additional.autobow.AutoBowRegistration;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.event.ModEventHandler;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModCreativeTabs;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BoboTweaks.MODID)
public class BoboTweaks {
    public static final String MODID = "bobo_tweaks";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BoboTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        ModPotions.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.register(ModEventHandler.class);
        forgeEventBus.register(new ModEventHandler());

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.CLIENT_SPEC);

        AutoBowRegistration.init();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
