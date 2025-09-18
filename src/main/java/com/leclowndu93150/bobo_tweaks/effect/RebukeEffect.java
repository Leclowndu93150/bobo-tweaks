package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class RebukeEffect extends MobEffect {
    private static final Map<UUID, RebukeData> rebukeDataMap = new HashMap<>();
    
    public RebukeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8B008B);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            int negationCount = amplifier + 1;
            RebukeData data = new RebukeData(negationCount, amplifier);
            rebukeDataMap.put(entity.getUUID(), data);
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            rebukeDataMap.remove(entity.getUUID());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (areTeammates(target, attacker)) {
                return;
            }
            
            RebukeData data = rebukeDataMap.get(target.getUUID());
            
            if (data != null && data.remainingNegations > 0) {
                event.setCanceled(true);
                
                data.remainingNegations--;
                
                float baseDamage = getBaseDamage(data.amplifier);
                float maxHpDamage = attacker.getMaxHealth() * getMaxHpPercentage(data.amplifier);
                float totalDamage = baseDamage + maxHpDamage;
                
                DamageSource rebukeSource = target.level().damageSources().magic();
                attacker.hurt(rebukeSource, totalDamage);
                
                if (target.level() instanceof ServerLevel serverLevel) {
                    ModNetworking.playSound(serverLevel, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8F, 1.0F);
                }
                
                if (data.remainingNegations <= 0) {
                    rebukeDataMap.remove(target.getUUID());
                    
                    MobEffectInstance rebukeInstance = target.getEffect(ModPotions.REBUKE.get());
                    if (rebukeInstance != null) {
                        target.removeEffect(ModPotions.REBUKE.get());
                    }
                }
            }
        }
    }
    
    private static float getBaseDamage(int amplifier) {
        return ModConfig.COMMON.rebukeBaseDamage.get().floatValue() * (amplifier + 1);
    }
    
    private static float getMaxHpPercentage(int amplifier) {
        return ModConfig.COMMON.rebukeMaxHpPercentage.get().floatValue() * (amplifier + 1);
    }
    
    private static boolean areTeammates(LivingEntity target, LivingEntity attacker) {
        if (target instanceof Player targetPlayer && attacker instanceof Player attackerPlayer) {
            return targetPlayer.getTeam() != null && 
                   targetPlayer.getTeam().equals(attackerPlayer.getTeam());
        }
        return false;
    }
    
    @Override
    public boolean isInstantenous() {
        return false;
    }
    
    private static class RebukeData {
        public int remainingNegations;
        public int amplifier;
        
        public RebukeData(int remainingNegations, int amplifier) {
            this.remainingNegations = remainingNegations;
            this.amplifier = amplifier;
        }
    }
}