package com.leclowndu93150.bobo_tweaks.additional.enchantments.events;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import net.combatroll.api.event.ServerSideRollEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnARollHandler {
    
    private static final Map<UUID, Long> rollCooldowns = new ConcurrentHashMap<>();
    
    public static void register() {
        ServerSideRollEvents.PLAYER_START_ROLLING.register((player, velocity) -> {
            System.out.println("[OnARoll] Player started rolling: " + player.getName().getString());
            
            if (!EnchantmentModuleConfig.OnARoll.enabled) {
                System.out.println("[OnARoll] Enchantment disabled in config");
                return;
            }
            
            Player forgePlayer = (Player) player;
            
            int onARollLevel = EnchantmentHelper.getItemEnchantmentLevel(
                EnchantmentModuleRegistration.ON_A_ROLL.get(),
                forgePlayer.getItemBySlot(EquipmentSlot.LEGS)
            );
            System.out.println("[OnARoll] Enchantment level: " + onARollLevel);
            
            if (onARollLevel > 0) {
                UUID playerId = forgePlayer.getUUID();
                long currentTime = System.currentTimeMillis();
                Long lastUse = rollCooldowns.get(playerId);
                
                int cooldown = EnchantmentModuleConfig.OnARoll.baseCooldown -
                        (onARollLevel - 1) * EnchantmentModuleConfig.OnARoll.cooldownReductionPerLevel;
                long cooldownMs = cooldown * 50L;
                
                System.out.println("[OnARoll] Cooldown: " + cooldown + " ticks (" + cooldownMs + "ms)");
                System.out.println("[OnARoll] Last use: " + lastUse + ", Current time: " + currentTime);
                
                if (lastUse == null || currentTime - lastUse >= cooldownMs) {
                    System.out.println("[OnARoll] Cooldown ready, applying effect");
                    rollCooldowns.put(playerId, currentTime);
                    
                    int duration = EnchantmentModuleConfig.OnARoll.baseDuration +
                            (onARollLevel - 1) * EnchantmentModuleConfig.OnARoll.durationPerLevel;
                    
                    System.out.println("[OnARoll] Duration: " + duration + " ticks, Amplifier: " + (onARollLevel - 1));
                    
                    forgePlayer.addEffect(new MobEffectInstance(
                            ModPotions.ADRENALINE.get(),
                            duration,
                            onARollLevel - 1,
                            false,
                            true,
                            true
                    ));

                    System.out.println("[OnARoll] ADDED ADRENALINE EFFECT");
                    
                    if (forgePlayer.level() instanceof ServerLevel serverLevel) {
                        ModNetworking.playSound(serverLevel, forgePlayer.getX(), forgePlayer.getY(), forgePlayer.getZ(), 
                                SoundEvents.RAVAGER_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
                        System.out.println("[OnARoll] Played sound effect");
                    }
                } else {
                    long timeRemaining = cooldownMs - (currentTime - lastUse);
                    System.out.println("[OnARoll] On cooldown, " + timeRemaining + "ms remaining");
                }
            } else {
                System.out.println("[OnARoll] Player does not have enchantment");
            }
        });
    }
}