package com.leclowndu93150.bobo_tweaks.additional.autobow.client;

import com.leclowndu93150.baguettelib.event.inventory.InventoryUpdateEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bobo_tweaks", value = Dist.CLIENT)
public class AutoBowInventoryHandler {
    
    @SubscribeEvent
    public static void onHandSlotChange(InventoryUpdateEvent.Hands event) {
        if (event.getEquipmentSlot() != EquipmentSlot.MAINHAND) {
            return;
        }
        
        ItemStack oldStack = event.getOldStack();
        ItemStack newStack = event.getNewStack();
        
        boolean oldWasBow = oldStack.getItem() instanceof BowItem;
        boolean oldWasCrossbow = oldStack.getItem() instanceof CrossbowItem;
        boolean newIsBow = newStack.getItem() instanceof BowItem;
        boolean newIsCrossbow = newStack.getItem() instanceof CrossbowItem;
        
        if ((oldWasBow || oldWasCrossbow) && (newIsBow || newIsCrossbow)) {
            if (oldWasBow && newIsCrossbow) {
                AutoBowClientHandler.onWeaponSwitch();
            } else if (oldWasCrossbow && newIsBow) {
                AutoBowClientHandler.onWeaponSwitch();
            }
        }
        else if ((oldWasBow || oldWasCrossbow) && !newIsBow && !newIsCrossbow) {
            AutoBowClientHandler.onWeaponSwitch();
        }
    }
}