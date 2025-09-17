package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentModuleRegistration {
    
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = 
        DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "bobo_tweaks");
    
    public static final RegistryObject<Enchantment> REPRISAL = ENCHANTMENTS.register("reprisal",
            ReprisalEnchantment::new);
    
    public static final RegistryObject<Enchantment> MOMENTUM = ENCHANTMENTS.register("momentum",
            MomentumEnchantment::new);
    
    public static final RegistryObject<Enchantment> SPELLBLADE = ENCHANTMENTS.register("spellblade",
            SpellbladeEnchantment::new);
    
    public static final RegistryObject<Enchantment> MAGICAL_ATTUNEMENT = ENCHANTMENTS.register("magical_attunement",
            MagicalAttunementEnchantment::new);
    
    public static final RegistryObject<Enchantment> PERFECTIONIST = ENCHANTMENTS.register("perfectionist",
            PerfectionistEnchantment::new);
    
    public static final RegistryObject<Enchantment> HUNTER = ENCHANTMENTS.register("hunter",
            HunterEnchantment::new);
    
    public static final RegistryObject<Enchantment> MULTISCALE = ENCHANTMENTS.register("multiscale",
            MultiscaleEnchantment::new);
    
    public static final RegistryObject<Enchantment> INVIGORATING_DEFENSES = ENCHANTMENTS.register("invigorating_defenses",
            InvigoratingDefensesEnchantment::new);
    
    public static final RegistryObject<Enchantment> LIFE_SURGE = ENCHANTMENTS.register("life_surge",
            LifeSurgeEnchantment::new);
    
    public static final RegistryObject<Enchantment> SHADOW_WALKER = ENCHANTMENTS.register("shadow_walker",
            ShadowWalkerEnchantment::new);
    
    public static final RegistryObject<Enchantment> INITIATIVE = ENCHANTMENTS.register("initiative",
            InitiativeEnchantment::new);
    
    public static final RegistryObject<Enchantment> SAINTS_PLEDGE = ENCHANTMENTS.register("saints_pledge",
            SaintsPledgeEnchantment::new);
    
    public static final RegistryObject<Enchantment> LEAD_THE_CHARGE = ENCHANTMENTS.register("lead_the_charge",
            LeadTheChargeEnchantment::new);
    
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
        EnchantmentModuleConfig.load();
    }
    
    public static void registerEvents() {
        EnchantmentModuleHandler.register();
    }
}