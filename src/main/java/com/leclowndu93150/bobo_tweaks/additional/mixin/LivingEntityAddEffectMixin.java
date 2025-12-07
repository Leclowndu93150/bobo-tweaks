package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.effectamplifiercap.config.EffectAmplifierCapConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityAddEffectMixin {

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MobEffectInstance boboTweaks$capEffectAmplifier(MobEffectInstance effectInstance) {
        LivingEntity self = (LivingEntity) (Object) this;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(self.getType()).toString();
        String effectId = BuiltInRegistries.MOB_EFFECT.getKey(effectInstance.getEffect()).toString();

        Integer cap = EffectAmplifierCapConfig.getCapForEntityAndEffect(entityId, effectId);

        if (cap != null && effectInstance.getAmplifier() > cap) {
            return new MobEffectInstance(
                    effectInstance.getEffect(),
                    effectInstance.getDuration(),
                    cap,
                    effectInstance.isAmbient(),
                    effectInstance.isVisible(),
                    effectInstance.showIcon()
            );
        }

        return effectInstance;
    }
}
