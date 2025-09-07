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

    public static final RegistryObject<MobEffect> GUARD_POINT = MOB_EFFECTS.register("guard_point",
        GuardPointEffect::new);
    
    public static final RegistryObject<MobEffect> THE_WARDEN = MOB_EFFECTS.register("the_warden",
        TheWardenEffect::new);

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

    public static final RegistryObject<MobEffect> CONDUIT = MOB_EFFECTS.register("conduit",
        ConduitEffect::new);

    public static final RegistryObject<MobEffect> MYSTICAL_SHIELD = MOB_EFFECTS.register("mystical_shield",
        MysticalShieldEffect::new);

    public static final RegistryObject<MobEffect> MANA_DRAIN = MOB_EFFECTS.register("mana_drain",
        ManaDrainEffect::new);
    
    public static final RegistryObject<MobEffect> FURY = MOB_EFFECTS.register("fury",
        FuryEffect::new);
    
    public static final RegistryObject<MobEffect> REBUKE = MOB_EFFECTS.register("rebuke",
        RebukeEffect::new);
    
    public static final RegistryObject<MobEffect> STATUS_OFFLINE = MOB_EFFECTS.register("status_offline",
        StatusOfflineEffect::new);
    
    public static final RegistryObject<MobEffect> REJUVENATING_SHOTS = MOB_EFFECTS.register("rejuvenating_shots",
        RejuvenatingShotsEffect::new);
    
    public static final RegistryObject<MobEffect> ALCHEMICAL_LEECHING = MOB_EFFECTS.register("alchemical_leeching",
        AlchemicalLeechingEffect::new);
    
    public static final RegistryObject<MobEffect> WOODWARD_SENTINEL = MOB_EFFECTS.register("woodward_sentinel",
        WoodwardSentinelEffect::new);

    public static final RegistryObject<MobEffect> PENUMBRAL_MARK = MOB_EFFECTS.register("penumbral_mark",
        PenumbralMarkEffect::new);
    
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }
}