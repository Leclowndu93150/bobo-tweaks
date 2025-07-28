package com.leclowndu93150.bobo_tweaks.refinement;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BoboTweaks.MODID)
public class RefinementEvents {
    
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }
        
        // Check if this is a refinement operation
        RefinementRecipe recipe = RefinementManager.getRecipeFor(left);
        if (recipe == null) {
            return;
        }
        
        // Check if the right item matches the upgrade material
        ItemStack upgradeMaterial = recipe.getUpgradeMaterial();
        if (!ItemStack.isSameItemSameTags(right, upgradeMaterial)) {
            return;
        }
        
        // Check if the item can be refined
        if (!RefinementHelper.canRefine(left, recipe)) {
            return;
        }
        
        int currentLevel = RefinementHelper.getRefinementLevel(left);
        int requiredAmount = recipe.getUpgradeCost(currentLevel);
        
        // Check if player has enough materials
        if (right.getCount() < requiredAmount) {
            return;
        }
        
        // Create the refined item
        ItemStack refined = RefinementHelper.createRefinedCopy(left, recipe);
        
        // Set the output
        event.setOutput(refined);
        
        // Set appropriate cost that scales with level
        int levelCost = 5 + currentLevel * 3; // Base cost + scaling
        event.setCost(levelCost);
        event.setMaterialCost(requiredAmount);
    }
    
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        // Remove old modifiers
        ItemStack oldStack = event.getFrom();
        if (!oldStack.isEmpty()) {
            removeRefinementModifiers(player, oldStack, event.getSlot());
        }
        
        // Apply new modifiers
        ItemStack newStack = event.getTo();
        if (!newStack.isEmpty()) {
            applyRefinementModifiers(player, newStack, event.getSlot());
        }
    }
    
    private static void removeRefinementModifiers(Player player, ItemStack stack, EquipmentSlot slot) {
        int refinementLevel = RefinementHelper.getRefinementLevel(stack);
        if (refinementLevel <= 0) {
            return;
        }
        
        String recipeId = RefinementHelper.getRefinementRecipeId(stack);
        if (recipeId == null) {
            return;
        }
        
        RefinementRecipe recipe = RefinementManager.getAllRecipes().get(new ResourceLocation(recipeId));
        if (recipe == null) {
            return;
        }
        
        for (RefinementRecipe.AttributeModification mod : recipe.getAttributeModifications()) {
            AttributeInstance instance = player.getAttribute(mod.getAttribute());
            if (instance != null) {
                // Remove all refinement modifiers for this attribute
                for (int i = 1; i <= refinementLevel; i++) {
                    AttributeModifier modifier = mod.createModifier("Refinement " + slot.getName(), i);
                    instance.removeModifier(modifier);
                }
            }
        }
    }
    
    private static void applyRefinementModifiers(Player player, ItemStack stack, EquipmentSlot slot) {
        int refinementLevel = RefinementHelper.getRefinementLevel(stack);
        if (refinementLevel <= 0) {
            return;
        }
        
        String recipeId = RefinementHelper.getRefinementRecipeId(stack);
        if (recipeId == null) {
            return;
        }
        
        RefinementRecipe recipe = RefinementManager.getAllRecipes().get(new ResourceLocation(recipeId));
        if (recipe == null) {
            return;
        }
        
        for (RefinementRecipe.AttributeModification mod : recipe.getAttributeModifications()) {
            AttributeInstance instance = player.getAttribute(mod.getAttribute());
            if (instance != null) {
                AttributeModifier modifier = mod.createModifier("Refinement " + slot.getName(), refinementLevel);
                if (!instance.hasModifier(modifier)) {
                    instance.addPermanentModifier(modifier);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int refinementLevel = RefinementHelper.getRefinementLevel(stack);
        
        if (refinementLevel > 0) {
            List<Component> tooltip = event.getToolTip();
            
            // Find the recipe to get max refinements
            RefinementRecipe recipe = RefinementManager.getRecipeFor(stack);
            if (recipe != null) {
                // Add refinement info after the item name
                Component refinementInfo = Component.literal("Refined (" + refinementLevel + " / " + recipe.getMaxRefinements() + ")")
                    .withStyle(ChatFormatting.GOLD);
                tooltip.add(1, refinementInfo);
                
                // Add attribute bonuses
                tooltip.add(2, Component.empty());
                for (RefinementRecipe.AttributeModification mod : recipe.getAttributeModifications()) {
                    double totalBonus = mod.getAmount() * refinementLevel;
                    String sign = totalBonus >= 0 ? "+" : "";
                    ChatFormatting color = totalBonus >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
                    
                    Component attributeLine;
                    if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
                        attributeLine = Component.literal(" " + sign + String.format("%.1f", totalBonus) + " ")
                            .append(Component.translatable(mod.getAttribute().getDescriptionId()))
                            .withStyle(color);
                    } else {
                        attributeLine = Component.literal(" " + sign + String.format("%.0f%%", totalBonus * 100) + " ")
                            .append(Component.translatable(mod.getAttribute().getDescriptionId()))
                            .withStyle(color);
                    }
                    
                    tooltip.add(3, attributeLine);
                }
            }
        }
    }
}