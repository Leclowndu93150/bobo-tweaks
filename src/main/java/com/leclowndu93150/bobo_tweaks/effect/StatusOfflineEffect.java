package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class StatusOfflineEffect extends MobEffect {
    private static final Set<UUID> activeEffectPlayers = new HashSet<>();
    
    public StatusOfflineEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00CED1);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            activeEffectPlayers.add(entity.getUUID());
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            activeEffectPlayers.remove(entity.getUUID());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            handleStatusOffline(attacker, event.getEntity(), attacker);
        } else if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow) {
            if (arrow.getOwner() instanceof LivingEntity attacker) {
                handleStatusOffline(attacker, event.getEntity(), attacker);
            }
        }
    }
    
    private static void handleStatusOffline(LivingEntity attacker, LivingEntity target, LivingEntity effectSource) {
        if (!activeEffectPlayers.contains(attacker.getUUID())) {
            return;
        }
        
        MobEffectInstance statusOfflineEffect = attacker.getEffect(ModPotions.STATUS_OFFLINE.get());
        if (statusOfflineEffect == null) {
            return;
        }
        
        // Only trigger if this is not magic damage (to avoid infinite loops)
        DamageSource magicDamageCheck = attacker.damageSources().magic();
        // We can't check the event source here, so we'll use a different approach
        // StatusOffline only triggers once per effect, so infinite loops shouldn't be an issue
        
        int amplifier = statusOfflineEffect.getAmplifier();
        List<MobEffectInstance> negativeEffects = new ArrayList<>();
        
        for (MobEffectInstance effect : target.getActiveEffects()) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                negativeEffects.add(effect);
            }
        }
        
        int clearedCount = negativeEffects.size();
        
        for (MobEffectInstance effect : negativeEffects) {
            target.removeEffect(effect.getEffect());
        }
        
        AttributeInstance damageAmpInstance = attacker.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        double damageAmplifier = damageAmpInstance != null ? damageAmpInstance.getValue() : 0.0D;
        
        float baseDamage = getBaseMagicDamage(amplifier);
        float statusBonus = getClearedStatusBonus(amplifier) * clearedCount;
        float totalDamage = (baseDamage + statusBonus) * (float)(1.0 + damageAmplifier);
        
        if (totalDamage > 0 && !target.level().isClientSide()) {
            DamageSource magicSource = attacker.level().damageSources().magic();
            target.hurt(magicSource, totalDamage);
        }
        
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
            SoundEvents.BLAZE_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
        
        attacker.removeEffect(ModPotions.STATUS_OFFLINE.get());
        activeEffectPlayers.remove(attacker.getUUID());
    }
    
    private static float getBaseMagicDamage(int amplifier) {
        return ModConfig.COMMON.statusOfflineBaseDamage.get().floatValue() * (amplifier + 1);
    }
    
    private static float getClearedStatusBonus(int amplifier) {
        return ModConfig.COMMON.statusOfflineStatusBonus.get().floatValue() * (amplifier + 1);
    }
    
    @Override
    public boolean isInstantenous() {
        return false;
    }
}