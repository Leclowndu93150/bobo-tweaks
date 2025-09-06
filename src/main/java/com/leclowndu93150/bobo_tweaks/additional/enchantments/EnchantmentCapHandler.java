package com.leclowndu93150.bobo_tweaks.additional.enchantments;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.config.EnchantmentModuleConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentCapHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!EnchantmentModuleConfig.enableEnchantmentModule || !EnchantmentModuleConfig.EnchantmentCap.enabled) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty()) {
            return;
        }

        String itemId = ForgeRegistries.ITEMS.getKey(left.getItem()).toString();
        Integer cap = EnchantmentModuleConfig.EnchantmentCap.itemCaps.get(itemId);

        if (cap == null) {
            return;
        }

        Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.getEnchantments(left);
        Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.getEnchantments(right);

        Map<Enchantment, Integer> combinedEnchants = new java.util.HashMap<>(leftEnchants);

        for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            Integer level = entry.getValue();

            if (enchant.canEnchant(left) || left.isEnchanted()) {
                Integer existingLevel = combinedEnchants.get(enchant);
                if (existingLevel == null) {
                    combinedEnchants.put(enchant, level);
                } else if (existingLevel.equals(level) && level < enchant.getMaxLevel()) {
                    combinedEnchants.put(enchant, level + 1);
                } else if (level > existingLevel) {
                    combinedEnchants.put(enchant, level);
                }
            }
        }

        if (combinedEnchants.size() > cap) {
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
            event.setEnchantLevel(0);
        }
    }
}