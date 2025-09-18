package com.leclowndu93150.bobo_tweaks.additional.enchantments.base;

import com.leclowndu93150.bobo_tweaks.additional.enchantments.tracking.EnchantmentTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;

public abstract class EventHandlingEnchantment extends Enchantment {
    
    public EventHandlingEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    public int getEnchantmentLevelFromCategory(Player player, String categoryName) {
        ItemStack itemStack = getEnchantedItemStackFromCategory(player, categoryName);
        return EnchantmentHelper.getItemEnchantmentLevel(this, itemStack);
    }

    protected ItemStack getEnchantedItemStackFromCategory(Player player, String categoryName) {
        ItemStack enchantedItem = ItemStack.EMPTY;
        int maxLevel = 0;

        switch (categoryName.toUpperCase()) {
            case "ARMOR":
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                        ItemStack itemStack = player.getItemBySlot(slot);
                        int level = EnchantmentHelper.getItemEnchantmentLevel(this, itemStack);
                        if (level > maxLevel) {
                            maxLevel = level;
                            enchantedItem = itemStack;
                        }
                    }
                }
                break;
            case "ARMOR_FEET":
                enchantedItem = player.getItemBySlot(EquipmentSlot.FEET);
                break;
            case "ARMOR_LEGS":
                enchantedItem = player.getItemBySlot(EquipmentSlot.LEGS);
                break;
            case "ARMOR_CHEST":
                enchantedItem = player.getItemBySlot(EquipmentSlot.CHEST);
                break;
            case "ARMOR_HEAD":
                enchantedItem = player.getItemBySlot(EquipmentSlot.HEAD);
                break;
            case "WEAPON":
            case "DIGGER":
            case "FISHING_ROD":
            case "TRIDENT":
            case "BOW":
            case "CROSSBOW":
            case "WEAPON_AND_BOW":
                enchantedItem = player.getMainHandItem();
                break;
            case "WEARABLE":
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack itemStack = player.getItemBySlot(slot);
                    int level = EnchantmentHelper.getItemEnchantmentLevel(this, itemStack);
                    if (level > maxLevel) {
                        maxLevel = level;
                        enchantedItem = itemStack;
                    }
                }
                break;
            case "BREAKABLE":
            case "VANISHABLE":
                for (ItemStack itemStack : player.getInventory().items) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(this, itemStack);
                    if (level > maxLevel) {
                        maxLevel = level;
                        enchantedItem = itemStack;
                    }
                }
                break;
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(this, enchantedItem) > 0) {
            return enchantedItem;
        }
        return ItemStack.EMPTY;
    }

    public void onLivingHurt(LivingHurtEvent event) {}
    public void onLivingAttack(LivingAttackEvent event) {}
    public void onShieldBlock(ShieldBlockEvent event) {}
    public void onLivingDeath(LivingDeathEvent event) {}
    public void onSpellCast(SpellOnCastEvent event) {}
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {}
    public void onCriticalHit(CriticalHitEvent event) {}
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {}
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EnchantmentTracker.cleanupPlayerData(event.getEntity().getUUID());
    }
}