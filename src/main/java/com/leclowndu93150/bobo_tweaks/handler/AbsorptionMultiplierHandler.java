package com.leclowndu93150.bobo_tweaks.handler;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class AbsorptionMultiplierHandler {
    
    @SubscribeEvent
    public static void onAbsorptionGain(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        double multiplier = entity.getAttributeValue(ModAttributes.ABSORPTION_MULTIPLIER.get());
        if (multiplier != 1.0D) {
            float currentAbsorption = entity.getAbsorptionAmount();
            entity.setAbsorptionAmount(currentAbsorption * (float)multiplier);
        }
    }
    
}