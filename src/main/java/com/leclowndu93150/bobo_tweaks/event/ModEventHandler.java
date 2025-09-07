package com.leclowndu93150.bobo_tweaks.event;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.DamageSourceConfig;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModDamageTypes;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import com.leclowndu93150.bobo_tweaks.refinement.RefinementManager;
import com.leclowndu93150.bobo_tweaks.util.AttributeTickHandler;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
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
                    event.add(entityType, ModAttributes.ABSORPTION_MULTIPLIER.get());
                    event.add(entityType, ModAttributes.MAX_HP_REGEN.get());
                    event.add(entityType, ModAttributes.FIRE_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.ICE_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.LIGHTNING_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.HOLY_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.ENDER_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.BLOOD_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.EVOCATION_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.NATURE_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.ELDRITCH_COOLDOWN_REDUCTION.get());
                    event.add(entityType, ModAttributes.FIRE_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.ICE_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.LIGHTNING_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.HOLY_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.ENDER_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.BLOOD_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.EVOCATION_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.NATURE_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.ELDRITCH_CAST_TIME_REDUCTION.get());
                    event.add(entityType, ModAttributes.LIFE_LEECH.get());
                    event.add(entityType, ModAttributes.LEECH_CAP.get());
                    event.add(entityType, ModAttributes.SPELL_LEECH.get());
                    event.add(entityType, ModAttributes.SPELL_LEECH_CAP.get());
                    event.add(entityType, ModAttributes.LIFESTEAL.get());
                    event.add(entityType, ModAttributes.LIFESTEAL_CAP.get());
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        // Penumbral Mark Logicv
        if (target.hasEffect(ModPotions.PENUMBRAL_MARK.get())) {
            if (event.getSource().getEntity() instanceof Player attacker) {
                int amplifier = target.getEffect(ModPotions.PENUMBRAL_MARK.get()).getAmplifier();
                
                // Damage Increase
                double baseDamageIncrease = ModConfig.COMMON.penumbralMarkDamageIncreaseBase.get();
                double damageIncreasePerLevel = ModConfig.COMMON.penumbralMarkDamageIncreasePerLevel.get();
                double totalDamageIncrease = baseDamageIncrease + (amplifier * damageIncreasePerLevel);
                event.setAmount(event.getAmount() * (1.0F + (float)totalDamageIncrease));

                // Healing
                AttributeInstance alchemicalBoostAttr = attacker.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
                double alchemicalBoost = alchemicalBoostAttr != null ? alchemicalBoostAttr.getValue() : 0.0;
                double flatHeal = ModConfig.COMMON.penumbralMarkFlatHealPerLevel.get() * (amplifier + 1);
                double scaledHeal = alchemicalBoost * ModConfig.COMMON.penumbralMarkAlchemicalBoostScaleFactorPerLevel.get() * (amplifier + 1);
                attacker.heal((float)(flatHeal + scaledHeal));

                // Remove effect
                target.removeEffect(ModPotions.PENUMBRAL_MARK.get());
            }
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            AttributeInstance damageAmpInstance = attacker.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
            if (damageAmpInstance != null && damageAmpInstance.getValue() > 0) {
                float originalDamage = event.getAmount();
                float amplifier = (float) (1.0 + damageAmpInstance.getValue());
                event.setAmount(originalDamage * amplifier);
            }
        }
    }
    
    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new RefinementManager());
        event.addListener(new DamageSourceConfig());
    }
    
    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect() == ModPotions.MANA_DRAIN.get()) {
            LivingEntity entity = event.getEntity();
            int amplifier = event.getEffectInstance().getAmplifier();
            
            System.out.println(!entity.level().isClientSide() + " " + ModList.get().isLoaded("irons_spellbooks"));
            
            if (!entity.level().isClientSide() && ModList.get().isLoaded("irons_spellbooks")) {
                try {
                    var magicData = MagicData.getPlayerMagicData(entity);
                    float currentMana = magicData.getMana();
                    float manaToDrain = ModConfig.COMMON.manaDrainAmount.get().floatValue() * (amplifier + 1);
                    
                    magicData.setMana(Math.max(0, currentMana - manaToDrain));
                    
                    BoboTweaks.getLogger().info("Drained {} mana from {}", manaToDrain, entity.getName().getString());
                } catch (Exception e) {
                    BoboTweaks.getLogger().warn("Failed to drain mana from entity", e);
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onSpecialDamageTypes(LivingHurtEvent event) {
        if (event.getSource().is(ModDamageTypes.HYPERTHERMIA)) {
            LivingEntity entity = event.getEntity();
            double heatResistance = entity.getAttributeValue(ModAttributes.HEAT_RESISTANCE.get());
            double hyperthermia = entity.getAttributeValue(ModAttributes.HYPERTHERMIA.get());
            double effectiveHyperthermia = Math.max(0, hyperthermia - heatResistance);

            float newDamage = (float) (entity.getMaxHealth() * effectiveHyperthermia);
            event.setAmount(newDamage);

        } else if (event.getSource().is(ModDamageTypes.HYPOTHERMIA)) {
            LivingEntity entity = event.getEntity();
            double coldResistance = entity.getAttributeValue(ModAttributes.COLD_RESISTANCE.get());
            double hypothermia = entity.getAttributeValue(ModAttributes.HYPOTHERMIA.get());
            double effectiveHypothermia = Math.max(0, hypothermia - coldResistance);

            float newDamage = (float) (entity.getMaxHealth() * effectiveHypothermia);
            event.setAmount(newDamage);
        }
    }
    
    @SubscribeEvent
    public void onLivingKnockBack(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getLastDamageSource() != null) {
            if (entity.getLastDamageSource().is(ModDamageTypes.LIFE_DRAIN) ||
                entity.getLastDamageSource().is(ModDamageTypes.HYPERTHERMIA) ||
                entity.getLastDamageSource().is(ModDamageTypes.HYPOTHERMIA)) {

                event.setCanceled(true);
            }
        }
    }
}