package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.villager.config.VillagerModuleConfig;
import com.leclowndu93150.bobo_tweaks.additional.villager.VillagerModuleHandler;
import com.leclowndu93150.bobo_tweaks.additional.villager.VillagerPenaltyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    
    @Shadow
    public abstract void updateTrades();
    
    @Shadow
    protected abstract void updateSpecialPrices(Player player);

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }
    
    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void onCustomServerAiStep(CallbackInfo ci) {
        if (!this.level().isClientSide && VillagerModuleConfig.enableVillagerModule) {
            VillagerModuleHandler.handleVillagerTick((Villager)(Object)this);

            if (this.level().getGameTime() % 20 == 0) {
                refreshPricesForAllPlayers();
            }
        }
    }
    
    private void refreshPricesForAllPlayers() {
        Villager villager = (Villager)(Object)this;
        Player tradingPlayer = this.getTradingPlayer();

        if (!this.getOffers().isEmpty()) {
            for (MerchantOffer offer : this.getOffers()) {
                offer.resetSpecialPriceDiff();
            }

            if (tradingPlayer != null) {
                this.updateSpecialPrices(tradingPlayer);
            }
        }
    }
    
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!VillagerModuleConfig.enableVillagerModule) {
            return;
        }
        
        Villager villager = (Villager)(Object)this;

        if (!VillagerModuleHandler.canTradeWithVillager(villager)) {
            if (!this.level().isClientSide) {
                player.displayClientMessage(
                    Component.translatable("bobo_tweaks.villager.no_night_trading"),
                    true
                );
            }
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (!this.level().isClientSide && player.isShiftKeyDown()) {
            VillagerPenaltyManager.PenaltyData data = VillagerPenaltyManager.getPenaltyData(villager);
            player.displayClientMessage(
                Component.literal(String.format(
                    "Penalties - Sleep: %d, Social: %d, Shelter: %d (Total: %d, Price: +%.0f%%)",
                    data.getSleepPenalties(),
                    data.getSocialPenalties(),
                    data.getShelterPenalties(),
                    data.getTotalPenalties(),
                    (VillagerPenaltyManager.getPriceModifier(villager) - 1) * 100
                )),
                true
            );
        }
    }
    
    @Inject(method = "updateSpecialPrices", at = @At("HEAD"))
    private void resetPenaltyPrices(Player player, CallbackInfo ci) {
        if (!VillagerModuleConfig.enableVillagerModule) {
            return;
        }

        Villager villager = (Villager)(Object)this;
        float lastPenaltyModifier = VillagerPenaltyManager.getPriceModifier(villager);
    }
    
    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void applyPenaltyPrices(Player player, CallbackInfo ci) {
        if (!VillagerModuleConfig.enableVillagerModule) {
            return;
        }
        
        Villager villager = (Villager)(Object)this;
        VillagerPenaltyManager.PenaltyData data = VillagerPenaltyManager.getPenaltyData(villager);
        int totalPenalties = data.getTotalPenalties();
        
        if (totalPenalties > 0) {
            float penaltyMultiplier = VillagerPenaltyManager.getPriceModifier(villager) - 1.0f;
            
            for (MerchantOffer offer : this.getOffers()) {
                int baseCost = offer.getBaseCostA().getCount();
                int penaltyIncrease = Mth.floor(baseCost * penaltyMultiplier);
                offer.addToSpecialPriceDiff(penaltyIncrease);
            }
        }
    }
    
    @Inject(method = "startTrading", at = @At("HEAD"))
    private void onStartTrading(Player player, CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            VillagerPenaltyManager.setCurrentTradingVillager((Villager)(Object)this);
        }
    }
    
    @Inject(method = "stopTrading", at = @At("TAIL"))
    private void onStopTrading(CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            VillagerPenaltyManager.clearCurrentTradingVillager();
        }
    }
    
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onSave(CompoundTag tag, CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            VillagerPenaltyManager.savePenaltyData((Villager)(Object)this, tag);
        }
    }
    
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            VillagerPenaltyManager.loadPenaltyData((Villager)(Object)this, tag);
        }
    }
    
    @Inject(method = "die", at = @At("TAIL"))
    private void onDeath(CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            VillagerPenaltyManager.clearData(this.getUUID());
        }
    }
    
    @Inject(method = "stopSleeping", at = @At("TAIL"))
    private void onWakeUp(CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            refreshPricesForAllPlayers();
        }
    }
    
    @Inject(method = "restock", at = @At("TAIL"))
    private void onRestock(CallbackInfo ci) {
        if (VillagerModuleConfig.enableVillagerModule) {
            refreshPricesForAllPlayers();
        }
    }
}