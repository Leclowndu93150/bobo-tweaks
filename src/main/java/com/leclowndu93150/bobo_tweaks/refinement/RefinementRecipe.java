package com.leclowndu93150.bobo_tweaks.refinement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RefinementRecipe {
    private final ResourceLocation id;
    private final List<Item> applicableItems;
    private final ItemStack upgradeMaterial;
    private final int maxRefinements;
    private final double costMultiplier;
    private final List<AttributeModification> attributeModifications;
    
    public RefinementRecipe(ResourceLocation id, List<Item> applicableItems, ItemStack upgradeMaterial, 
                           int maxRefinements, double costMultiplier, List<AttributeModification> attributeModifications) {
        this.id = id;
        this.applicableItems = applicableItems;
        this.upgradeMaterial = upgradeMaterial;
        this.maxRefinements = maxRefinements;
        this.costMultiplier = costMultiplier;
        this.attributeModifications = attributeModifications;
    }
    
    public ResourceLocation getId() {
        return id;
    }
    
    public boolean isApplicable(ItemStack stack) {
        return applicableItems.contains(stack.getItem());
    }
    
    public ItemStack getUpgradeMaterial() {
        return upgradeMaterial.copy();
    }
    
    public int getMaxRefinements() {
        return maxRefinements;
    }
    
    public int getUpgradeCost(int currentRefinement) {
        if (currentRefinement == 0) {
            return upgradeMaterial.getCount();
        }
        return (int) Math.ceil(upgradeMaterial.getCount() * Math.pow(costMultiplier, currentRefinement));
    }
    
    public List<AttributeModification> getAttributeModifications() {
        return attributeModifications;
    }
    
    public static RefinementRecipe fromJson(ResourceLocation id, JsonObject json) {
        // Parse items array
        List<Item> items = new ArrayList<>();
        JsonArray itemsArray = json.getAsJsonArray("items");
        for (JsonElement element : itemsArray) {
            ResourceLocation itemId = new ResourceLocation(element.getAsString());
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        
        // Parse upgrade material
        JsonObject materialObj = json.getAsJsonObject("upgrade_material");
        ResourceLocation materialId = new ResourceLocation(materialObj.get("item").getAsString());
        Item materialItem = ForgeRegistries.ITEMS.getValue(materialId);
        int materialAmount = materialObj.get("amount").getAsInt();
        ItemStack upgradeMaterial = new ItemStack(materialItem, materialAmount);
        
        // Parse other values
        int maxRefinements = json.get("max_refinements").getAsInt();
        double costMultiplier = json.get("cost_mult").getAsDouble();
        
        // Parse attribute modifications
        List<AttributeModification> modifications = new ArrayList<>();
        JsonArray modsArray = json.getAsJsonArray("attribute_modifications_per_upgrade");
        for (JsonElement element : modsArray) {
            JsonObject modObj = element.getAsJsonObject();
            ResourceLocation attrId = new ResourceLocation(modObj.get("name").getAsString());
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attrId);
            if (attribute != null) {
                double modAmount = modObj.get("amount").getAsDouble();
                String operationStr = modObj.get("operation").getAsString();
                AttributeModifier.Operation operation = parseOperation(operationStr);
                modifications.add(new AttributeModification(attribute, modAmount, operation));
            }
        }
        
        return new RefinementRecipe(id, items, upgradeMaterial, maxRefinements, costMultiplier, modifications);
    }
    
    private static AttributeModifier.Operation parseOperation(String operation) {
        return switch (operation.toLowerCase()) {
            case "add" -> AttributeModifier.Operation.ADDITION;
            case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> AttributeModifier.Operation.ADDITION;
        };
    }
    
    public static class AttributeModification {
        private final Attribute attribute;
        private final double amount;
        private final AttributeModifier.Operation operation;
        
        public AttributeModification(Attribute attribute, double amount, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.amount = amount;
            this.operation = operation;
        }
        
        public Attribute getAttribute() {
            return attribute;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public AttributeModifier.Operation getOperation() {
            return operation;
        }
        
        public AttributeModifier createModifier(String name, int refinementLevel) {
            double totalAmount = amount * refinementLevel;
            UUID uuid = UUID.nameUUIDFromBytes((attribute.getDescriptionId() + "_refinement_" + refinementLevel).getBytes());
            return new AttributeModifier(uuid, name, totalAmount, operation);
        }
    }
}