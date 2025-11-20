package com.leclowndu93150.bobo_tweaks.additional.parrysystem;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.additional.parrysystem.config.ParrySystemConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import dev.shadowsoffire.placebo.events.ItemUseEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParrySystemHandler {
    
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!ParrySystemConfig.enableParrySystem) {
            return;
        }
        
        Player player = event.getEntity();
        
        if (!ParryData.hasParryAbility(player)) {
            return;
        }
        
        if (event.getItemStack().getItem() instanceof ShieldItem) {
            if (player.level() instanceof ServerLevel serverLevel) {
                long currentTime = serverLevel.getGameTime();
                ParryData.updateShieldRaiseTime(player, currentTime);
            }
        }
    }
    
    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!ParrySystemConfig.enableParrySystem) {
            return;
        }
        
        if (event.getEntity() instanceof Player player) {
            if (!ParryData.hasParryAbility(player)) {
                return;
            }
            
            if (!(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            
            ParryData.ParryAbilityData data = ParryData.getPlayerData(player);
            long currentTime = serverLevel.getGameTime();
            long timeSinceRaise = currentTime - data.lastShieldRaiseTime;
            long parryWindowTicks = (long)(ParrySystemConfig.parryTimingWindowSeconds * 20);
            
            if (timeSinceRaise <= parryWindowTicks && timeSinceRaise > 0) {
                handleSuccessfulParry(player, data.abilityType, serverLevel, currentTime);
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!ParrySystemConfig.enableParrySystem) {
            return;
        }
        
        if (event.getEntity() instanceof Player player) {
            if (!ParryData.hasParryAbility(player)) {
                return;
            }
            
            if (!(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            
            if (event.getSource().getDirectEntity() == null) {
                return;
            }
            
            ParryData.ParryAbilityData data = ParryData.getPlayerData(player);
            long currentTime = serverLevel.getGameTime();
            long timeSinceRaise = currentTime - data.lastShieldRaiseTime;
            long parryWindowTicks = (long)(ParrySystemConfig.parryTimingWindowSeconds * 20);
            
            if (timeSinceRaise <= parryWindowTicks && timeSinceRaise > 0) {
                handleSuccessfulParry(player, data.abilityType, serverLevel, currentTime);
                event.setCanceled(true);
                data.lastShieldRaiseTime = 0;
            }
        }
    }
    
    private static void handleSuccessfulParry(Player player, String abilityType, ServerLevel level, long currentTime) {
        if (abilityType.equals("energized")) {
            boolean alreadyHasEffect = player.hasEffect(ModPotions.ENERGIZED.get());
            
            player.addEffect(new MobEffectInstance(
                ModPotions.ENERGIZED.get(),
                ParrySystemConfig.energizedEffectDuration,
                0,
                false,
                true,
                true
            ));
            
            level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                20,
                0.5,
                0.5,
                0.5,
                0.1
            );
            
            level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.IRON_GOLEM_REPAIR,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
            );
        }
    }
    
    @SubscribeEvent
    public static void onItemUse(ItemUseEvent event) {
        if (!ParrySystemConfig.enableParrySystem) {
            return;
        }
        
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        
        if (!ParryData.hasParryAbility(player)) {
            return;
        }
        
        if (event.getItemStack().getItem() instanceof ShieldItem) {
            if (player.level() instanceof ServerLevel serverLevel) {
                long currentTime = serverLevel.getGameTime();
                ParryData.updateShieldRaiseTime(player, currentTime);
            }
        }
    }
}
