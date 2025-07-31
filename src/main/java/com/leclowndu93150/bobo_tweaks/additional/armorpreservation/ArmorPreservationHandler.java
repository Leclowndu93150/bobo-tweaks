package com.leclowndu93150.bobo_tweaks.additional.armorpreservation;

import com.leclowndu93150.bobo_tweaks.additional.armorpreservation.config.ArmorPreservationConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraft.ChatFormatting;
import com.leclowndu93150.baguettelib.event.inventory.InventoryUpdateEvent;

public class ArmorPreservationHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!ArmorPreservationConfig.VALUES.enabled.get()) {
            return;
        }
        
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }
        
        double threshold = ArmorPreservationConfig.VALUES.damageThreshold.get();
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equipmentStack = player.getItemBySlot(slot);
            
            if (equipmentStack.isEmpty() || !equipmentStack.isDamageableItem()) {
                continue;
            }
            
            if (!canEquipInSlot(equipmentStack, slot)) {
                continue;
            }
            
            int maxDamage = equipmentStack.getMaxDamage();
            int currentDamage = equipmentStack.getDamageValue();
            double durabilityPercent = (double)(maxDamage - currentDamage) / maxDamage;
            
            if (durabilityPercent <= threshold) {
                ItemStack preservedEquipment = equipmentStack.copy();
                
                player.setItemSlot(slot, ItemStack.EMPTY);
                
                if (!player.getInventory().add(preservedEquipment)) {
                    player.drop(preservedEquipment, false);
                }
                
                player.displayClientMessage(Component.literal("Your equipment was preserved and moved to inventory!"), true);
            }
        }
    }
    
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ArmorPreservationConfig.VALUES.enabled.get()) {
            return;
        }
        
        ItemStack itemStack = event.getItemStack();
        if (!itemStack.isDamageableItem() || !hasEquipmentSlot(itemStack)) {
            return;
        }
        
        if (isDamagedBeyondThreshold(itemStack)) {
            String colorName = ArmorPreservationConfig.VALUES.textColor.get();
            ChatFormatting color = getChatFormattingFromString(colorName);
            String text = ArmorPreservationConfig.VALUES.unusableText.get();
            
            event.getToolTip().add(Component.literal(text).withStyle(color));
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArmorInventoryUpdate(InventoryUpdateEvent.Armor event) {
        if (!ArmorPreservationConfig.VALUES.enabled.get()) {
            return;
        }
        
        ItemStack newStack = event.getNewStack();
        if (newStack.isEmpty() || !newStack.isDamageableItem()) {
            return;
        }
        
        EquipmentSlot equipmentSlot = event.getEquipmentSlot();
        if (!canEquipInSlot(newStack, equipmentSlot)) {
            return;
        }
        
        if (isDamagedBeyondThreshold(newStack)) {
            Player player = event.getPlayer();
            String text = ArmorPreservationConfig.VALUES.unusableText.get();
            player.displayClientMessage(Component.literal("Cannot equip: " + text), true);
            
            // Schedule removal on next tick
            if (!player.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.getServer().tell(new TickTask(serverLevel.getServer().getTickCount() + 1, () -> {
                    player.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                    if (!player.getInventory().add(newStack)) {
                        player.drop(newStack, false);
                    }
                }));
            }
        }
    }
    
    private static boolean isDamagedBeyondThreshold(ItemStack itemStack) {
        if (itemStack.isEmpty() || !itemStack.isDamageableItem()) {
            return false;
        }
        
        double threshold = ArmorPreservationConfig.VALUES.damageThreshold.get();
        int maxDamage = itemStack.getMaxDamage();
        int currentDamage = itemStack.getDamageValue();
        double durabilityPercent = (double)(maxDamage - currentDamage) / maxDamage;
        
        return durabilityPercent <= threshold;
    }
    
    private static boolean canEquipInSlot(ItemStack itemStack, EquipmentSlot slot) {
        if (itemStack.getItem() instanceof Equipable equipable) {
            return equipable.getEquipmentSlot() == slot;
        }
        return false;
    }
    
    private static boolean hasEquipmentSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof Equipable;
    }
    
    private static ChatFormatting getChatFormattingFromString(String colorName) {
        try {
            return ChatFormatting.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatFormatting.DARK_RED;
        }
    }
}