package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentCapHandler {
    
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule || !EnchantmentModuleConfig.EnchantmentCap.enabled) {
            return;
        }
        
        ItemStack result = event.getOutput();
        if (result.isEmpty()) {
            return;
        }
        
        String itemId = ForgeRegistries.ITEMS.getKey(result.getItem()).toString();
        Integer cap = EnchantmentModuleConfig.EnchantmentCap.itemCaps.get(itemId);
        
        if (cap != null && EnchantmentHelper.getEnchantments(result).size() > cap) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule || !EnchantmentModuleConfig.EnchantmentCap.enabled) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item.isEmpty()) {
            return;
        }
        
        String itemId = ForgeRegistries.ITEMS.getKey(item.getItem()).toString();
        Integer cap = EnchantmentModuleConfig.EnchantmentCap.itemCaps.get(itemId);
        
        if (cap != null && EnchantmentHelper.getEnchantments(item).size() >= cap) {
            event.setCanceled(true);
        }
    }
}