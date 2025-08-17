package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.smithing.SmithingBlacklistManager;
import com.leclowndu93150.bobo_tweaks.additional.smithing.config.SmithingBlacklistConfig;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {
    
    @Shadow
    @Final
    private List<SmithingRecipe> recipes;
    
    @Inject(method = "createInputSlotDefinitions", at = @At("RETURN"), cancellable = true)
    private void injectBlacklistChecks(CallbackInfoReturnable<ItemCombinerMenuSlotDefinition> cir) {
        if (!SmithingBlacklistConfig.COMMON.enableSmithingBlacklist.get()) {
            return;
        }

        ItemCombinerMenuSlotDefinition.Builder builder = ItemCombinerMenuSlotDefinition.create();
        
        // Template slot (slot 0) - 8, 48
        builder.withSlot(0, 8, 48, (itemStack) -> {
            if (SmithingBlacklistConfig.COMMON.preventBlacklistedTemplates.get() && 
                SmithingBlacklistManager.isItemBlacklisted(itemStack)) {
                return false;
            }
            // Call original validation logic
            return this.recipes.stream().anyMatch(recipe -> recipe.isTemplateIngredient(itemStack));
        });
        
        // Base slot (slot 1) - 26, 48  
        builder.withSlot(1, 26, 48, (itemStack) -> {
            if (SmithingBlacklistConfig.COMMON.preventBlacklistedBase.get() && 
                SmithingBlacklistManager.isItemBlacklisted(itemStack)) {
                return false;
            }

            return this.recipes.stream().anyMatch(recipe -> recipe.isBaseIngredient(itemStack));
        });
        
        // Addition slot (slot 2) - 44, 48
        builder.withSlot(2, 44, 48, (itemStack) -> {
            if (SmithingBlacklistConfig.COMMON.preventBlacklistedAddition.get() && 
                SmithingBlacklistManager.isItemBlacklisted(itemStack)) {
                return false;
            }

            return this.recipes.stream().anyMatch(recipe -> recipe.isAdditionIngredient(itemStack));
        });
        
        // Result slot (slot 3) - 98, 48
        builder.withResultSlot(3, 98, 48);
        
        cir.setReturnValue(builder.build());
    }
    
    @Inject(method = "canMoveIntoInputSlots", at = @At("HEAD"), cancellable = true)
    private void preventBlacklistedItemMovement(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!SmithingBlacklistConfig.COMMON.enableSmithingBlacklist.get()) {
            return;
        }
        
        if (SmithingBlacklistManager.isItemBlacklisted(itemStack)) {
            cir.setReturnValue(false);
        }
    }
}