package com.leclowndu93150.bobo_tweaks.additional.autobow.compat;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.craftjakob.morecrossbows.common.item.ModCrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class MoreCrossbowsCompat {
    private static final String MORE_CROSSBOWS_MODID = "morecrossbows";
    private static boolean isModLoaded = false;
    
    static {
        isModLoaded = ModList.get().isLoaded(MORE_CROSSBOWS_MODID);
        if (isModLoaded) {
            BoboTweaks.getLogger().info("More Crossbows mod detected, enabling compatibility");
        }
    }

    public static boolean isMoreCrossbowsLoaded() {
        return isModLoaded;
    }

    public static boolean isModCrossbow(ItemStack stack) {
        if (!isModLoaded || stack.isEmpty()) {
            return false;
        }
        
        return stack.getItem() instanceof ModCrossbowItem;
    }

    public static int getModCrossbowChargeDuration(ItemStack stack) {
        if (!isModLoaded || !isModCrossbow(stack)) {
            return 25;
        }
        
        ModCrossbowItem crossbow = (ModCrossbowItem) stack.getItem();
        return crossbow.getUseDuration(stack) - 3;
    }

    public static boolean isModCrossbowCharged(ItemStack stack) {
        if (!isModLoaded || !isModCrossbow(stack)) {
            return false;
        }
        
        return ModCrossbowItem.isCharged(stack);
    }

    public static float getModCrossbowPowerForTime(ItemStack stack, int chargeTime) {
        if (!isModLoaded || !isModCrossbow(stack)) {
            return 0.0f;
        }
        
        ModCrossbowItem crossbow = (ModCrossbowItem) stack.getItem();
        return crossbow.getModPowerForTime(chargeTime, stack);
    }
}