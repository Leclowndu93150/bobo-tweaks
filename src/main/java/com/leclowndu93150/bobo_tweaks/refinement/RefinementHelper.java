package com.leclowndu93150.bobo_tweaks.refinement;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class RefinementHelper {
    private static final String REFINEMENT_TAG = "bobo_refinement";
    private static final String REFINEMENT_LEVEL = "refinement_level";
    private static final String REFINEMENT_RECIPE = "refinement_recipe";
    
    public static int getRefinementLevel(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(REFINEMENT_TAG)) {
            CompoundTag refinementTag = stack.getTag().getCompound(REFINEMENT_TAG);
            return refinementTag.getInt(REFINEMENT_LEVEL);
        }
        return 0;
    }
    
    public static void setRefinementLevel(ItemStack stack, int level, RefinementRecipe recipe) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag refinementTag = new CompoundTag();
        refinementTag.putInt(REFINEMENT_LEVEL, level);
        refinementTag.putString(REFINEMENT_RECIPE, recipe.getId().toString());
        tag.put(REFINEMENT_TAG, refinementTag);
    }
    
    public static boolean canRefine(ItemStack stack, RefinementRecipe recipe) {
        if (stack.isEmpty() || !recipe.isApplicable(stack)) {
            return false;
        }
        
        // Check if item is at full durability or retain current durability
        // We'll allow refinement at any durability level
        
        int currentLevel = getRefinementLevel(stack);
        return currentLevel < recipe.getMaxRefinements();
    }
    
    public static ItemStack createRefinedCopy(ItemStack original, RefinementRecipe recipe) {
        ItemStack refined = original.copy();
        int currentLevel = getRefinementLevel(original);
        int newLevel = currentLevel + 1;

        setRefinementLevel(refined, newLevel, recipe);
        
        return refined;
    }
    
    public static String getRefinementRecipeId(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(REFINEMENT_TAG)) {
            CompoundTag refinementTag = stack.getTag().getCompound(REFINEMENT_TAG);
            return refinementTag.getString(REFINEMENT_RECIPE);
        }
        return null;
    }
}