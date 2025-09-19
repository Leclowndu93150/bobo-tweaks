package com.leclowndu93150.bobo_tweaks.additional.enchantments.registry;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.impl.*;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentEventRegistry {
    private static final List<EventHandlingEnchantment> ENCHANTMENTS = new ArrayList<>();

    public static void registerEnchantments() {
        // This method is called after enchantments are registered, so we can safely get them
    }
    
    private static void ensureEnchantmentsLoaded() {
        if (ENCHANTMENTS.isEmpty()) {
            // Lazy load the enchantments when first needed
            if (EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get());
            if (EnchantmentModuleRegistration.INITIATIVE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.INITIATIVE.get());
            if (EnchantmentModuleRegistration.SAINTS_PLEDGE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.SAINTS_PLEDGE.get());
            if (EnchantmentModuleRegistration.LEAD_THE_CHARGE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.LEAD_THE_CHARGE.get());
            if (EnchantmentModuleRegistration.RISING_EDGE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.RISING_EDGE.get());
            if (EnchantmentModuleRegistration.REPRISAL.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.REPRISAL.get());
            if (EnchantmentModuleRegistration.MOMENTUM.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.MOMENTUM.get());
            if (EnchantmentModuleRegistration.SPELLBLADE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.SPELLBLADE.get());
            if (EnchantmentModuleRegistration.PERFECTIONIST.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.PERFECTIONIST.get());
            if (EnchantmentModuleRegistration.HUNTER.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.HUNTER.get());
            if (EnchantmentModuleRegistration.MULTISCALE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.MULTISCALE.get());
            if (EnchantmentModuleRegistration.INVIGORATING_DEFENSES.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.INVIGORATING_DEFENSES.get());
            if (EnchantmentModuleRegistration.LIFE_SURGE.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.LIFE_SURGE.get());
            if (EnchantmentModuleRegistration.SHADOW_WALKER.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.SHADOW_WALKER.get());
            if (EnchantmentModuleRegistration.SNIPER.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.SNIPER.get());
            if (EnchantmentModuleRegistration.ON_A_ROLL.get() instanceof EventHandlingEnchantment)
                ENCHANTMENTS.add((EventHandlingEnchantment) EnchantmentModuleRegistration.ON_A_ROLL.get());
        }
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onLivingHurt(event);
        }
    }

    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onLivingAttack(event);
        }
    }

    public static void onShieldBlock(ShieldBlockEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onShieldBlock(event);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onLivingDeath(event);
        }
    }

    public static void onSpellCast(SpellOnCastEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onSpellCast(event);
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        ensureEnchantmentsLoaded();
        EnchantmentTracker.cleanupExpiredModifiers(event.player);

        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onPlayerTick(event);
        }
    }

    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            EnchantmentTracker.cleanupExpiredModifiers(event.getEntity());
        }
    }

    public static void onCriticalHit(CriticalHitEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onCriticalHit(event);
        }
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ensureEnchantmentsLoaded();
        EnchantmentTracker.cleanupAllModifiers(event.getEntity());
        
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onPlayerLogin(event);
        }
    }

    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ensureEnchantmentsLoaded();
        EnchantmentTracker.cleanupPlayerData(event.getEntity().getUUID());
        
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onPlayerLogout(event);
        }
    }

    public static void triggerPerfectionist(Player player) {
        ensureEnchantmentsLoaded();
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            if (enchantment instanceof PerfectionistEnchantment perfectionist) {
                perfectionist.triggerPerfectionist(player);
                break;
            }
        }
    }
}