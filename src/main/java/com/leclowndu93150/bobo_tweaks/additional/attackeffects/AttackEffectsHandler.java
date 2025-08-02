package com.leclowndu93150.bobo_tweaks.additional.attackeffects;

import com.leclowndu93150.bobo_tweaks.additional.attackeffects.config.AttackEffectsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttackEffectsHandler {
    
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();
        
        String targetEntityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString();

        AttackEffectsConfig.EntityAttackConfig config = AttackEffectsConfig.getConfigForEntity(targetEntityId);
        if (config == null) {
            return;
        }

        if (attacker instanceof Player && !config.appliedToPlayer) {
            return;
        }

        for (AttackEffectsConfig.EffectConfig effectConfig : config.effectsApplied) {
            net.minecraft.resources.ResourceLocation effectLocation = net.minecraft.resources.ResourceLocation.tryParse(effectConfig.effectId);
            if (effectLocation != null) {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectLocation);
                if (effect != null) {
                    MobEffectInstance effectInstance = new MobEffectInstance(
                        BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect).get(),
                        effectConfig.duration,
                        effectConfig.amplifier
                    );
                    attacker.addEffect(effectInstance);
                }
            }
        }
    }
}