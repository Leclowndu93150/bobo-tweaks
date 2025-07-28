package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.autobow.client.AutoBowClientHandler;
import com.leclowndu93150.bobo_tweaks.additional.autobow.config.AutoBowConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    
    @Inject(method = "use", at = @At("HEAD"))
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (FMLEnvironment.dist == Dist.CLIENT && AutoBowConfig.VALUES.autoBowEnabled.get()) {
            AutoBowClientHandler.onCrossbowUseStart(player, hand);
        }
    }
    
    @Inject(method = "use", at = @At("RETURN"))
    private void onUseReturn(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (FMLEnvironment.dist == Dist.CLIENT && AutoBowConfig.VALUES.autoBowEnabled.get()) {
            ItemStack stack = player.getItemInHand(hand);
            if (CrossbowItem.isCharged(stack)) {
                AutoBowClientHandler.onCrossbowShot(player, hand);
            }
        }
    }
    
    @Inject(method = "releaseUsing", at = @At("HEAD"))
    private void onReleaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;
        
        if (FMLEnvironment.dist == Dist.CLIENT && AutoBowConfig.VALUES.autoBowEnabled.get()) {
            AutoBowClientHandler.onCrossbowReleaseUsing(player, stack, timeLeft);
        }
    }
}