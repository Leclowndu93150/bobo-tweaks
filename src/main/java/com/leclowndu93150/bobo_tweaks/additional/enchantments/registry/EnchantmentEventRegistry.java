package com.leclowndu93150.bobo_tweaks.additional.enchantments.registry;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.impl.*;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentEventRegistry {
    private static final List<EventHandlingEnchantment> ENCHANTMENTS = new ArrayList<>();

    public static void registerEnchantments() {
        ENCHANTMENTS.clear();
        
        // All enchantments with event handling
        ENCHANTMENTS.add(new MagicalAttunementEnchantment());
        ENCHANTMENTS.add(new InitiativeEnchantment());
        ENCHANTMENTS.add(new SaintsPledgeEnchantment());
        ENCHANTMENTS.add(new LeadTheChargeEnchantment());
        ENCHANTMENTS.add(new RisingEdgeEnchantment());
        ENCHANTMENTS.add(new ReprisalEnchantment());
        ENCHANTMENTS.add(new MomentumEnchantment());
        ENCHANTMENTS.add(new SpellbladeEnchantment());
        ENCHANTMENTS.add(new PerfectionistEnchantment());
        ENCHANTMENTS.add(new HunterEnchantment());
        ENCHANTMENTS.add(new MultiscaleEnchantment());
        ENCHANTMENTS.add(new InvigoratingDefensesEnchantment());
        ENCHANTMENTS.add(new LifeSurgeEnchantment());
        ENCHANTMENTS.add(new ShadowWalkerEnchantment());
        ENCHANTMENTS.add(new SniperEnchantment());
        ENCHANTMENTS.add(new OnARollEnchantment());
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onLivingHurt(event);
        }
    }

    public static void onLivingAttack(LivingAttackEvent event) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onLivingAttack(event);
        }
    }

    public static void onShieldBlock(ShieldBlockEvent event) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onShieldBlock(event);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onLivingDeath(event);
        }
    }

    public static void onSpellCast(SpellOnCastEvent event) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onSpellCast(event);
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        EnchantmentTracker.cleanupExpiredModifiers(event.player);

        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onPlayerTick(event);
        }
    }

    public static void onCriticalHit(CriticalHitEvent event) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onCriticalHit(event);
        }
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EnchantmentTracker.cleanupAllModifiers(event.getEntity());
        
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onPlayerLogin(event);
        }
    }

    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EnchantmentTracker.cleanupPlayerData(event.getEntity().getUUID());
        
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            enchantment.onPlayerLogout(event);
        }
    }

    public static void triggerPerfectionist(Player player) {
        for (EventHandlingEnchantment enchantment : ENCHANTMENTS) {
            if (enchantment instanceof PerfectionistEnchantment perfectionist) {
                perfectionist.triggerPerfectionist(player);
                break;
            }
        }
    }
}