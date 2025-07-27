package com.leclowndu93150.bobo_tweaks.util;

import com.leclowndu93150.bobo_tweaks.registry.ModDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public class ModDamageSources {
    
    public static DamageSource lifeDrain(LivingEntity entity) {
        Holder<DamageType> damageType = entity.level().registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(ModDamageTypes.LIFE_DRAIN);
        return new DamageSource(damageType);
    }
    
    public static DamageSource hyperthermia(LivingEntity entity) {
        Holder<DamageType> damageType = entity.level().registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(ModDamageTypes.HYPERTHERMIA);
        return new DamageSource(damageType);
    }
    
    public static DamageSource hypothermia(LivingEntity entity) {
        Holder<DamageType> damageType = entity.level().registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(ModDamageTypes.HYPOTHERMIA);
        return new DamageSource(damageType);
    }
}