package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityKnockbackMixin {
    
    @Shadow
    public abstract LivingEntity getLastHurtByMob();
    
    @Inject(method = "knockback", at = @At("TAIL"))
    private void applyRisingEdgeKnockup(double strength, double ratioX, double ratioZ, CallbackInfo ci) {
        if (!EnchantmentModuleConfig.RisingEdge.enabled) return;
        
        LivingEntity target = (LivingEntity)(Object)this;
        LivingEntity lastAttacker = this.getLastHurtByMob();

        if (lastAttacker instanceof Player attacker) {
            int risingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                EnchantmentModuleRegistration.RISING_EDGE.get(), 
                attacker.getMainHandItem()
            );
            
            if (risingLevel > 0 && attacker.isSprinting()) {
                UUID attackerId = attacker.getUUID();
                long currentTime = System.currentTimeMillis();
                String cooldownKey = "rising_edge_cooldown";

                if (!EnchantmentTracker.isOnCooldown(attackerId, cooldownKey, currentTime)) {
                    int cooldown = EnchantmentModuleConfig.RisingEdge.PassiveA.baseCooldown -
                            (risingLevel - 1) * EnchantmentModuleConfig.RisingEdge.PassiveA.cooldownReductionPerLevel;
                    EnchantmentTracker.setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50L));

                    double knockUpDistance = EnchantmentModuleConfig.RisingEdge.PassiveA.baseKnockUp +
                            (risingLevel - 1) * EnchantmentModuleConfig.RisingEdge.PassiveA.knockUpPerLevel;
                    //knockUpDistance += 1000;
                    
                    System.out.println("Applying Rising Edge Knockup of " + knockUpDistance + " in knockback method");

                    Vec3 currentMotion = target.getDeltaMovement();
                    target.setDeltaMovement(currentMotion.add(0, knockUpDistance, 0));
                    target.hasImpulse = true;
                    target.hurtMarked = true;
                }
            }
        }
    }
}