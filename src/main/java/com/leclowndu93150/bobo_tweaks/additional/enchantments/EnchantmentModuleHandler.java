package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentModuleHandler {
    
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();
    private static final Map<UUID, CompoundTag> playerEnchantmentData = new HashMap<>();
    private static final Map<UUID, UUID> hunterMarks = new HashMap<>();
    
    public static void register() {
        MinecraftForge.EVENT_BUS.register(EnchantmentModuleHandler.class);
        MinecraftForge.EVENT_BUS.register(EnchantmentCapHandler.class);
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        
        if (event.getEntity() instanceof Player player) {
            handleReprisalTrigger(player, event.getSource());
            handleLifeSurgeTrigger(player);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        
        if (event.getSource().getEntity() instanceof Player attacker) {
            handleWeaponEnchantmentAttacks(attacker, event.getEntity(), event.getSource());
            handleSpellbladePassiveB(attacker);
            handleShadowWalkerInvisibilityDamage(attacker);
            handleMagicalAttunementDamage(attacker, event);
        }
        
        if (event.getEntity() instanceof Player defender) {
            handleInvigoratingDefensesTrigger(defender, event);
        }
    }
    
    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();
        
        handlePerfectionistTrigger(player, weapon);
    }
    
    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        
        if (event.getSource().getEntity() instanceof Player killer) {
            handleMomentumTrigger(killer, event.getEntity(), true);
            handleShadowWalkerTrigger(killer, true);
            handleHunterMarkRemoval(event.getEntity());
        }
        
        handleTeammateKillEffects(event);
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        
        Player player = event.player;
        handlePeriodicEffects(player);
        handleMomentumExpiry(player);
        handleInvigoratingDefensesHealing(player);
        handleMultiscaleContinuous(player);
    }
    
    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;

        handleSpellbladeSpellCast(event.getEntity());
    }
    
    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        playerCooldowns.remove(playerId);
        playerEnchantmentData.remove(playerId);
        hunterMarks.values().removeIf(markedBy -> markedBy.equals(playerId));
    }
    
    private static void handleReprisalTrigger(Player player, DamageSource source) {
        if (!EnchantmentModuleConfig.Reprisal.enabled) return;
        
        ItemStack weapon = player.getMainHandItem();
        int reprisalLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.REPRISAL.get(), weapon);
            
        if (reprisalLevel > 0 && source.getEntity() instanceof Mob) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "reprisal_cooldown";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;
            
            int cooldown = EnchantmentModuleConfig.Reprisal.baseCooldown - 
                (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.cooldownReductionPerLevel;
            setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50));
            
            double damageBoost = EnchantmentModuleConfig.Reprisal.baseDamageBoost + 
                (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.damageBoostPerLevel;
            int duration = EnchantmentModuleConfig.Reprisal.baseDuration + 
                (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.durationPerLevel;
            
            applyDamageAmplifier(player, damageBoost, duration);
            setEnchantmentFlag(playerId, "reprisal_active", true, duration);
        }
    }
    
    private static void handleMomentumTrigger(Player killer, LivingEntity victim, boolean isDirectKill) {
        if (!EnchantmentModuleConfig.Momentum.enabled) return;
        
        ItemStack weapon = killer.getMainHandItem();
        int momentumLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.MOMENTUM.get(), weapon);
            
        if (momentumLevel > 0 && (victim instanceof Mob)) {
            UUID killerId = killer.getUUID();
            
            int stacksToAdd = isDirectKill ? 
                EnchantmentModuleConfig.Momentum.killStacks : 
                EnchantmentModuleConfig.Momentum.allyKillStacks;
            
            CompoundTag data = getOrCreateEnchantmentData(killerId);
            int currentStacks = data.getInt("momentum_stacks");
            int maxStacks = EnchantmentModuleConfig.Momentum.baseMaxStacks + 
                (momentumLevel - 1) * EnchantmentModuleConfig.Momentum.maxStackIncreasePerLevel;
            
            if (currentStacks >= maxStacks) {
                data.putLong("momentum_expire_time", 
                    System.currentTimeMillis() + (EnchantmentModuleConfig.Momentum.stackDuration * 50));
            } else {
                int newStacks = Math.min(currentStacks + stacksToAdd, maxStacks);
                data.putInt("momentum_stacks", newStacks);
                data.putLong("momentum_expire_time", 
                    System.currentTimeMillis() + (EnchantmentModuleConfig.Momentum.stackDuration * 50));
                updateMomentumDamage(killer, newStacks, momentumLevel);
            }
        }
    }
    
    private static void handleShadowWalkerTrigger(Player killer, boolean isDirectKill) {
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;
        
        ItemStack chestplate = killer.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        int shadowLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.SHADOW_WALKER.get(), chestplate);
            
        if (shadowLevel > 0) {
            if (isDirectKill) {
                MobEffect trueInvisEffect = ForgeRegistries.MOB_EFFECTS.getValue(
                    new ResourceLocation("irons_spellbooks", "true_invisibility"));
                
                if (trueInvisEffect != null) {
                    killer.addEffect(new MobEffectInstance(trueInvisEffect, 
                        EnchantmentModuleConfig.ShadowWalker.invisibilityDuration, 0));
                }
            }
            
            AttributeInstance speedAttr = killer.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                AttributeModifier speedMod = new AttributeModifier(
                    UUID.fromString("shadow-walker-speed"), 
                    "Shadow Walker Speed", 
                    EnchantmentModuleConfig.ShadowWalker.movementSpeedPercent / 100.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
                speedAttr.addTransientModifier(speedMod);
            }
        }
    }
    
    private static void handleSpellbladePassiveB(Player attacker) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;
        
        ItemStack weapon = attacker.getMainHandItem();
        int spellbladeLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.SPELLBLADE.get(), weapon);
            
        if (spellbladeLevel > 0) {
            UUID attackerId = attacker.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "spellblade_passive_b_cooldown";
            if (isOnCooldown(attackerId, cooldownKey, currentTime)) return;
            
            int cooldown = EnchantmentModuleConfig.Spellblade.PassiveB.baseCooldown - 
                (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.cooldownDecreasePerLevel;
            setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50));
            
            double arrowDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseArrowDamageBoost + 
                (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.arrowBoostPerLevel;
            double attackDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseAttackDamageBoost + 
                (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.attackBoostPerLevel;
            
            applyAttributeBoost(attacker, getArrowDamageAttribute(), arrowDamageBoost, 
                EnchantmentModuleConfig.Spellblade.PassiveB.duration);
            applyAttributeBoost(attacker, Attributes.ATTACK_DAMAGE, attackDamageBoost, 
                EnchantmentModuleConfig.Spellblade.PassiveB.duration);
        }
    }
    
    
    private static void handlePerfectionistTrigger(Player player, ItemStack weapon) {
        if (!EnchantmentModuleConfig.Perfectionist.enabled) return;
        
        int perfectionistLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.PERFECTIONIST.get(), weapon);
            
        if (perfectionistLevel > 0) {
            double attackSpeedBoost = EnchantmentModuleConfig.Perfectionist.baseAttackSpeedBoost + 
                (perfectionistLevel - 1) * EnchantmentModuleConfig.Perfectionist.attackSpeedPerLevel;
            double castSpeedBoost = EnchantmentModuleConfig.Perfectionist.baseCastSpeedBoost + 
                (perfectionistLevel - 1) * EnchantmentModuleConfig.Perfectionist.castSpeedPerLevel;
            
            applyAttributeBoost(player, Attributes.ATTACK_SPEED, attackSpeedBoost, 
                EnchantmentModuleConfig.Perfectionist.duration);
            applyAttributeBoost(player, getCastTimeReductionAttribute(), castSpeedBoost, 
                EnchantmentModuleConfig.Perfectionist.duration);
        }
    }
    
    private static void handleLifeSurgeTrigger(Player player) {
        if (!EnchantmentModuleConfig.LifeSurge.enabled) return;
        
        ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        int lifeSurgeLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.LIFE_SURGE.get(), chestplate);
            
        if (lifeSurgeLevel > 0 && 
            player.getHealth() / player.getMaxHealth() <= EnchantmentModuleConfig.LifeSurge.healthThreshold) {
            
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "life_surge_cooldown";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;
            
            int cooldown = EnchantmentModuleConfig.LifeSurge.baseCooldown - 
                (lifeSurgeLevel - 1) * EnchantmentModuleConfig.LifeSurge.cooldownDecreasePerLevel;
            setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50));
            
            double armorBoost = EnchantmentModuleConfig.LifeSurge.flatArmorPerLevel * lifeSurgeLevel;
            double armorPercentBoost = EnchantmentModuleConfig.LifeSurge.percentArmorPerLevel * lifeSurgeLevel;
            
            applyAttributeBoost(player, Attributes.ARMOR, armorBoost, 
                EnchantmentModuleConfig.LifeSurge.duration);
            applyAttributeBoost(player, Attributes.ARMOR, 
                player.getAttribute(Attributes.ARMOR).getBaseValue() * (armorPercentBoost / 100.0), 
                EnchantmentModuleConfig.LifeSurge.duration);
            
            double lifestealBoost = EnchantmentModuleConfig.LifeSurge.flatLifestealPerLevel * lifeSurgeLevel;
            double spellStealBoost = EnchantmentModuleConfig.LifeSurge.flatSpellStealPerLevel * lifeSurgeLevel;
            
            applyAttributeBoost(player, getLifestealAttribute(), lifestealBoost / 100.0, 
                EnchantmentModuleConfig.LifeSurge.duration);
            applyAttributeBoost(player, getSpellLeechAttribute(), spellStealBoost / 100.0, 
                EnchantmentModuleConfig.LifeSurge.duration);
        }
    }
    
    private static void handleInvigoratingDefensesTrigger(Player player, LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.InvigoratingDefenses.enabled) return;
        
        ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        int invigoratingLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.INVIGORATING_DEFENSES.get(), chestplate);
            
        if (invigoratingLevel > 0 && player.isBlocking()) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "invigorating_defenses_cooldown";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;
            
            setCooldown(playerId, cooldownKey, currentTime + (EnchantmentModuleConfig.InvigoratingDefenses.cooldown * 50));
            
            double speedBoost = EnchantmentModuleConfig.InvigoratingDefenses.baseMovementSpeedBoost + 
                (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.speedBoostPerLevel;
            int duration = EnchantmentModuleConfig.InvigoratingDefenses.baseDuration + 
                (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.durationPerLevel;
            
            applyAttributeBoost(player, Attributes.MOVEMENT_SPEED, speedBoost / 100.0, duration);
            
            double healthToRestore = EnchantmentModuleConfig.InvigoratingDefenses.baseHealthRestored + 
                (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.healthRestoredPerLevel;
            
            setEnchantmentData(playerId, "invigorating_healing", healthToRestore);
            setEnchantmentData(playerId, "invigorating_duration", (double) duration);
        }
    }
    
    private static void handleWeaponEnchantmentAttacks(Player attacker, Entity target, DamageSource source) {
        ItemStack weapon = attacker.getMainHandItem();
        
        if (target instanceof LivingEntity livingTarget) {
            handleHunterMarking(attacker, livingTarget, weapon);
            handleHunterDamageBonus(attacker, livingTarget, weapon);
            handleSpellbladePassiveA(attacker, weapon, source);
            
            if (hasEnchantmentFlag(attacker.getUUID(), "reprisal_active")) {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                livingTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 2));
                livingTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 2));
            }
        }
    }
    
    private static void handleHunterMarking(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;
        
        int hunterLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.HUNTER.get(), weapon);
            
        if (hunterLevel > 0) {
            UUID attackerId = attacker.getUUID();
            UUID targetId = target.getUUID();
            
            if (!hunterMarks.containsValue(attackerId)) {
                hunterMarks.put(targetId, attackerId);
                target.getPersistentData().putString("hunter_marked_by", attackerId.toString());
            }
        }
    }
    
    private static void handleHunterDamageBonus(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;
        
        int hunterLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.HUNTER.get(), weapon);
            
        if (hunterLevel > 0) {
            UUID attackerId = attacker.getUUID();
            String markedBy = target.getPersistentData().getString("hunter_marked_by");
            
            if (attackerId.toString().equals(markedBy)) {
                double critDamageBoost = EnchantmentModuleConfig.Hunter.baseCritDamageBoost + 
                    (hunterLevel - 1) * EnchantmentModuleConfig.Hunter.critDamagePerLevel;
                double critChanceBoost = EnchantmentModuleConfig.Hunter.flatCritChance;
                
                applyAttributeBoost(attacker, getCritDamageAttribute(), critDamageBoost, 1);
                applyAttributeBoost(attacker, getCritChanceAttribute(), critChanceBoost, 1);
            }
        }
    }
    
    private static void handleHunterMarkRemoval(LivingEntity deadEntity) {
        UUID deadId = deadEntity.getUUID();
        if (hunterMarks.containsKey(deadId)) {
            hunterMarks.remove(deadId);
            deadEntity.getPersistentData().remove("hunter_marked_by");
        }
    }
    
    private static void handleSpellbladePassiveA(Player attacker, ItemStack weapon, DamageSource source) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;
        
        int spellbladeLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.SPELLBLADE.get(), weapon);
            
        if (spellbladeLevel > 0) {
            UUID attackerId = attacker.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "spellblade_passive_a_cooldown";
            if (isOnCooldown(attackerId, cooldownKey, currentTime)) return;
            
            boolean isMeleeAttack = !source.isIndirect() && source.getDirectEntity() == attacker;
            boolean isRangedAttack = source.isIndirect() || 
                weapon.getItem() instanceof BowItem || 
                weapon.getItem() instanceof CrossbowItem || 
                weapon.getItem() instanceof TridentItem;
            
            if (isMeleeAttack || isRangedAttack) {
                int cooldown = EnchantmentModuleConfig.Spellblade.PassiveA.baseCooldown - 
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.cooldownDecreasePerLevel;
                setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50));
                
                double spellPowerBoost;
                if (isMeleeAttack) {
                    spellPowerBoost = EnchantmentModuleConfig.Spellblade.PassiveA.baseMeleeSpellPowerBoost + 
                        (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.meleeBoostPerLevel;
                } else {
                    spellPowerBoost = EnchantmentModuleConfig.Spellblade.PassiveA.baseRangedSpellPowerBoost + 
                        (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.rangedBoostPerLevel;
                }
                
                applyAttributeBoost(attacker, getSpellPowerAttribute(), spellPowerBoost / 100.0, 
                    EnchantmentModuleConfig.Spellblade.PassiveA.duration);
            }
        }
    }
    
    private static void updateMomentumDamage(Player player, int stacks, int level) {
        double totalDamageBoost = stacks * (EnchantmentModuleConfig.Momentum.damageBoostPerStack + 
            (level - 1) * EnchantmentModuleConfig.Momentum.damageBoostPerLevel);
        
        applyDamageAmplifier(player, totalDamageBoost, EnchantmentModuleConfig.Momentum.stackDuration);
    }
    
    private static void applyDamageAmplifier(Player player, double amount, int duration) {
        AttributeInstance damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            AttributeModifier damageMod = new AttributeModifier(
                UUID.randomUUID(), "Enchantment Damage Boost", amount, 
                AttributeModifier.Operation.ADDITION);
            damageAttr.addTransientModifier(damageMod);
        }
    }
    
    private static void applyAttributeBoost(Player player, Attribute attribute, double amount, int duration) {
        if (attribute == null) return;
        
        AttributeInstance attrInstance = player.getAttribute(attribute);
        if (attrInstance != null) {
            AttributeModifier modifier = new AttributeModifier(
                UUID.randomUUID(), "Enchantment Boost", amount, 
                AttributeModifier.Operation.ADDITION);
            attrInstance.addTransientModifier(modifier);
        }
    }
    
    private static boolean isOnCooldown(UUID playerId, String cooldownKey, long currentTime) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long cooldownEnd = data.getLong(cooldownKey + "_end");
        return currentTime < cooldownEnd;
    }
    
    private static void setCooldown(UUID playerId, String cooldownKey, long endTime) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putLong(cooldownKey + "_end", endTime);
    }
    
    private static CompoundTag getOrCreateEnchantmentData(UUID playerId) {
        return playerEnchantmentData.computeIfAbsent(playerId, k -> new CompoundTag());
    }
    
    private static void setEnchantmentData(UUID playerId, String key, double value) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putDouble(key, value);
    }
    
    private static void setEnchantmentFlag(UUID playerId, String key, boolean value, int duration) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putBoolean(key, value);
        data.putLong(key + "_expire", System.currentTimeMillis() + (duration * 50));
    }
    
    private static boolean hasEnchantmentFlag(UUID playerId, String key) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long expireTime = data.getLong(key + "_expire");
        if (System.currentTimeMillis() > expireTime) {
            data.remove(key);
            data.remove(key + "_expire");
            return false;
        }
        return data.getBoolean(key);
    }
    
    private static double getPlayerMaxMana(Player player) {
        AttributeInstance manaAttr = player.getAttribute(getMaxManaAttribute());
        return manaAttr != null ? manaAttr.getValue() : 100.0;
    }
    
    private static Attribute getSpellPowerAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", "spell_power"));
    }
    
    private static Attribute getMaxManaAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", "max_mana"));
    }
    
    private static Attribute getCastTimeReductionAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", "cast_time_reduction"));
    }
    
    private static Attribute getArrowDamageAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", "arrow_damage"));
    }
    
    private static Attribute getCritDamageAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", "crit_damage"));
    }
    
    private static Attribute getCritChanceAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", "crit_chance"));
    }
    
    private static Attribute getLifestealAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("bobo_tweaks", "lifesteal"));
    }
    
    private static Attribute getSpellLeechAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("bobo_tweaks", "spell_leech"));
    }
    
    
    private static void handleTeammateKillEffects(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob victim)) return;
        
        Player directKiller = event.getSource().getEntity() instanceof Player ? 
            (Player) event.getSource().getEntity() : null;
            
        if (directKiller == null) return;
        
        Team killerTeam = directKiller.getTeam();
        if (killerTeam == null) return;
        
        for (Player nearbyPlayer : directKiller.level().getEntitiesOfClass(Player.class, 
            directKiller.getBoundingBox().inflate(50))) {
            
            if (nearbyPlayer.equals(directKiller)) continue;

            Team nearbyTeam = nearbyPlayer.getTeam();
            if (nearbyTeam != null && nearbyTeam.equals(killerTeam)) {
                handleMomentumTrigger(nearbyPlayer, victim, false);
                handleShadowWalkerTrigger(nearbyPlayer, false);
            }
        }
    }
    
    private static void handleShadowWalkerInvisibilityDamage(Player attacker) {
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;
        
        ItemStack chestplate = attacker.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        int shadowLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.SHADOW_WALKER.get(), chestplate);
            
        if (shadowLevel > 0) {
            boolean hasInvisibility = attacker.hasEffect(MobEffects.INVISIBILITY);
            
            MobEffect trueInvisEffect = ForgeRegistries.MOB_EFFECTS.getValue(
                new ResourceLocation("irons_spellbooks", "true_invisibility"));
            boolean hasTrueInvisibility = trueInvisEffect != null && attacker.hasEffect(trueInvisEffect);
            
            if (hasInvisibility || hasTrueInvisibility) {
                double damageBoost = EnchantmentModuleConfig.ShadowWalker.baseDamageAmplifier + 
                    (shadowLevel - 1) * EnchantmentModuleConfig.ShadowWalker.damageAmplifierPerLevel;
                
                applyDamageAmplifier(attacker, damageBoost, 1);
            }
        }
    }
    
    private static void handlePeriodicEffects(Player player) {
        handleMagicalAttunementPeriodic(player);
    }
    
    private static void handleMagicalAttunementPeriodic(Player player) {
        if (!EnchantmentModuleConfig.MagicalAttunement.enabled) return;
        
        ItemStack weapon = player.getMainHandItem();
        int attunementLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get(), weapon);
            
        if (attunementLevel > 0) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "magical_attunement_periodic";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;
            
            int cooldown = EnchantmentModuleConfig.MagicalAttunement.baseCooldown - 
                (attunementLevel - 1) * EnchantmentModuleConfig.MagicalAttunement.cooldownDecreasePerLevel;
            setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50));
            
            setEnchantmentFlag(playerId, "magical_attunement_ready", true, 
                EnchantmentModuleConfig.MagicalAttunement.duration);
        }
    }
    
    private static void handleMagicalAttunementDamage(Player attacker, LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.MagicalAttunement.enabled) return;
        
        UUID attackerId = attacker.getUUID();
        if (!hasEnchantmentFlag(attackerId, "magical_attunement_ready")) return;
        
        ItemStack weapon = attacker.getMainHandItem();
        int attunementLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get(), weapon);
            
        if (attunementLevel > 0) {
            double baseDamage = EnchantmentModuleConfig.MagicalAttunement.baseDamagePerLevel * attunementLevel;
            double manaBonus = getPlayerMaxMana(attacker) * EnchantmentModuleConfig.MagicalAttunement.maxManaPercent;
            double totalDamage = baseDamage + manaBonus;

            event.getEntity().hurt(attacker.damageSources().lightningBolt(), (float) totalDamage);
            
            setEnchantmentFlag(attackerId, "magical_attunement_ready", false, 0);
        }
    }
    
    private static void handleSpellbladeSpellCast(Player caster) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;
        
        ItemStack weapon = caster.getMainHandItem();
        int spellbladeLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.SPELLBLADE.get(), weapon);
            
        if (spellbladeLevel > 0) {
            UUID casterId = caster.getUUID();
            long currentTime = System.currentTimeMillis();
            
            String cooldownKey = "spellblade_passive_b_cooldown";
            if (isOnCooldown(casterId, cooldownKey, currentTime)) return;
            
            int cooldown = EnchantmentModuleConfig.Spellblade.PassiveB.baseCooldown - 
                (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.cooldownDecreasePerLevel;
            setCooldown(casterId, cooldownKey, currentTime + (cooldown * 50));
            
            double arrowDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseArrowDamageBoost + 
                (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.arrowBoostPerLevel;
            double attackDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseAttackDamageBoost + 
                (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.attackBoostPerLevel;
            
            applyAttributeBoost(caster, getArrowDamageAttribute(), arrowDamageBoost, 
                EnchantmentModuleConfig.Spellblade.PassiveB.duration);
            applyAttributeBoost(caster, Attributes.ATTACK_DAMAGE, attackDamageBoost, 
                EnchantmentModuleConfig.Spellblade.PassiveB.duration);
        }
    }
    
    private static void handleMomentumExpiry(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long expireTime = data.getLong("momentum_expire_time");
        
        if (System.currentTimeMillis() > expireTime && data.getInt("momentum_stacks") > 0) {
            data.putInt("momentum_stacks", 0);
            data.remove("momentum_expire_time");
            removeAttributeModifiersContaining(player, "momentum");
        }
    }
    
    private static void handleInvigoratingDefensesHealing(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        
        if (data.contains("invigorating_healing")) {
            double healAmount = data.getDouble("invigorating_healing");
            double duration = data.getDouble("invigorating_duration");
            long lastHealTime = data.getLong("invigorating_last_heal");
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastHealTime >= 1000) {
                double percentHealing = healAmount / 100.0;
                float healingAmount = (float) (player.getMaxHealth() * percentHealing);
                player.heal(healingAmount);
                
                data.putLong("invigorating_last_heal", currentTime);
                
                duration -= 1000;
                if (duration <= 0) {
                    data.remove("invigorating_healing");
                    data.remove("invigorating_duration");
                    data.remove("invigorating_last_heal");
                } else {
                    data.putDouble("invigorating_duration", duration);
                }
            }
        }
    }
    
    private static void handleMultiscaleContinuous(Player player) {
        if (!EnchantmentModuleConfig.Multiscale.enabled) return;
        
        ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        int multiscaleLevel = EnchantmentHelper.getItemEnchantmentLevel(
            EnchantmentModuleRegistration.MULTISCALE.get(), chestplate);
            
        boolean isFullHealth = player.getHealth() >= player.getMaxHealth();
        boolean hasMultiscaleEffect = hasAttributeModifier(player, Attributes.ARMOR, "multiscale");
        
        if (multiscaleLevel > 0 && isFullHealth && !hasMultiscaleEffect) {
            double flatArmor = EnchantmentModuleConfig.Multiscale.flatArmorPerLevel * multiscaleLevel;
            double percentArmor = EnchantmentModuleConfig.Multiscale.percentArmorPerLevel * multiscaleLevel;
            
            AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
            if (armorAttr != null) {
                double currentArmor = armorAttr.getBaseValue();
                double percentBonus = currentArmor * (percentArmor / 100.0);
                
                AttributeModifier flatMod = new AttributeModifier(
                    UUID.fromString("multiscale-flat"), "Multiscale Flat Armor", 
                    flatArmor, AttributeModifier.Operation.ADDITION);
                AttributeModifier percentMod = new AttributeModifier(
                    UUID.fromString("multiscale-percent"), "Multiscale Percent Armor", 
                    percentBonus, AttributeModifier.Operation.ADDITION);
                
                armorAttr.addTransientModifier(flatMod);
                armorAttr.addTransientModifier(percentMod);
            }
        } else if ((!isFullHealth || multiscaleLevel == 0) && hasMultiscaleEffect) {
            removeAttributeModifiersContaining(player, "multiscale");
        }
    }
    
    private static boolean hasAttributeModifier(Player player, Attribute attribute, String name) {
        AttributeInstance attrInstance = player.getAttribute(attribute);
        if (attrInstance == null) return false;
        
        return attrInstance.getModifiers().stream()
            .anyMatch(mod -> mod.getName().toLowerCase().contains(name.toLowerCase()));
    }
    
    private static void removeAttributeModifiersContaining(Player player, String name) {
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
            AttributeInstance attrInstance = player.getAttribute(attribute);
            if (attrInstance != null) {
                attrInstance.getModifiers().stream()
                    .filter(mod -> mod.getName().toLowerCase().contains(name.toLowerCase()))
                    .forEach(attrInstance::removeModifier);
            }
        }
    }
}