package com.leclowndu93150.bobo_tweaks.additional.exclusiveitems;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CuriosIntegration {

    public static List<ItemStack> getCuriosItemsFromTag(Player player, TagKey<Item> tag) {
        List<ItemStack> items = new ArrayList<>();
        
        if (!ModList.get().isLoaded("curios")) {
            return items;
        }
        
        try {
            CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                
                for (ICurioStacksHandler stacksHandler : curios.values()) {
                    IDynamicStackHandler stacks = stacksHandler.getStacks();
                    
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        ItemStack stack = stacks.getStackInSlot(i);
                        if (!stack.isEmpty() && stack.is(tag)) {
                            items.add(stack);
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
        
        return items;
    }
    
    public static void removeCuriosItem(Player player, ItemStack toRemove) {
        if (!ModList.get().isLoaded("curios")) {
            return;
        }
        
        try {
            CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                
                for (ICurioStacksHandler stacksHandler : curios.values()) {
                    IDynamicStackHandler stacks = stacksHandler.getStacks();
                    
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        ItemStack stack = stacks.getStackInSlot(i);
                        if (ItemStack.isSameItemSameTags(stack, toRemove)) {
                            stacks.setStackInSlot(i, ItemStack.EMPTY);
                            return;
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }
}