package com.leclowndu93150.bobo_tweaks.additional.enchantments.impl;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.base.EventHandlingEnchantment;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
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
                String drawTicksKey = "sniper_draw_ticks";
                String readyKey = "sniper_ready";
                
                if (player.isUsingItem() && player.getUseItem() == mainHand) {
                    int drawTicks = data.getInt(drawTicksKey);
                    boolean wasDrawing = drawTicks > 0;
                    drawTicks++;
                    data.putInt(drawTicksKey, drawTicks);
                    
                    if (drawTicks >= EnchantmentModuleConfig.Sniper.drawTime) {
                        if (!data.getBoolean(readyKey)) {
                            data.putBoolean(readyKey, true);
                            if (player.level() instanceof ServerLevel serverLevel) {
                                ModNetworking.playSound(serverLevel, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 2.0F);
                            }
                        }
                    }
                } else {
                    if (data.contains(drawTicksKey)) {
                        data.remove(drawTicksKey);
                    }
                }
            }
        }
    }
    
    public void onSniperArrowHit(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow &&
                event.getSource().getEntity() instanceof Player attacker) {
            
            CompoundTag arrowData = arrow.getPersistentData();
            if (arrowData.getBoolean("sniper_arrow")) {
                int sniperLevel = arrowData.getInt("sniper_level");
                String ownerUUID = arrowData.getString("sniper_owner");
                
                if (sniperLevel > 0 && attacker.getUUID().toString().equals(ownerUUID)) {
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
                        
                        if (attacker.level() instanceof ServerLevel serverLevel) {
                            ModNetworking.playSound(serverLevel, attacker.getX(), attacker.getY(), attacker.getZ(),
                                    SoundEvents.RAVAGER_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }
    
    
    @SubscribeEvent
    public static void onSniperArrowLoose(ArrowLooseEvent event) {
        if (!EnchantmentModuleConfig.Sniper.enabled) return;
        
        Player player = event.getEntity();
        SniperEnchantment sniperEnchantment = (SniperEnchantment) EnchantmentModuleRegistration.SNIPER.get();
        int sniperLevel = sniperEnchantment.getEnchantmentLevelFromCategory(player, EnchantmentModuleConfig.Sniper.category);
        
        if (sniperLevel > 0) {
            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(player.getUUID());
            if (data.getBoolean("sniper_ready")) {
                data.putBoolean("sniper_arrow_pending", true);
                data.putInt("sniper_pending_level", sniperLevel);
                data.putLong("sniper_pending_time", System.currentTimeMillis());
            }
        }
    }
    
    @SubscribeEvent
    public static void onSniperArrowSpawn(EntityJoinLevelEvent event) {
        if (!EnchantmentModuleConfig.Sniper.enabled) return;
        
        if (event.getEntity() instanceof AbstractArrow arrow && 
            arrow.getOwner() instanceof Player owner) {
            
            CompoundTag data = EnchantmentTracker.getOrCreateEnchantmentData(owner.getUUID());
            
            if (data.getBoolean("sniper_arrow_pending")) {
                long pendingTime = data.getLong("sniper_pending_time");
                
                if (System.currentTimeMillis() - pendingTime < 1000) {
                    int sniperLevel = data.getInt("sniper_pending_level");
                    
                    CompoundTag arrowData = arrow.getPersistentData();
                    arrowData.putBoolean("sniper_arrow", true);
                    arrowData.putInt("sniper_level", sniperLevel);
                    arrowData.putString("sniper_owner", owner.getUUID().toString());
                }
                
                data.remove("sniper_arrow_pending");
                data.remove("sniper_pending_level");
                data.remove("sniper_pending_time");
                data.remove("sniper_ready");
                data.remove("sniper_draw_ticks");
            }
        }
    }
}