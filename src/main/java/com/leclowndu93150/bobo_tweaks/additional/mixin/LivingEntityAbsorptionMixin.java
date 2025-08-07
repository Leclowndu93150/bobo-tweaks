package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityAbsorptionMixin {
    
    @Shadow
    private float absorptionAmount;
    
    @ModifyVariable(method = "setAbsorptionAmount", at = @At("HEAD"), argsOnly = true)
    private float modifyAbsorptionAmount(float absorption) {
        LivingEntity entity = (LivingEntity)(Object)this;
        try {
            double multiplier = entity.getAttributeValue(ModAttributes.ABSORPTION_MULTIPLIER.get());
            if (multiplier != 1.0D && absorption > this.absorptionAmount) {
                float difference = absorption - this.absorptionAmount;
                return this.absorptionAmount + (difference * (float)multiplier);
            }
        } catch (Exception e) {
            // Ignore attribute errors during world load - return original value
        }
        return absorption;
    }
}