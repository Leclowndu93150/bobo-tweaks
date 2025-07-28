package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.refinement.RefinementHelper;
import com.leclowndu93150.bobo_tweaks.refinement.RefinementManager;
import com.leclowndu93150.bobo_tweaks.refinement.RefinementRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    
    @Shadow @Final private DataSlot cost;
    @Shadow public int repairItemCountCost;
    
    public AnvilMenuMixin(MenuType<?> type, int id, Player player) {
        super(type, id, player.getInventory(), null);
    }
    
    @Inject(method = "createResult", at = @At("TAIL"))
    private void onCreateResult(CallbackInfo ci) {
        ItemStack left = this.inputSlots.getItem(0);
        ItemStack right = this.inputSlots.getItem(1);
        
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        RefinementRecipe recipe = RefinementManager.getRecipeFor(left);
        if (recipe == null) {
            return;
        }

        ItemStack upgradeMaterial = recipe.getUpgradeMaterial();
        if (!ItemStack.isSameItemSameTags(right, upgradeMaterial)) {
            return;
        }

        if (this.cost.get() >= 40 && RefinementHelper.canRefine(left, recipe)) {
            int currentLevel = RefinementHelper.getRefinementLevel(left);
            int requiredAmount = recipe.getUpgradeCost(currentLevel);
            
            if (right.getCount() >= requiredAmount) {
                ItemStack refined = RefinementHelper.createRefinedCopy(left, recipe);
                this.resultSlots.setItem(0, refined);
                this.repairItemCountCost = requiredAmount;
            }
        }
    }
    
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void onMayPickup(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack left = this.inputSlots.getItem(0);
        ItemStack result = this.resultSlots.getItem(0);
        
        if (!left.isEmpty() && !result.isEmpty()) {
            RefinementRecipe recipe = RefinementManager.getRecipeFor(left);
            if (recipe != null && RefinementHelper.getRefinementLevel(result) > RefinementHelper.getRefinementLevel(left)) {
                cir.setReturnValue(player.getAbilities().instabuild || player.experienceLevel >= this.cost.get());
            }
        }
    }
}