package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleHandler;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = AttributeEvents.class, remap = false)
public class AttributeEventsMixin {

    @Inject(
            method = "apothCriticalStrike",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;level()Lnet/minecraft/world/level/Level;",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void bobo_tweaks_onApothCrit(LivingHurtEvent e, CallbackInfo ci, LivingEntity attacker, double critChance, float critDmg, RandomSource rand, float critMult) {
        if (critMult > 1.0F && attacker instanceof Player) {
            EnchantmentModuleHandler.triggerPerfectionist((Player) attacker);
        }
    }

    @Inject(
            method = "vanillaCritDmg",
            at = @At("TAIL")
    )
    private void bobo_tweaks_onVanillaCrit(CriticalHitEvent e, CallbackInfo ci) {
        if (e.isVanillaCritical()) {
            EnchantmentModuleHandler.triggerPerfectionist(e.getEntity());
        }
    }
}