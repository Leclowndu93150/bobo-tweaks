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
        () -> new RangedAttribute("attribute." + BoboTweaks.MODID + ".damage_amplifier", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    
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
    
    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}