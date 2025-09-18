package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class SniperEnchantment extends EventHandlingEnchantment {
    
    public SniperEnchantment() {
        super(Rarity.RARE, EnchantmentModuleConfig.getCategoryFromString(EnchantmentModuleConfig.Sniper.category),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return EnchantmentModuleConfig.Sniper.maxLevel;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isCurse() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.Sniper.enabled || event.phase != TickEvent.Phase.END) return;
        
        onSniperBowDraw(event);
    }

    @Override
    public void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.Sniper.enabled) return;
        
        onSniperArrowHit(event);
    }

    public void onSniperBowDraw(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        
        int sniperLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.Sniper.category);
        
        if (sniperLevel > 0) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof CrossbowItem) {
                CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(player.getUUID());
                String drawKey = "sniper_draw_time";
                String readyKey = "sniper_ready";
                
                if (player.isUsingItem() && player.getUseItem() == mainHand) {
                    long drawTime = data.getLong(drawKey);
                    if (drawTime == 0) {
                        data.putLong(drawKey, System.currentTimeMillis());
                    } else if (System.currentTimeMillis() - drawTime >= EnchantmentModuleConfig.Sniper.drawTime * 50L) {
                        if (!data.getBoolean(readyKey)) {
                            data.putBoolean(readyKey, true);
                            player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                                    SoundSource.PLAYERS, 0.5F, 2.0F);
                        }
                    }
                } else {
                    data.remove(drawKey);
                    data.remove(readyKey);
                }
            }
        }
    }
    
    public void onSniperArrowHit(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow &&
                event.getSource().getEntity() instanceof Player attacker) {
            
            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(attacker.getUUID());
            if (data.getBoolean("sniper_ready")) {
                data.remove("sniper_ready");
                data.remove("sniper_draw_time");
                
                int sniperLevel = getEnchantmentLevelFromCategory(attacker, EnchantmentModuleConfig.Sniper.category);
                
                if (sniperLevel > 0) {
                    UUID attackerId = attacker.getUUID();
                    long currentTime = System.currentTimeMillis();
                    
                    String cooldownKey = "sniper_cooldown";
                    if (!EnchantmentTracker.isOnCooldown(attackerId, cooldownKey, currentTime)) {
                        int cooldown = EnchantmentModuleConfig.Sniper.baseCooldown -
                                (sniperLevel - 1) * EnchantmentModuleConfig.Sniper.cooldownReductionPerLevel;
                        EnchantmentTracker.setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50L));
                        
                        LivingEntity target = event.getEntity();
                        int debuffDuration = EnchantmentModuleConfig.Sniper.baseDebuffDuration +
                                (sniperLevel - 1) * EnchantmentModuleConfig.Sniper.debuffDurationPerLevel;
                        
                        AttributeInstance attackerDamageAmp = attacker.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
                        double damageAmp = attackerDamageAmp != null ? attackerDamageAmp.getValue() : 0.0;
                        
                        double armorReduction = EnchantmentModuleConfig.Sniper.baseArmorReduction *
                                (EnchantmentModuleConfig.Sniper.baseArmorScaleFactor +
                                (sniperLevel - 1) * EnchantmentModuleConfig.Sniper.armorScaleFactorPerLevel) *
                                (1 + damageAmp);
                        
                        AttributeInstance targetArmor = target.getAttribute(Attributes.ARMOR);
                        if (targetArmor != null && targetArmor.getValue() > 0) {
                            UUID armorDebuffUUID = UUID.randomUUID();
                            EnchantmentTracker.applyTimedModifier(target, Attributes.ARMOR, "sniper_armor_reduction_" + armorDebuffUUID,
                                    "Sniper Armor Reduction", -armorReduction, AttributeModifier.Operation.ADDITION,
                                    debuffDuration);
                        }
                        
                        AttributeInstance attackerArmor = attacker.getAttribute(Attributes.ARMOR);
                        double armor = attackerArmor != null ? attackerArmor.getValue() : 0.0;
                        
                        double damageAmpReduction = EnchantmentModuleConfig.Sniper.baseDamageAmpReduction *
                                (EnchantmentModuleConfig.Sniper.baseDamageAmpScaleFactor +
                                (sniperLevel - 1) * EnchantmentModuleConfig.Sniper.damageAmpScaleFactorPerLevel) *
                                (1 + armor * 0.01);
                        
                        AttributeInstance targetDamageAmp = target.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
                        if (targetDamageAmp != null && targetDamageAmp.getValue() > 0) {
                            UUID damageDebuffUUID = UUID.randomUUID();
                            EnchantmentTracker.applyTimedModifier(target, ModAttributes.DAMAGE_AMPLIFIER.get(), "sniper_damage_reduction_" + damageDebuffUUID,
                                    "Sniper Damage Reduction", -damageAmpReduction / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                                    debuffDuration);
                        }
                        
                        attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.RAVAGER_ATTACK,
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}