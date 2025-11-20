package com.leclowndu93150.bobo_tweaks;

import com.leclowndu93150.bobo_tweaks.additional.arrowrebalance.ArrowRebalanceRegistration;
import com.leclowndu93150.bobo_tweaks.additional.autobow.AutoBowRegistration;
import com.leclowndu93150.bobo_tweaks.additional.itempreservation.ItemPreservationRegistration;
import com.leclowndu93150.bobo_tweaks.additional.effectimmunity.EffectImmunityRegistration;
import com.leclowndu93150.bobo_tweaks.additional.attackeffects.AttackEffectsRegistration;
import com.leclowndu93150.bobo_tweaks.additional.exclusiveitems.ExclusiveItemsRegistration;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.additional.smithing.SmithingBlacklistRegistration;
import com.leclowndu93150.bobo_tweaks.additional.villager.VillagerModuleRegistration;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import com.leclowndu93150.bobo_tweaks.additional.parrysystem.ParrySystemRegistration;
import com.leclowndu93150.bobo_tweaks.event.ModEventHandler;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModCreativeTabs;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

@Mod(BoboTweaks.MODID)
public class BoboTweaks {
    public static final String MODID = "bobo_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BoboTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        ModPotions.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        EnchantmentModuleRegistration.register(modEventBus);

        modEventBus.register(ModEventHandler.class);
        forgeEventBus.register(new ModEventHandler());

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC, "bobotweaks/bobo_tweaks-common.toml");
        ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.CLIENT_SPEC, "bobotweaks/bobo_tweaks-client.toml");

        ArrowRebalanceRegistration.init();
        AutoBowRegistration.init();
        ItemPreservationRegistration.init();
        EffectImmunityRegistration.init();
        AttackEffectsRegistration.init();
        ExclusiveItemsRegistration.init();
        SmithingBlacklistRegistration.init();
        VillagerModuleRegistration.register();
        EnchantmentModuleRegistration.registerEvents();
        ParrySystemRegistration.register();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}