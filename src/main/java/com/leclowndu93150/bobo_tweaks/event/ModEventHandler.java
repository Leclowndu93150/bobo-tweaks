package com.leclowndu93150.bobo_tweaks.event;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.util.AttributeTickHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModEventHandler {
    
    @Mod.EventBusSubscriber(modid = BoboTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            MinecraftForge.EVENT_BUS.register(AttributeTickHandler.class);
        }
        
        @SubscribeEvent
        public static void onAttributeModification(EntityAttributeModificationEvent event) {
            for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
                if (event.has(entityType, Attributes.MAX_HEALTH)) {
                    event.add(entityType, ModAttributes.DAMAGE_AMPLIFIER.get());
                    event.add(entityType, ModAttributes.LIFE_DRAIN.get());
                    event.add(entityType, ModAttributes.REGENERATION.get());
                    event.add(entityType, ModAttributes.HYPERTHERMIA.get());
                    event.add(entityType, ModAttributes.HEAT_RESISTANCE.get());
                    event.add(entityType, ModAttributes.HYPOTHERMIA.get());
                    event.add(entityType, ModAttributes.COLD_RESISTANCE.get());
                    event.add(entityType, ModAttributes.ALCHEMICAL_BOOST.get());
                    event.add(entityType, ModAttributes.POWER_LINK.get());
                    event.add(entityType, ModAttributes.JUMP_COUNT.get());
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            AttributeInstance damageAmpInstance = attacker.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
            if (damageAmpInstance != null && damageAmpInstance.getValue() > 0) {
                float originalDamage = event.getAmount();
                float amplifier = (float) (1.0 + damageAmpInstance.getValue());
                event.setAmount(originalDamage * amplifier);
            }
        }
    }
}