package com.leclowndu93150.bobo_tweaks.registry;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = 
        DeferredRegister.create(ForgeRegistries.ATTRIBUTES, BoboTweaks.MODID);
    
    // Combat Attributes
    public static final RegistryObject<Attribute> DAMAGE_AMPLIFIER = ATTRIBUTES.register("damage_amplifier",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".damage_amplifier", 1.0D, 0.0D, 1024.0D).setSyncable(true));
    
    // Health & Regen Attributes
    public static final RegistryObject<Attribute> LIFE_DRAIN = ATTRIBUTES.register("life_drain",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".life_drain", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> REGENERATION = ATTRIBUTES.register("regeneration",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".regeneration", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    // Temperature Attributes
    public static final RegistryObject<Attribute> HYPERTHERMIA = ATTRIBUTES.register("hyperthermia",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".hyperthermia", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> HEAT_RESISTANCE = ATTRIBUTES.register("heat_resistance",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".heat_resistance", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> HYPOTHERMIA = ATTRIBUTES.register("hypothermia",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".hypothermia", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> COLD_RESISTANCE = ATTRIBUTES.register("cold_resistance",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".cold_resistance", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    // Alchemical Attributes
    public static final RegistryObject<Attribute> ALCHEMICAL_BOOST = ATTRIBUTES.register("alchemical_boost",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".alchemical_boost", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> POWER_LINK = ATTRIBUTES.register("power_link",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".power_link", 0.0D, 0.0D, 10.0D).setSyncable(true));
    
    // Movement Attributes
    public static final RegistryObject<Attribute> JUMP_COUNT = ATTRIBUTES.register("jump_count",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".jump_count", 1.0D, 1.0D, 100.0D).setSyncable(true));
    
    // New Attributes
    public static final RegistryObject<Attribute> ABSORPTION_MULTIPLIER = ATTRIBUTES.register("absorption_multiplier",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".absorption_multiplier", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> MAX_HP_REGEN = ATTRIBUTES.register("max_hp_regen",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".max_hp_regen", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    // Spell School Specific CDR Attributes
    public static final RegistryObject<Attribute> FIRE_COOLDOWN_REDUCTION = ATTRIBUTES.register("fire_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".fire_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> ICE_COOLDOWN_REDUCTION = ATTRIBUTES.register("ice_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".ice_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> LIGHTNING_COOLDOWN_REDUCTION = ATTRIBUTES.register("lightning_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".lightning_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> HOLY_COOLDOWN_REDUCTION = ATTRIBUTES.register("holy_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".holy_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> ENDER_COOLDOWN_REDUCTION = ATTRIBUTES.register("ender_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".ender_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> BLOOD_COOLDOWN_REDUCTION = ATTRIBUTES.register("blood_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".blood_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> EVOCATION_COOLDOWN_REDUCTION = ATTRIBUTES.register("evocation_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".evocation_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> NATURE_COOLDOWN_REDUCTION = ATTRIBUTES.register("nature_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".nature_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> ELDRITCH_COOLDOWN_REDUCTION = ATTRIBUTES.register("eldritch_cooldown_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".eldritch_cooldown_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    // Spell School Specific Cast Time Reduction Attributes
    public static final RegistryObject<Attribute> FIRE_CAST_TIME_REDUCTION = ATTRIBUTES.register("fire_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".fire_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> ICE_CAST_TIME_REDUCTION = ATTRIBUTES.register("ice_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".ice_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> LIGHTNING_CAST_TIME_REDUCTION = ATTRIBUTES.register("lightning_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".lightning_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> HOLY_CAST_TIME_REDUCTION = ATTRIBUTES.register("holy_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".holy_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> ENDER_CAST_TIME_REDUCTION = ATTRIBUTES.register("ender_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".ender_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> BLOOD_CAST_TIME_REDUCTION = ATTRIBUTES.register("blood_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".blood_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> EVOCATION_CAST_TIME_REDUCTION = ATTRIBUTES.register("evocation_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".evocation_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> NATURE_CAST_TIME_REDUCTION = ATTRIBUTES.register("nature_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".nature_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> ELDRITCH_CAST_TIME_REDUCTION = ATTRIBUTES.register("eldritch_cast_time_reduction",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".eldritch_cast_time_reduction", 1.0D, 0.0D, 10.0D).setSyncable(true));
    
    // Leech Attributes
    public static final RegistryObject<Attribute> LIFE_LEECH = ATTRIBUTES.register("life_leech",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".life_leech", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> LEECH_CAP = ATTRIBUTES.register("leech_cap",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".leech_cap", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> SPELL_LEECH = ATTRIBUTES.register("spell_leech",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".spell_leech", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> SPELL_LEECH_CAP = ATTRIBUTES.register("spell_leech_cap",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".spell_leech_cap", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> LIFESTEAL = ATTRIBUTES.register("lifesteal",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".lifesteal", 0.0D, 0.0D, 1.0D).setSyncable(true));
    
    public static final RegistryObject<Attribute> LIFESTEAL_CAP = ATTRIBUTES.register("lifesteal_cap",
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".lifesteal_cap", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    
    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}