package com.leclowndu93150.bobo_tweaks.additional.autobow.client;

import com.leclowndu93150.bobo_tweaks.additional.autobow.config.AutoBowConfig;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.network.packet.AutoBowReleasePacket;
import com.leclowndu93150.bobo_tweaks.network.packet.AutoCrossbowReleasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AutoBowClientHandler {
    private static boolean isRightClickHeld = false;
    private static InteractionHand activeHand = null;
    private static ItemType currentItemType = ItemType.NONE;
    private static int bowDrawTicks = 0;
    private static int crossbowChargeTicks = 0;
    private static int crossbowMaxChargeTime = 25; // Standard crossbow charge time
    private static boolean crossbowCharging = false;
    private static boolean shouldIgnoreRightClick = false;
    
    private enum ItemType {
        NONE, BOW, CROSSBOW
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !AutoBowConfig.VALUES.autoBowEnabled.get()) {
            reset();
            return;
        }
        
        if (isRightClickHeld && activeHand != null) {
            ItemStack stack = mc.player.getItemInHand(activeHand);
            
            if (currentItemType == ItemType.BOW && stack.getItem() instanceof BowItem) {
                handleBowAutoShoot(mc.player, stack, activeHand);
            } else if (currentItemType == ItemType.CROSSBOW && stack.getItem() instanceof CrossbowItem) {
                handleCrossbowAutoShoot(mc.player, stack, activeHand);
            } else {
                reset();
            }
        }
    }
    
    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        if (!AutoBowConfig.VALUES.autoBowEnabled.get()) return;
        
        if (event.getButton() == 1) { // Right mouse button
            if (event.getAction() == 1) { // Press
                if (!shouldIgnoreRightClick) {
                    isRightClickHeld = true;
                }
            } else if (event.getAction() == 0) { // Release
                isRightClickHeld = false;
                shouldIgnoreRightClick = false;
                reset();
            }
        }
    }
    
    public static void onBowUseStart(Player player, InteractionHand hand) {
        if (!AutoBowConfig.VALUES.autoBowEnabled.get() || shouldIgnoreRightClick) return;
        
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof BowItem)) {
            return;
        }
        
        if (currentItemType != ItemType.BOW) {
            resetWeaponState();
        }
        
        activeHand = hand;
        currentItemType = ItemType.BOW;
        bowDrawTicks = 0;
    }
    
    public static void onBowShot(Player player, ItemStack stack, InteractionHand hand) {
        if (!AutoBowConfig.VALUES.autoBowEnabled.get()) return;
        
        if (isRightClickHeld && activeHand == hand) {
            shouldIgnoreRightClick = true;
            
            Minecraft.getInstance().execute(() -> {
                if (isRightClickHeld && player.isAlive() && activeHand != null) {
                    shouldIgnoreRightClick = false;
                    bowDrawTicks = 0;
                    player.startUsingItem(activeHand);
                }
            });
        }
    }
    
    private static void handleBowAutoShoot(Player player, ItemStack stack, InteractionHand hand) {
        if (!player.isUsingItem()) {
            return;
        }
        
        bowDrawTicks++;
        
        // For vanilla bows, max power is at 20 ticks (1 second)
        // For modded bows, let them charge fully to respect custom draw speeds
        int maxDrawTime = 20;
        if (stack.getItem() instanceof BowItem bowItem) {
            // Some modded bows might have different charge times
            // We use 20 as the standard max charge time for full power
            float currentPower = BowItem.getPowerForTime(bowDrawTicks);
            
            // Check if we've reached max power (1.0)
            if (currentPower >= 1.0f || bowDrawTicks >= maxDrawTime) {
                ModNetworking.sendToServer(new AutoBowReleasePacket(hand, bowDrawTicks));
                bowDrawTicks = 0;
            }
        }
    }
    
    public static void onCrossbowUseStart(Player player, InteractionHand hand) {
        if (!AutoBowConfig.VALUES.autoBowEnabled.get() || shouldIgnoreRightClick) return;
        
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof CrossbowItem)) {
            return;
        }
        
        if (currentItemType != ItemType.CROSSBOW) {
            resetWeaponState();
        }
        
        activeHand = hand;
        currentItemType = ItemType.CROSSBOW;
        
        if (!CrossbowItem.isCharged(stack)) {
            crossbowCharging = true;
            crossbowChargeTicks = 0;
            crossbowMaxChargeTime = CrossbowItem.getChargeDuration(stack);
        } else {
            if (isRightClickHeld) {
                Minecraft.getInstance().execute(() -> {
                    if (isRightClickHeld && player.isAlive()) {
                        ModNetworking.sendToServer(new AutoCrossbowReleasePacket(hand, 0));
                    }
                });
            }
        }
    }
    
    public static void onCrossbowShot(Player player, InteractionHand hand) {
        if (!AutoBowConfig.VALUES.autoBowEnabled.get()) return;
        
        if (isRightClickHeld && activeHand == hand) {
            shouldIgnoreRightClick = true;
            
            Minecraft.getInstance().execute(() -> {
                if (isRightClickHeld && player.isAlive() && activeHand != null) {
                    shouldIgnoreRightClick = false;
                    crossbowCharging = true;
                    crossbowChargeTicks = 0;
                    player.startUsingItem(activeHand);
                }
            });
        }
    }
    
    public static void onCrossbowReleaseUsing(Player player, ItemStack stack, int timeLeft) {
        if (!AutoBowConfig.VALUES.autoBowEnabled.get()) return;
        
        if (CrossbowItem.isCharged(stack)) {
            crossbowCharging = false;
            crossbowChargeTicks = 0;
        }
    }
    
    private static void handleCrossbowAutoShoot(Player player, ItemStack stack, InteractionHand hand) {
        if (crossbowCharging && !CrossbowItem.isCharged(stack)) {
            crossbowChargeTicks++;
            
            if (!player.isUsingItem() && isRightClickHeld) {
                player.startUsingItem(hand);
            }
            
            if (crossbowChargeTicks >= crossbowMaxChargeTime && player.isUsingItem()) {
                ModNetworking.sendToServer(new AutoCrossbowReleasePacket(hand, crossbowChargeTicks));
                crossbowCharging = false;
                crossbowChargeTicks = 0;
            }
        }
    }
    
    public static void onWeaponSwitch() {
        resetWeaponState();
    }
    
    private static void resetWeaponState() {
        activeHand = null;
        currentItemType = ItemType.NONE;
        bowDrawTicks = 0;
        crossbowChargeTicks = 0;
        crossbowCharging = false;
    }
    
    private static void reset() {
        isRightClickHeld = false;
        activeHand = null;
        currentItemType = ItemType.NONE;
        bowDrawTicks = 0;
        crossbowChargeTicks = 0;
        crossbowCharging = false;
        shouldIgnoreRightClick = false;
    }
}