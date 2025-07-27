package com.leclowndu93150.bobo_tweaks.registry;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = 
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BoboTweaks.MODID);
    
    public static final DeferredRegister<Potion> POTIONS = 
        DeferredRegister.create(ForgeRegistries.POTIONS, BoboTweaks.MODID);
    
    // Shield Effects
    public static final RegistryObject<MobEffect> GUARD_POINT = MOB_EFFECTS.register("guard_point",
        GuardPointEffect::new);
    
    public static final RegistryObject<MobEffect> THE_WARDEN = MOB_EFFECTS.register("the_warden",
        TheWardenEffect::new);
    
    // Alchemical Protocol Effects
    public static final RegistryObject<MobEffect> ALCHEMICAL_POWER_UP = MOB_EFFECTS.register("alchemical_power_up",
        AlchemicalPowerUpEffect::new);
    
    public static final RegistryObject<MobEffect> ALCHEMICAL_RESTORE = MOB_EFFECTS.register("alchemical_restore",
        AlchemicalRestoreEffect::new);
    
    public static final RegistryObject<MobEffect> ALCHEMICAL_REGENERATION = MOB_EFFECTS.register("alchemical_regeneration",
        AlchemicalRegenerationEffect::new);
    
    public static final RegistryObject<MobEffect> ALCHEMICAL_SHIELDING = MOB_EFFECTS.register("alchemical_shielding",
        AlchemicalShieldingEffect::new);
    
    public static final RegistryObject<MobEffect> ALCHEMICAL_SPEED = MOB_EFFECTS.register("alchemical_speed",
        AlchemicalSpeedEffect::new);
    
    // Team Effects
    public static final RegistryObject<MobEffect> CONDUIT = MOB_EFFECTS.register("conduit",
        ConduitEffect::new);

    public static final RegistryObject<MobEffect> MYSTICAL_SHIELD = MOB_EFFECTS.register("mystical_shield",
        MysticalShieldEffect::new);
    
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }
}