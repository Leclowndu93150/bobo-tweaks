package com.leclowndu93150.bobo_tweaks.additional.enchantments.handler;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.registry.EnchantmentEventRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentModuleHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(EnchantmentModuleHandler.class);
        MinecraftForge.EVENT_BUS.register(EnchantmentCapHandler.class);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        EnchantmentEventRegistry.onLivingHurt(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        EnchantmentEventRegistry.onLivingAttack(event);
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        EnchantmentEventRegistry.onShieldBlock(event);
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        EnchantmentEventRegistry.onLivingDeath(event);
    }

    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        EnchantmentEventRegistry.onSpellCast(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        EnchantmentEventRegistry.onPlayerTick(event);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EnchantmentEventRegistry.onPlayerLogout(event);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EnchantmentEventRegistry.onPlayerLogin(event);
    }
}