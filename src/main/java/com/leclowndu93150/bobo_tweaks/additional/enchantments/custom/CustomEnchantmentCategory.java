package com.leclowndu93150.bobo_tweaks.additional.enchantments.custom;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CustomEnchantmentCategory {

    public static final EnchantmentCategory WEAPON_AND_BOW = EnchantmentCategory.create("WEAPON_AND_BOW",
            item -> item instanceof SwordItem ||
                    item instanceof AxeItem ||
                    item instanceof TridentItem ||
                    item instanceof BowItem ||
                    item instanceof CrossbowItem);
}