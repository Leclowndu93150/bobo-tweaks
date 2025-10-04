package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.effect.AbsoptionMobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// DISABLED: Absorption multiplier attribute has been replaced by 4 school-specific attributes (Aqua/Geo cast time/cooldown reduction)
// @Mixin(AbsoptionMobEffect.class)
public class AbsorptionEffectMixin {
    
    // @Inject(method = "addAttributeModifiers", at = @At("HEAD"), cancellable = true)
    // private void modifyAbsorptionGain(LivingEntity entity, AttributeMap attributeMap, int amplifier, CallbackInfo ci) {
    //     try {
    //         double multiplier = entity.getAttributeValue(ModAttributes.ABSORPTION_MULTIPLIER.get());
    //
    //         float baseAbsorption = 4 * (amplifier + 1);
    //         float multipliedAbsorption = baseAbsorption * (float)multiplier;
    //         
    //         BoboTweaks.getLogger().info("Absorption Effect Applied: Amplifier={}, Base={}, Multiplier={}, Final={}", 
    //             amplifier, baseAbsorption, multiplier, multipliedAbsorption);
    //
    //         entity.setAbsorptionAmount(entity.getAbsorptionAmount() + multipliedAbsorption);
    //
    //         ci.cancel();
    //     } catch (Exception e) {
    //         BoboTweaks.getLogger().error("Error in absorption effect mixin: ", e);
    //     }
    // }
    // 
    // @Inject(method = "removeAttributeModifiers", at = @At("HEAD"), cancellable = true)
    // private void modifyAbsorptionRemoval(LivingEntity entity, AttributeMap attributeMap, int amplifier, CallbackInfo ci) {
    //     try {
    //         double multiplier = entity.getAttributeValue(ModAttributes.ABSORPTION_MULTIPLIER.get());
    //
    //         float baseAbsorption = 4 * (amplifier + 1);
    //         float multipliedAbsorption = baseAbsorption * (float)multiplier;
    //         
    //         BoboTweaks.getLogger().info("Absorption Effect Removed: Amplifier={}, Base={}, Multiplier={}, Removing={}", 
    //             amplifier, baseAbsorption, multiplier, multipliedAbsorption);
    //
    //         entity.setAbsorptionAmount(Math.max(0, entity.getAbsorptionAmount() - multipliedAbsorption));
    //
    //         ci.cancel();
    //     } catch (Exception e) {
    //         BoboTweaks.getLogger().error("Error in absorption effect removal mixin: ", e);
    //     }
    // }
}