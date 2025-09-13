package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentModuleHandler {

    private static final Map<UUID, CompoundTag> playerEnchantmentData = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> hunterMarks = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, Long>> activeModifiers = new ConcurrentHashMap<>();

    private static final UUID REPRISAL_DAMAGE_UUID = UUID.fromString("e7f3c5d2-8a9b-4f3e-b1d4-6c8a9f2e3d5b");
    private static final UUID MOMENTUM_DAMAGE_UUID = UUID.fromString("a3b2c1d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
    private static final UUID MULTISCALE_FLAT_UUID = UUID.fromString("b4d5e6f7-8a9b-1c2d-3e4f-5a6b7c8d9e0f");
    private static final UUID MULTISCALE_PERCENT_UUID = UUID.fromString("c5e6f7a8-9b0c-1d2e-3f4a-5b6c7d8e9f0a");
    private static final UUID SHADOW_WALKER_SPEED_UUID = UUID.fromString("d6f7a8b9-0c1d-2e3f-4a5b-6c7d8e9f0a1b");
    private static final UUID PERFECTIONIST_ATTACK_SPEED_UUID = UUID.fromString("e7a8b9c0-1d2e-3f4a-5b6c-7d8e9f0a1b2c");
    private static final UUID PERFECTIONIST_CAST_SPEED_UUID = UUID.fromString("f8b9c0d1-2e3f-4a5b-6c7d-8e9f0a1b2c3d");
    private static final UUID INVIGORATING_SPEED_UUID = UUID.fromString("a9c0d1e2-3f4a-5b6c-7d8e-9f0a1b2c3d4e");
    private static final UUID LIFE_SURGE_ARMOR_FLAT_UUID = UUID.fromString("b0d1e2f3-4a5b-6c7d-8e9f-0a1b2c3d4e5f");
    private static final UUID LIFE_SURGE_ARMOR_PERCENT_UUID = UUID.fromString("c1e2f3a4-5b6c-7d8e-9f0a-1b2c3d4e5f6a");
    private static final UUID LIFE_SURGE_LIFESTEAL_UUID = UUID.fromString("d2f3a4b5-6c7d-8e9f-0a1b-2c3d4e5f6a7b");
    private static final UUID LIFE_SURGE_SPELL_LEECH_UUID = UUID.fromString("e3a4b5c6-7d8e-9f0a-1b2c-3d4e5f6a7b8c");
    private static final UUID SPELLBLADE_A_SPELL_POWER_UUID = UUID.fromString("f4b5c6d7-8e9f-0a1b-2c3d-4e5f6a7b8c9d");
    private static final UUID SPELLBLADE_B_ARROW_UUID = UUID.fromString("a5c6d7e8-9f0a-1b2c-3d4e-5f6a7b8c9d0e");
    private static final UUID SPELLBLADE_B_ATTACK_UUID = UUID.fromString("b6d7e8f9-0a1b-2c3d-4e5f-6a7b8c9d0e1f");
    private static final UUID HUNTER_CRIT_DAMAGE_UUID = UUID.fromString("c7e8f9a0-1b2c-3d4e-5f6a-7b8c9d0e1f2a");
    private static final UUID HUNTER_CRIT_CHANCE_UUID = UUID.fromString("d8f9a0b1-2c3d-4e5f-6a7b-8c9d0e1f2a3b");
    private static final UUID SHADOW_WALKER_DAMAGE_UUID = UUID.fromString("e8f9a0b1-2c3d-4e5f-6a7b-8c9d0e1f2a3c");

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
            handleMagicalAttunementDamage(attacker, event);
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;

        if (event.getEntity() instanceof Player player) {
            handleInvigoratingDefensesTrigger(player);
        }
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
    public static void onSpellCast(SpellOnCastEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;

        handleSpellbladeSpellCast(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule) return;
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;

        cleanupExpiredModifiers(player);

        handleMomentumExpiry(player);

        handleInvigoratingDefensesHealing(player);

        handleMultiscaleContinuous(player);

        handlePeriodicEffects(player);
        
        handleHunterEffect(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        playerEnchantmentData.remove(playerId);
        activeModifiers.remove(playerId);
        hunterMarks.values().removeIf(markerId -> markerId.equals(playerId));

        cleanupAllModifiers(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        cleanupAllModifiers(event.getEntity());
    }

    public static void triggerPerfectionist(Player player) {
        if (!EnchantmentModuleConfig.Perfectionist.enabled) return;

        int perfectionistLevel = getEnchantmentLevelFromCategory(player,
                EnchantmentModuleRegistration.PERFECTIONIST.get(), EnchantmentModuleConfig.Perfectionist.category);

        if (perfectionistLevel > 0) {
            double attackSpeedBoost = EnchantmentModuleConfig.Perfectionist.baseAttackSpeedBoost +
                    (perfectionistLevel - 1) * EnchantmentModuleConfig.Perfectionist.attackSpeedPerLevel;
            double castSpeedBoost = EnchantmentModuleConfig.Perfectionist.baseCastSpeedBoost +
                    (perfectionistLevel - 1) * EnchantmentModuleConfig.Perfectionist.castSpeedPerLevel;

            applyTimedModifier(player, Attributes.ATTACK_SPEED, PERFECTIONIST_ATTACK_SPEED_UUID,
                    "Perfectionist Attack Speed", attackSpeedBoost / 100.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Perfectionist.duration);

            Attribute castTimeReduction = getCastTimeReductionAttribute();
            if (castTimeReduction != null) {
                applyTimedModifier(player, castTimeReduction, PERFECTIONIST_CAST_SPEED_UUID,
                        "Perfectionist Cast Speed", castSpeedBoost,
                        AttributeModifier.Operation.ADDITION,
                        EnchantmentModuleConfig.Perfectionist.duration);
            }
        }
    }

    private static void handleReprisalTrigger(Player player, DamageSource source) {
        if (!EnchantmentModuleConfig.Reprisal.enabled) return;

        int reprisalLevel = getEnchantmentLevelFromCategory(player, EnchantmentModuleRegistration.REPRISAL.get(), EnchantmentModuleConfig.Reprisal.category);

        if (reprisalLevel > 0 && source.getEntity() instanceof Mob) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "reprisal_cooldown";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.Reprisal.baseCooldown -
                    (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.cooldownReductionPerLevel;
            setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50L));

            double damageBoost = EnchantmentModuleConfig.Reprisal.baseDamageBoost +
                    (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.damageBoostPerLevel;
            int duration = EnchantmentModuleConfig.Reprisal.baseDuration +
                    (reprisalLevel - 1) * EnchantmentModuleConfig.Reprisal.durationPerLevel;

            applyTimedModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), REPRISAL_DAMAGE_UUID,
                    "Reprisal Damage", damageBoost / 100.0, AttributeModifier.Operation.ADDITION, duration);

            setEnchantmentFlag(playerId, "reprisal_active", true, duration);
        }
    }

    private static void handleMomentumTrigger(Player killer, LivingEntity victim, boolean isDirectKill) {
        if (!EnchantmentModuleConfig.Momentum.enabled) return;

        int momentumLevel = getEnchantmentLevelFromCategory(killer,
                EnchantmentModuleRegistration.MOMENTUM.get(), EnchantmentModuleConfig.Momentum.category);

        if (momentumLevel > 0 && (victim instanceof Mob)) {
            UUID killerId = killer.getUUID();

            int stacksToAdd = isDirectKill ?
                    EnchantmentModuleConfig.Momentum.killStacks :
                    EnchantmentModuleConfig.Momentum.allyKillStacks;

            CompoundTag data = getOrCreateEnchantmentData(killerId);
            int currentStacks = data.getInt("momentum_stacks");
            int maxStacks = EnchantmentModuleConfig.Momentum.baseMaxStacks +
                    (momentumLevel - 1) * EnchantmentModuleConfig.Momentum.maxStackIncreasePerLevel;

            data.putLong("momentum_expire_time",
                    System.currentTimeMillis() + (EnchantmentModuleConfig.Momentum.stackDuration * 50L));

            if (currentStacks < maxStacks) {
                int newStacks = Math.min(currentStacks + stacksToAdd, maxStacks);
                data.putInt("momentum_stacks", newStacks);
                updateMomentumDamage(killer, newStacks, momentumLevel);
            }
        }
    }

    private static void handleShadowWalkerTrigger(Player killer, boolean isDirectKill) {
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;

        int shadowLevel = getEnchantmentLevelFromCategory(killer,
                EnchantmentModuleRegistration.SHADOW_WALKER.get(), EnchantmentModuleConfig.ShadowWalker.category);

        if (shadowLevel > 0) {
            if (isDirectKill) {
                MobEffect trueInvis = MobEffectRegistry.TRUE_INVISIBILITY.get();
                if (trueInvis != null) {
                    killer.addEffect(new MobEffectInstance(trueInvis, EnchantmentModuleConfig.ShadowWalker.invisibilityDuration, 0));
                }
            }

            applyTimedModifier(killer, Attributes.MOVEMENT_SPEED, SHADOW_WALKER_SPEED_UUID,
                    "Shadow Walker Speed",
                    EnchantmentModuleConfig.ShadowWalker.movementSpeedPercent / 100.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.ShadowWalker.movementSpeedDuration);
        }
    }

    private static void handleSpellbladePassiveA(Player attacker, DamageSource source) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;

        ItemStack weapon = getEnchantedItemStackFromCategory(attacker,
                EnchantmentModuleRegistration.SPELLBLADE.get(), EnchantmentModuleConfig.Spellblade.category);
        int spellbladeLevel = EnchantmentHelper.getItemEnchantmentLevel(
                EnchantmentModuleRegistration.SPELLBLADE.get(), weapon);

        if (spellbladeLevel > 0) {
            UUID attackerId = attacker.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "spellblade_passive_a_cooldown";
            if (isOnCooldown(attackerId, cooldownKey, currentTime)) return;

            boolean isMeleeAttack = !source.isIndirect() && source.getDirectEntity() == attacker;
            boolean isRangedAttack = source.isIndirect() ||
                    source.getDirectEntity() instanceof AbstractArrow ||
                    weapon.getItem() instanceof BowItem ||
                    weapon.getItem() instanceof CrossbowItem ||
                    weapon.getItem() instanceof TridentItem;

            if (isMeleeAttack || isRangedAttack) {
                int cooldown = EnchantmentModuleConfig.Spellblade.PassiveA.baseCooldown -
                        (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.cooldownDecreasePerLevel;
                setCooldown(attackerId, cooldownKey, currentTime + (cooldown * 50L));

                double spellPowerBoost;
                if (isMeleeAttack) {
                    spellPowerBoost = EnchantmentModuleConfig.Spellblade.PassiveA.baseMeleeSpellPowerBoost +
                            (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.meleeBoostPerLevel;
                } else {
                    spellPowerBoost = EnchantmentModuleConfig.Spellblade.PassiveA.baseRangedSpellPowerBoost +
                            (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveA.rangedBoostPerLevel;
                }

                Attribute spellPower = getSpellPowerAttribute();
                if (spellPower != null) {
                    applyTimedModifier(attacker, spellPower, SPELLBLADE_A_SPELL_POWER_UUID,
                            "Spellblade Spell Power", spellPowerBoost,
                            AttributeModifier.Operation.ADDITION,
                            EnchantmentModuleConfig.Spellblade.PassiveA.duration);
                }
            }
        }
    }

    private static void handleSpellbladeSpellCast(Player caster) {
        if (!EnchantmentModuleConfig.Spellblade.enabled) return;

        int spellbladeLevel = getEnchantmentLevelFromCategory(caster,
                EnchantmentModuleRegistration.SPELLBLADE.get(), EnchantmentModuleConfig.Spellblade.category);

        if (spellbladeLevel > 0) {
            UUID casterId = caster.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "spellblade_passive_b_cooldown";
            if (isOnCooldown(casterId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.Spellblade.PassiveB.baseCooldown -
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.cooldownDecreasePerLevel;
            setCooldown(casterId, cooldownKey, currentTime + (cooldown * 50L));

            double arrowDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseArrowDamageBoost +
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.arrowBoostPerLevel;
            double attackDamageBoost = EnchantmentModuleConfig.Spellblade.PassiveB.baseAttackDamageBoost +
                    (spellbladeLevel - 1) * EnchantmentModuleConfig.Spellblade.PassiveB.attackBoostPerLevel;

            applyTimedModifier(caster, ALObjects.Attributes.ARROW_DAMAGE.get(), SPELLBLADE_B_ARROW_UUID,
                    "Spellblade Arrow Damage", arrowDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Spellblade.PassiveB.duration);

            applyTimedModifier(caster, Attributes.ATTACK_DAMAGE, SPELLBLADE_B_ATTACK_UUID,
                    "Spellblade Attack Damage", attackDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    EnchantmentModuleConfig.Spellblade.PassiveB.duration);
        }
    }

    private static void handleLifeSurgeTrigger(Player player) {
        if (!EnchantmentModuleConfig.LifeSurge.enabled) return;

        int lifeSurgeLevel = getEnchantmentLevelFromCategory(player,
                EnchantmentModuleRegistration.LIFE_SURGE.get(), EnchantmentModuleConfig.LifeSurge.category);

        if (lifeSurgeLevel > 0 &&
                player.getHealth() / player.getMaxHealth() <= EnchantmentModuleConfig.LifeSurge.healthThreshold) {

            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "life_surge_cooldown";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.LifeSurge.baseCooldown -
                    (lifeSurgeLevel - 1) * EnchantmentModuleConfig.LifeSurge.cooldownDecreasePerLevel;
            setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50L));

            double flatArmor = EnchantmentModuleConfig.LifeSurge.flatArmorPerLevel * lifeSurgeLevel;
            double percentArmor = EnchantmentModuleConfig.LifeSurge.percentArmorPerLevel * lifeSurgeLevel;

            applyTimedModifier(player, Attributes.ARMOR, LIFE_SURGE_ARMOR_FLAT_UUID,
                    "Life Surge Flat Armor", flatArmor, AttributeModifier.Operation.ADDITION,
                    EnchantmentModuleConfig.LifeSurge.duration);

            AttributeInstance armorInstance = player.getAttribute(Attributes.ARMOR);
            if (armorInstance != null) {
                double currentArmor = armorInstance.getBaseValue();
                applyTimedModifier(player, Attributes.ARMOR, LIFE_SURGE_ARMOR_PERCENT_UUID,
                        "Life Surge Percent Armor", currentArmor * (percentArmor / 100.0),
                        AttributeModifier.Operation.ADDITION,
                        EnchantmentModuleConfig.LifeSurge.duration);
            }

            double lifestealBoost = EnchantmentModuleConfig.LifeSurge.flatLifestealPerLevel * lifeSurgeLevel;
            double spellLeechBoost = EnchantmentModuleConfig.LifeSurge.flatSpellStealPerLevel * lifeSurgeLevel;

            applyTimedModifier(player, ALObjects.Attributes.LIFE_STEAL.get(), LIFE_SURGE_LIFESTEAL_UUID,
                    "Life Surge Lifesteal", lifestealBoost / 100.0, AttributeModifier.Operation.ADDITION,
                    EnchantmentModuleConfig.LifeSurge.duration);

            applyTimedModifier(player, ModAttributes.SPELL_LEECH.get(), LIFE_SURGE_SPELL_LEECH_UUID,
                    "Life Surge Spell Leech", spellLeechBoost / 100.0, AttributeModifier.Operation.ADDITION,
                    EnchantmentModuleConfig.LifeSurge.duration);
        }
    }

    private static void handleInvigoratingDefensesTrigger(Player player) {
        if (!EnchantmentModuleConfig.InvigoratingDefenses.enabled) return;

        int invigoratingLevel = getEnchantmentLevelFromCategory(player,
                EnchantmentModuleRegistration.INVIGORATING_DEFENSES.get(), EnchantmentModuleConfig.InvigoratingDefenses.category);

        if (invigoratingLevel > 0) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "invigorating_defenses_cooldown";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;

            setCooldown(playerId, cooldownKey, currentTime + (EnchantmentModuleConfig.InvigoratingDefenses.cooldown * 50L));

            double speedBoost = EnchantmentModuleConfig.InvigoratingDefenses.baseMovementSpeedBoost +
                    (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.speedBoostPerLevel;
            int duration = EnchantmentModuleConfig.InvigoratingDefenses.baseDuration +
                    (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.durationPerLevel;

            applyTimedModifier(player, Attributes.MOVEMENT_SPEED, INVIGORATING_SPEED_UUID,
                    "Invigorating Speed", speedBoost / 100.0, AttributeModifier.Operation.MULTIPLY_TOTAL,
                    duration);

            double healthToRestore = EnchantmentModuleConfig.InvigoratingDefenses.baseHealthRestored +
                    (invigoratingLevel - 1) * EnchantmentModuleConfig.InvigoratingDefenses.healthRestoredPerLevel;

            CompoundTag data = getOrCreateEnchantmentData(playerId);
            data.putDouble("invigorating_healing", healthToRestore);
            data.putLong("invigorating_healing_end", System.currentTimeMillis() + (duration * 50L));
            data.putLong("invigorating_last_heal", System.currentTimeMillis());
        }
    }

    private static void handleWeaponEnchantmentAttacks(Player attacker, Entity target, DamageSource source) {
        if (target instanceof LivingEntity livingTarget) {
            ItemStack hunterWeapon = getEnchantedItemStackFromCategory(attacker,
                    EnchantmentModuleRegistration.HUNTER.get(), EnchantmentModuleConfig.Hunter.category);
            handleHunterMarking(attacker, livingTarget, hunterWeapon);
            handleHunterDamageBonus(attacker, livingTarget, hunterWeapon);

            handleSpellbladePassiveA(attacker, source);
            handleShadowWalkerInvisibilityDamage(attacker);

            if (hasEnchantmentFlag(attacker.getUUID(), "reprisal_active")) {
                if (!attacker.level().isClientSide()) {
                    attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.ELDER_GUARDIAN_HURT_LAND, 
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    attacker.sendSystemMessage(Component.literal("Reprisal: +15% Damage!"));
                }
            }
        }
    }

    private static void handleHunterMarking(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;

        int hunterLevel = EnchantmentHelper.getItemEnchantmentLevel(
                EnchantmentModuleRegistration.HUNTER.get(), weapon);

        if (hunterLevel > 0 && attacker.hasEffect(ModPotions.HUNTER.get())) {
            UUID attackerId = attacker.getUUID();
            UUID targetId = target.getUUID();

            UUID currentMarkId = hunterMarks.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(attackerId))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            if (currentMarkId == null || currentMarkId.equals(targetId)) {
                hunterMarks.put(targetId, attackerId);
                
                int markDuration;
                if (EnchantmentModuleConfig.Hunter.markDuration < 0) {
                    markDuration = 20 * 60 * 60 * 24;
                } else {
                    markDuration = EnchantmentModuleConfig.Hunter.markDuration * 20;
                }
                target.addEffect(new MobEffectInstance(ModPotions.MARKED.get(), markDuration, 0, false, true));
            }
        }
    }

    private static void handleHunterDamageBonus(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;

        int hunterLevel = EnchantmentHelper.getItemEnchantmentLevel(
                EnchantmentModuleRegistration.HUNTER.get(), weapon);

        if (hunterLevel > 0 && attacker.hasEffect(ModPotions.HUNTER.get()) && 
                target.hasEffect(ModPotions.MARKED.get())) {
            
            UUID attackerId = attacker.getUUID();
            UUID markedBy = hunterMarks.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(target.getUUID()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);

            if (attackerId.equals(markedBy)) {
                double critDamageBoost = EnchantmentModuleConfig.Hunter.baseCritDamageBoost +
                        (hunterLevel - 1) * EnchantmentModuleConfig.Hunter.critDamagePerLevel;
                double critChanceBoost = EnchantmentModuleConfig.Hunter.flatCritChance;

                applyTimedModifier(attacker, ALObjects.Attributes.CRIT_DAMAGE.get(), HUNTER_CRIT_DAMAGE_UUID,
                        "Hunter Crit Damage", critDamageBoost / 100.0, AttributeModifier.Operation.MULTIPLY_BASE, 10);

                applyTimedModifier(attacker, ALObjects.Attributes.CRIT_CHANCE.get(), HUNTER_CRIT_CHANCE_UUID,
                        "Hunter Crit Chance", critChanceBoost / 100.0, AttributeModifier.Operation.ADDITION, 10);
            }
        }
    }

    private static void handleHunterMarkRemoval(LivingEntity deadEntity) {
        UUID deadId = deadEntity.getUUID();
        hunterMarks.remove(deadId);
        deadEntity.removeEffect(ModPotions.MARKED.get());
    }

    private static void handleShadowWalkerInvisibilityDamage(Player attacker) {
        if (!EnchantmentModuleConfig.ShadowWalker.enabled) return;

        int shadowLevel = getEnchantmentLevelFromCategory(attacker,
                EnchantmentModuleRegistration.SHADOW_WALKER.get(), EnchantmentModuleConfig.ShadowWalker.category);

        if (shadowLevel > 0) {
            boolean hasInvisibility = attacker.hasEffect(MobEffects.INVISIBILITY);
            MobEffect trueInvisEffect = MobEffectRegistry.TRUE_INVISIBILITY.get();
            boolean hasTrueInvisibility = trueInvisEffect != null && attacker.hasEffect(trueInvisEffect);

            if (hasInvisibility || hasTrueInvisibility) {
                double damageBoost = EnchantmentModuleConfig.ShadowWalker.baseDamageAmplifier +
                        (shadowLevel - 1) * EnchantmentModuleConfig.ShadowWalker.damageAmplifierPerLevel;

                applyTimedModifier(attacker, ModAttributes.DAMAGE_AMPLIFIER.get(),
                        SHADOW_WALKER_DAMAGE_UUID, "Shadow Walker Damage", damageBoost / 100.0,
                        AttributeModifier.Operation.ADDITION, 1);
            }
        }
    }

    private static void handlePeriodicEffects(Player player) {
        handleMagicalAttunementPeriodic(player);
    }
    
    private static void handleHunterEffect(Player player) {
        if (!EnchantmentModuleConfig.Hunter.enabled) return;
        
        int hunterLevel = getEnchantmentLevelFromCategory(player,
                EnchantmentModuleRegistration.HUNTER.get(), EnchantmentModuleConfig.Hunter.category);
        
        if (hunterLevel > 0) {
            if (!player.hasEffect(ModPotions.HUNTER.get()) || 
                    (player.getEffect(ModPotions.HUNTER.get()) != null && 
                     player.getEffect(ModPotions.HUNTER.get()).getDuration() < 100)) {
                player.addEffect(new MobEffectInstance(ModPotions.HUNTER.get(), 200, 0, false, false));
            }
        } else {
            if (player.hasEffect(ModPotions.HUNTER.get())) {
                player.removeEffect(ModPotions.HUNTER.get());
                
                UUID playerId = player.getUUID();
                List<UUID> marksToRemove = hunterMarks.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(playerId))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                
                for (UUID markId : marksToRemove) {
                    hunterMarks.remove(markId);
                    for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(100))) {
                        if (entity.getUUID().equals(markId) && entity instanceof LivingEntity le) {
                            le.removeEffect(ModPotions.MARKED.get());
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void handleMagicalAttunementPeriodic(Player player) {
        if (!EnchantmentModuleConfig.MagicalAttunement.enabled) return;

        int attunementLevel = getEnchantmentLevelFromCategory(player,
                EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get(), EnchantmentModuleConfig.MagicalAttunement.category);

        if (attunementLevel > 0) {
            UUID playerId = player.getUUID();
            long currentTime = System.currentTimeMillis();

            String cooldownKey = "magical_attunement_periodic";
            if (isOnCooldown(playerId, cooldownKey, currentTime)) return;

            int cooldown = EnchantmentModuleConfig.MagicalAttunement.baseCooldown -
                    (attunementLevel - 1) * EnchantmentModuleConfig.MagicalAttunement.cooldownDecreasePerLevel;
            setCooldown(playerId, cooldownKey, currentTime + (cooldown * 50L));

            setEnchantmentFlag(playerId, "magical_attunement_ready", true,
                    EnchantmentModuleConfig.MagicalAttunement.duration);
        }
    }

    private static void handleMagicalAttunementDamage(Player attacker, LivingAttackEvent event) {
        if (!EnchantmentModuleConfig.MagicalAttunement.enabled) return;

        UUID attackerId = attacker.getUUID();
        if (!hasEnchantmentFlag(attackerId, "magical_attunement_ready")) return;

        int attunementLevel = getEnchantmentLevelFromCategory(attacker,
                EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get(), EnchantmentModuleConfig.MagicalAttunement.category);

        if (attunementLevel > 0) {
            double baseDamage = EnchantmentModuleConfig.MagicalAttunement.baseDamagePerLevel * attunementLevel;
            double manaBonus = getPlayerMaxMana(attacker) * EnchantmentModuleConfig.MagicalAttunement.maxManaPercent;
            final float lightningDamage = (float)(baseDamage + manaBonus);

            if (attacker.level().getServer() != null) {
                attacker.level().getServer().execute(() -> {
                    if (event.getEntity().isAlive()) {
                        event.getEntity().hurt(attacker.damageSources().lightningBolt(), lightningDamage);
                    }
                });
            }

            CompoundTag data = getOrCreateEnchantmentData(attackerId);
            data.remove("magical_attunement_ready");
            data.remove("magical_attunement_ready_expire");
        }
    }

    private static void updateMomentumDamage(Player player, int stacks, int level) {
        double totalDamageBoost = stacks * (EnchantmentModuleConfig.Momentum.damageBoostPerStack +
                (level - 1) * EnchantmentModuleConfig.Momentum.damageBoostPerLevel) / 100.0;

        AttributeInstance damageAttr = player.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
        if (damageAttr != null) {
            damageAttr.removeModifier(MOMENTUM_DAMAGE_UUID);
            if (stacks > 0) {
                AttributeModifier damageMod = new AttributeModifier(
                        MOMENTUM_DAMAGE_UUID, "Momentum Damage", totalDamageBoost,
                        AttributeModifier.Operation.ADDITION);
                damageAttr.addTransientModifier(damageMod);

                trackModifier(player.getUUID(), "momentum_damage",
                        System.currentTimeMillis() + (EnchantmentModuleConfig.Momentum.stackDuration * 50L));
            }
        }
    }

    private static void handleMomentumExpiry(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long expireTime = data.getLong("momentum_expire_time");

        if (System.currentTimeMillis() > expireTime && data.getInt("momentum_stacks") > 0) {
            data.putInt("momentum_stacks", 0);
            data.remove("momentum_expire_time");

            AttributeInstance damageAttr = player.getAttribute(ModAttributes.DAMAGE_AMPLIFIER.get());
            if (damageAttr != null) {
                damageAttr.removeModifier(MOMENTUM_DAMAGE_UUID);
            }
        }
    }

    private static void handleInvigoratingDefensesHealing(Player player) {
        UUID playerId = player.getUUID();
        CompoundTag data = getOrCreateEnchantmentData(playerId);

        if (data.contains("invigorating_healing")) {
            long healingEnd = data.getLong("invigorating_healing_end");
            long currentTime = System.currentTimeMillis();

            if (currentTime > healingEnd) {
                data.remove("invigorating_healing");
                data.remove("invigorating_healing_end");
                data.remove("invigorating_last_heal");
            } else {
                long lastHealTime = data.getLong("invigorating_last_heal");

                if (currentTime - lastHealTime >= 1000) {
                    double healAmount = data.getDouble("invigorating_healing");
                    double percentHealing = healAmount / 100.0;
                    float healingAmount = (float) (player.getMaxHealth() * percentHealing);
                    player.heal(healingAmount);

                    data.putLong("invigorating_last_heal", currentTime);
                }
            }
        }
    }

    private static void handleMultiscaleContinuous(Player player) {
        if (!EnchantmentModuleConfig.Multiscale.enabled) return;

        int multiscaleLevel = getEnchantmentLevelFromCategory(player,
                EnchantmentModuleRegistration.MULTISCALE.get(), EnchantmentModuleConfig.Multiscale.category);

        boolean isFullHealth = player.getHealth() >= player.getMaxHealth() - 0.01f;
        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);

        if (armorAttr != null) {
            boolean hasMultiscaleFlat = armorAttr.getModifier(MULTISCALE_FLAT_UUID) != null;
            boolean hasMultiscalePercent = armorAttr.getModifier(MULTISCALE_PERCENT_UUID) != null;

            if (multiscaleLevel > 0 && isFullHealth && !hasMultiscaleFlat) {
                double flatArmor = EnchantmentModuleConfig.Multiscale.flatArmorPerLevel * multiscaleLevel;
                double percentArmor = EnchantmentModuleConfig.Multiscale.percentArmorPerLevel * multiscaleLevel;
                double currentArmor = armorAttr.getBaseValue();
                double percentBonus = currentArmor * (percentArmor / 100.0);

                AttributeModifier flatMod = new AttributeModifier(
                        MULTISCALE_FLAT_UUID, "Multiscale Flat Armor",
                        flatArmor, AttributeModifier.Operation.ADDITION);
                AttributeModifier percentMod = new AttributeModifier(
                        MULTISCALE_PERCENT_UUID, "Multiscale Percent Armor",
                        percentBonus, AttributeModifier.Operation.ADDITION);

                armorAttr.addTransientModifier(flatMod);
                armorAttr.addTransientModifier(percentMod);
            } else if ((!isFullHealth || multiscaleLevel == 0) && (hasMultiscaleFlat || hasMultiscalePercent)) {
                armorAttr.removeModifier(MULTISCALE_FLAT_UUID);
                armorAttr.removeModifier(MULTISCALE_PERCENT_UUID);
            }
        }
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

    private static void applyTimedModifier(Player player, Attribute attribute, UUID modifierUUID,
                                           String name, double amount, AttributeModifier.Operation operation,
                                           int durationTicks) {
        if (attribute == null) return;

        AttributeInstance attrInstance = player.getAttribute(attribute);
        if (attrInstance != null) {
            attrInstance.removeModifier(modifierUUID);

            AttributeModifier modifier = new AttributeModifier(modifierUUID, name, amount, operation);
            attrInstance.addTransientModifier(modifier);

            trackModifier(player.getUUID(), modifierUUID.toString(),
                    System.currentTimeMillis() + (durationTicks * 50L));
        }
    }

    private static void trackModifier(UUID playerId, String modifierId, long expireTime) {
        activeModifiers.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(modifierId, expireTime);
    }

    private static void cleanupExpiredModifiers(Player player) {
        UUID playerId = player.getUUID();
        Map<String, Long> modifiers = activeModifiers.get(playerId);
        if (modifiers == null) return;

        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = modifiers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime > entry.getValue()) {
                String modifierId = entry.getKey();

                if (modifierId.equals("momentum_damage")) {
                    removeModifier(player, ModAttributes.DAMAGE_AMPLIFIER.get(), MOMENTUM_DAMAGE_UUID);
                } else {
                    try {
                        UUID uuid = UUID.fromString(modifierId);
                        for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
                            AttributeInstance instance = player.getAttribute(attr);
                            if (instance != null) {
                                instance.removeModifier(uuid);
                            }
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                iterator.remove();
            }
        }
    }

    private static void cleanupAllModifiers(Player player) {
        UUID[] allModifierUUIDs = {
                REPRISAL_DAMAGE_UUID, MOMENTUM_DAMAGE_UUID, MULTISCALE_FLAT_UUID, MULTISCALE_PERCENT_UUID,
                SHADOW_WALKER_SPEED_UUID, PERFECTIONIST_ATTACK_SPEED_UUID, PERFECTIONIST_CAST_SPEED_UUID,
                INVIGORATING_SPEED_UUID, LIFE_SURGE_ARMOR_FLAT_UUID, LIFE_SURGE_ARMOR_PERCENT_UUID,
                LIFE_SURGE_LIFESTEAL_UUID, LIFE_SURGE_SPELL_LEECH_UUID, SPELLBLADE_A_SPELL_POWER_UUID,
                SPELLBLADE_B_ARROW_UUID, SPELLBLADE_B_ATTACK_UUID, HUNTER_CRIT_DAMAGE_UUID, HUNTER_CRIT_CHANCE_UUID,
                SHADOW_WALKER_DAMAGE_UUID
        };

        for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
            AttributeInstance instance = player.getAttribute(attr);
            if (instance != null) {
                for (UUID uuid : allModifierUUIDs) {
                    instance.removeModifier(uuid);
                }
            }
        }
    }

    private static ItemStack getEnchantedItemStackFromCategory(Player player, net.minecraft.world.item.enchantment.Enchantment enchantment, String categoryName) {
        ItemStack enchantedItem = ItemStack.EMPTY;
        int maxLevel = 0;

        switch (categoryName.toUpperCase()) {
            case "ARMOR":
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                        ItemStack itemStack = player.getItemBySlot(slot);
                        int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
                        if (level > maxLevel) {
                            maxLevel = level;
                            enchantedItem = itemStack;
                        }
                    }
                }
                break;
            case "ARMOR_FEET":
                enchantedItem = player.getItemBySlot(EquipmentSlot.FEET);
                break;
            case "ARMOR_LEGS":
                enchantedItem = player.getItemBySlot(EquipmentSlot.LEGS);
                break;
            case "ARMOR_CHEST":
                enchantedItem = player.getItemBySlot(EquipmentSlot.CHEST);
                break;
            case "ARMOR_HEAD":
                enchantedItem = player.getItemBySlot(EquipmentSlot.HEAD);
                break;
            case "WEAPON":
            case "DIGGER":
            case "FISHING_ROD":
            case "TRIDENT":
            case "BOW":
            case "CROSSBOW":
            case "WEAPON_AND_BOW":
                enchantedItem = player.getMainHandItem();
                break;
            case "WEARABLE":
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack itemStack = player.getItemBySlot(slot);
                    int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
                    if (level > maxLevel) {
                        maxLevel = level;
                        enchantedItem = itemStack;
                    }
                }
                break;
            case "BREAKABLE":
            case "VANISHABLE":
                for (ItemStack itemStack : player.getInventory().items) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
                    if (level > maxLevel) {
                        maxLevel = level;
                        enchantedItem = itemStack;
                    }
                }
                break;
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(enchantment, enchantedItem) > 0) {
            return enchantedItem;
        }
        return ItemStack.EMPTY;
    }

    private static int getEnchantmentLevelFromCategory(Player player, net.minecraft.world.item.enchantment.Enchantment enchantment, String categoryName) {
        ItemStack itemStack = getEnchantedItemStackFromCategory(player, enchantment, categoryName);
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
    }

    private static void removeModifier(Player player, Attribute attribute, UUID modifierUUID) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierUUID);
        }
    }

    private static boolean isOnCooldown(UUID playerId, String cooldownKey, long currentTime) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        long cooldownEnd = data.getLong(cooldownKey);
        return currentTime < cooldownEnd;
    }

    private static void setCooldown(UUID playerId, String cooldownKey, long endTime) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putLong(cooldownKey, endTime);
    }

    private static CompoundTag getOrCreateEnchantmentData(UUID playerId) {
        return playerEnchantmentData.computeIfAbsent(playerId, k -> new CompoundTag());
    }

    private static void setEnchantmentFlag(UUID playerId, String key, boolean value, int duration) {
        CompoundTag data = getOrCreateEnchantmentData(playerId);
        data.putBoolean(key, value);
        data.putLong(key + "_expire", System.currentTimeMillis() + (duration * 50L));
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
        Attribute manaAttr = getMaxManaAttribute();
        if (manaAttr != null) {
            AttributeInstance manaInstance = player.getAttribute(manaAttr);
            return manaInstance != null ? manaInstance.getValue() : 100.0;
        }
        return 100.0;
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

}