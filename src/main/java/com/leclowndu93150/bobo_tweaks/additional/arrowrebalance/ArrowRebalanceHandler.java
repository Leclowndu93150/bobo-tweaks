package com.leclowndu93150.bobo_tweaks.additional.arrowrebalance;

import com.leclowndu93150.bobo_tweaks.additional.arrowrebalance.config.ArrowRebalanceConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowRebalanceHandler {
    
    private static final Map<UUID, Vec3> projectileSpawnPositions = new HashMap<>();
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onProjectileSpawn(EntityJoinLevelEvent event) {
        if (!ArrowRebalanceConfig.isEnabled()) return;
        
        if (event.getEntity() instanceof Projectile projectile) {
            projectileSpawnPositions.put(projectile.getUUID(), projectile.position());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!ArrowRebalanceConfig.isEnabled()) return;
        
        Projectile projectile = event.getProjectile();
        ResourceLocation projectileId = BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType());
        if (projectileId == null) return;
        
        Vec3 spawnPos = projectileSpawnPositions.get(projectile.getUUID());
        if (spawnPos == null) return;
        
        double distance = projectile.position().distanceTo(spawnPos);
        
        ArrowRebalanceConfig.ProjectileConfig config;
        if (ArrowRebalanceConfig.hasProjectileConfig(projectileId.toString())) {
            config = ArrowRebalanceConfig.getProjectileConfig(projectileId.toString());
            if (!config.enabled) return;
        } else {
            config = new ArrowRebalanceConfig.ProjectileConfig();
            config.distanceThreshold = ArrowRebalanceConfig.getDistanceThreshold();
            config.damageReductionPercent = ArrowRebalanceConfig.getDamageReductionPercent();
        }
        
        if (distance > config.distanceThreshold && projectile instanceof AbstractArrow arrow) {
            double reductionMultiplier = 1.0 - (config.damageReductionPercent / 100.0);
            double newDamage = arrow.getBaseDamage() * reductionMultiplier;
            arrow.setBaseDamage(newDamage);
        }
    }
    
    @SubscribeEvent
    public static void onProjectileRemoved(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile) {
            projectileSpawnPositions.remove(projectile.getUUID());
        }
    }
}
