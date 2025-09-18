package com.leclowndu93150.bobo_tweaks.additional.enchantments.events;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.impl.OnARollEnchantment;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import net.combatroll.api.event.ServerSideRollEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnARollHandler {
    
    private static final Map<UUID, Long> rollCooldowns = new ConcurrentHashMap<>();
    private static final OnARollEnchantment onARollEnchantment = new OnARollEnchantment();
    
    public static void register() {
        ServerSideRollEvents.PLAYER_START_ROLLING.register((player, velocity) -> {
            if (!EnchantmentModuleConfig.OnARoll.enabled) return;
            
            Player forgePlayer = (Player) player;
            
            int onARollLevel = onARollEnchantment.getEnchantmentLevelFromCategory(forgePlayer, EnchantmentModuleConfig.OnARoll.category);
            
            if (onARollLevel > 0) {
                UUID playerId = forgePlayer.getUUID();
                long currentTime = System.currentTimeMillis();
                Long lastUse = rollCooldowns.get(playerId);
                
                int cooldown = EnchantmentModuleConfig.OnARoll.baseCooldown -
                        (onARollLevel - 1) * EnchantmentModuleConfig.OnARoll.cooldownReductionPerLevel;
                long cooldownMs = cooldown * 50L;
                
                if (lastUse == null || currentTime - lastUse >= cooldownMs) {
                    rollCooldowns.put(playerId, currentTime);
                    
                    int duration = EnchantmentModuleConfig.OnARoll.baseDuration +
                            (onARollLevel - 1) * EnchantmentModuleConfig.OnARoll.durationPerLevel;
                    
                    forgePlayer.addEffect(new MobEffectInstance(
                            ModPotions.ADRENALINE.get(),
                            duration,
                            onARollLevel - 1,
                            false,
                            true,
                            true
                    ));
                    
                    forgePlayer.level().playSound(null, forgePlayer.blockPosition(), 
                            SoundEvents.RAVAGER_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
    }
}