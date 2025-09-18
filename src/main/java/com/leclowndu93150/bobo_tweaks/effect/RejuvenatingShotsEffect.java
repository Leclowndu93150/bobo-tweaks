package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.registry.ModPotions;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class RejuvenatingShotsEffect extends MobEffect {
    private static final Set<UUID> activeEffectPlayers = new HashSet<>();
    
    public RejuvenatingShotsEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x98FB98);
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            activeEffectPlayers.add(entity.getUUID());
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide()) {
            activeEffectPlayers.remove(entity.getUUID());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        
        if (!(projectile instanceof AbstractArrow arrow)) {
            return;
        }
        
        if (!(arrow.getOwner() instanceof Player shooter)) {
            return;
        }
        
        if (!activeEffectPlayers.contains(shooter.getUUID())) {
            return;
        }
        
        if (!(event.getRayTraceResult().getType() == HitResult.Type.ENTITY)) {
            return;
        }
        
        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHit)) {
            return;
        }
        
        if (!(entityHit.getEntity() instanceof LivingEntity target)) {
            return;
        }
        
        if (target == shooter) {
            return;
        }
        
        boolean isAlly = false;
        if (shooter.getTeam() != null) {
            isAlly = shooter.getTeam().isAlliedTo(target.getTeam());
        }
        
        if (!isAlly && target instanceof Player targetPlayer) {
            if (shooter.getTeam() == null && targetPlayer.getTeam() == null) {
                isAlly = true;
            }
        }
        
        if (isAlly) {
            event.setCanceled(true);
            
            var effectInstance = shooter.getEffect(ModPotions.REJUVENATING_SHOTS.get());
            if (effectInstance == null) {
                return;
            }
            
            int amplifier = effectInstance.getAmplifier();
            
            float critRate = 0.0f;
            float maxMana = 0.0f;
            
            if (ModList.get().isLoaded("attributeslib")) {
                try {
                    var critChanceAttribute = ForgeRegistries.ATTRIBUTES
                        .getValue(new ResourceLocation("attributeslib", "crit_chance"));
                    if (critChanceAttribute != null) {
                        var instance = shooter.getAttribute(critChanceAttribute);
                        if (instance != null) {
                            critRate = (float) instance.getValue();
                        }
                    }
                } catch (Exception e) {
                    BoboTweaks.getLogger().warn("Failed to get crit chance attribute", e);
                }
            }
            
            if (ModList.get().isLoaded("irons_spellbooks")) {
                try {
                    var magicData = MagicData.getPlayerMagicData(shooter);
                    maxMana = (float) shooter.getAttributeValue(AttributeRegistry.MAX_MANA.get());
                    
                    float manaCost = getManaCost(amplifier);
                    float currentMana = magicData.getMana();
                    
                    if (currentMana < manaCost) {
                        return;
                    }
                    
                    magicData.setMana(currentMana - manaCost);
                } catch (Exception e) {
                    BoboTweaks.getLogger().warn("Failed to interact with mana system", e);
                }
            }
            
            float baseHeal = getBaseHeal(amplifier);
            float critBonus = critRate * baseHeal;
            float manaBonus = maxMana * getManaPercentage(amplifier);
            float totalHeal = baseHeal + critBonus + manaBonus;
            
            float healCap = getHealCap(amplifier);
            if (healCap > 0) {
                totalHeal = Math.min(totalHeal, healCap);
            }
            
            target.heal(totalHeal);
            
            if (target.level() instanceof ServerLevel serverLevel) {
                ModNetworking.playSound(serverLevel, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
    
    private static float getBaseHeal(int amplifier) {
        return ModConfig.COMMON.rejuvenatingShotsFlatHeal.get().floatValue() * (amplifier + 1);
    }
    
    private static float getManaPercentage(int amplifier) {
        return ModConfig.COMMON.rejuvenatingShotsManaPercentage.get().floatValue() * (amplifier + 1);
    }
    
    private static float getManaCost(int amplifier) {
        return ModConfig.COMMON.rejuvenatingShotsManaCost.get().floatValue() * (amplifier + 1);
    }
    
    private static float getHealCap(int amplifier) {
        return ModConfig.COMMON.rejuvenatingShotsHealCap.get().floatValue() * (amplifier + 1);
    }
    
    @Override
    public boolean isInstantenous() {
        return false;
    }
}