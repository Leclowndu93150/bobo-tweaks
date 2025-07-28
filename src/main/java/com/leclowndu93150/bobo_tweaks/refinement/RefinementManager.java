package com.leclowndu93150.bobo_tweaks.refinement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RefinementManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, RefinementRecipe> RECIPES = new HashMap<>();
    private static RefinementManager instance;
    
    public RefinementManager() {
        super(GSON, "refinements");
        instance = this;
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        RECIPES.clear();
        
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();
            
            if (element.isJsonObject()) {
                try {
                    RefinementRecipe recipe = RefinementRecipe.fromJson(id, element.getAsJsonObject());
                    RECIPES.put(id, recipe);
                    BoboTweaks.LOGGER.debug("Loaded refinement recipe: {}", id);
                } catch (Exception e) {
                    BoboTweaks.LOGGER.error("Failed to load refinement recipe: {}", id, e);
                }
            }
        }
        
        BoboTweaks.LOGGER.info("Loaded {} refinement recipes", RECIPES.size());
    }
    
    public static RefinementRecipe getRecipeFor(ItemStack stack) {
        for (RefinementRecipe recipe : RECIPES.values()) {
            if (recipe.isApplicable(stack)) {
                return recipe;
            }
        }
        return null;
    }
    
    public static Map<ResourceLocation, RefinementRecipe> getAllRecipes() {
        return new HashMap<>(RECIPES);
    }
    
    public static RefinementManager getInstance() {
        return instance;
    }
}