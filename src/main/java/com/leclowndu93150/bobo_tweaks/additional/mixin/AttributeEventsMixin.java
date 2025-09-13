package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleHandler;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALObjects.Attributes;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.placebo.network.PacketDistro;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = AttributeEvents.class, remap = false)
public class AttributeEventsMixin {

    /**
     * @author bobo_tweaks
     * @reason Add perfectionist enchantment trigger
     */
    @Overwrite
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void apothCriticalStrike(LivingHurtEvent e) {
        Entity var4 = e.getSource().getEntity();
        LivingEntity var10000;
        if (var4 instanceof LivingEntity le) {
            var10000 = le;
        } else {
            var10000 = null;
        }

        LivingEntity attacker = var10000;
        if (attacker != null) {
            double critChance = attacker.getAttributeValue((Attribute)Attributes.CRIT_CHANCE.get());
            float critDmg = (float)attacker.getAttributeValue((Attribute)Attributes.CRIT_DAMAGE.get());
            RandomSource rand = e.getEntity().getRandom();

            float critMult;
            for(critMult = 1.0F; (double)rand.nextFloat() <= critChance && critDmg > 1.0F; critDmg *= 0.85F) {
                --critChance;
                critMult *= critDmg;
            }

            e.setAmount(e.getAmount() * critMult);
            if (critMult > 1.0F && !attacker.level().isClientSide) {
                PacketDistro.sendToTracking(AttributesLib.CHANNEL, new CritParticleMessage(e.getEntity().getId()), (ServerLevel)attacker.level(), e.getEntity().blockPosition());

                if (attacker instanceof Player) {
                    EnchantmentModuleHandler.triggerPerfectionist((Player) attacker);
                }
            }
        }
    }

    /**
     * @author bobo_tweaks
     * @reason Add perfectionist enchantment trigger
     */
    @Overwrite
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void vanillaCritDmg(CriticalHitEvent e) {
        float critDmg = (float)e.getEntity().getAttributeValue((Attribute)Attributes.CRIT_DAMAGE.get());
        if (e.isVanillaCritical()) {
            e.setDamageModifier(Math.max(e.getDamageModifier(), critDmg));

            EnchantmentModuleHandler.triggerPerfectionist(e.getEntity());
        }
    }
}