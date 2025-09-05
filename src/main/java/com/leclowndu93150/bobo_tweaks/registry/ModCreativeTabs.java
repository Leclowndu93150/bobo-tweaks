package com.leclowndu93150.bobo_tweaks.registry;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import com.leclowndu93150.bobo_tweaks.additional.enchantments.EnchantmentModuleRegistration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoboTweaks.MODID);
    
    public static final RegistryObject<CreativeModeTab> BOBO_TAB = CREATIVE_MODE_TABS.register("bobo_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + BoboTweaks.MODID + ".bobo_tab"))
            .icon(Items.POTION::getDefaultInstance)
            .displayItems((parameters, output) -> {
                ModPotions.MOB_EFFECTS.getEntries().forEach(effect -> {
                    ItemStack potion = new ItemStack(Items.POTION);
                    PotionUtils.setCustomEffects(potion, List.of(new MobEffectInstance(effect.get(), 3600, 0)));
                    output.accept(potion);
                });

                ItemStack reprisalBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.REPRISAL.get(), 1), reprisalBook);
                output.accept(reprisalBook);
                
                ItemStack momentumBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.MOMENTUM.get(), 1), momentumBook);
                output.accept(momentumBook);
                
                ItemStack spellbladeBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.SPELLBLADE.get(), 1), spellbladeBook);
                output.accept(spellbladeBook);
                
                ItemStack attunementBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.MAGICAL_ATTUNEMENT.get(), 1), attunementBook);
                output.accept(attunementBook);
                
                ItemStack perfectionistBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.PERFECTIONIST.get(), 1), perfectionistBook);
                output.accept(perfectionistBook);
                
                ItemStack hunterBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.HUNTER.get(), 1), hunterBook);
                output.accept(hunterBook);
                
                ItemStack multiscaleBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.MULTISCALE.get(), 1), multiscaleBook);
                output.accept(multiscaleBook);
                
                ItemStack invigoratingBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.INVIGORATING_DEFENSES.get(), 1), invigoratingBook);
                output.accept(invigoratingBook);
                
                ItemStack lifeSurgeBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.LIFE_SURGE.get(), 1), lifeSurgeBook);
                output.accept(lifeSurgeBook);
                
                ItemStack shadowWalkerBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(java.util.Map.of(EnchantmentModuleRegistration.SHADOW_WALKER.get(), 1), shadowWalkerBook);
                output.accept(shadowWalkerBook);
            })
            .build());
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}