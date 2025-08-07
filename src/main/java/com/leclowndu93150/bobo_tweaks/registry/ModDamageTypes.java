package com.leclowndu93150.bobo_tweaks.registry;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> LIFE_DRAIN = ResourceKey.create(Registries.DAMAGE_TYPE, 
        new ResourceLocation(BoboTweaks.MODID, "life_drain"));
    
    public static final ResourceKey<DamageType> HYPERTHERMIA = ResourceKey.create(Registries.DAMAGE_TYPE,
        new ResourceLocation(BoboTweaks.MODID, "hyperthermia"));
    
    public static final ResourceKey<DamageType> HYPOTHERMIA = ResourceKey.create(Registries.DAMAGE_TYPE,
        new ResourceLocation(BoboTweaks.MODID, "hypothermia"));
}