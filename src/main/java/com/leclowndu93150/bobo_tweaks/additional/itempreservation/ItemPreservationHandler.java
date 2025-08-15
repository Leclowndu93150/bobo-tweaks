package com.leclowndu93150.bobo_tweaks.additional.itempreservation;

import com.leclowndu93150.bobo_tweaks.additional.itempreservation.config.ItemPreservationConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.ChatFormatting;
import com.leclowndu93150.baguettelib.event.inventory.InventoryUpdateEvent;

import java.util.HashSet;
import java.util.Set;

public class ItemPreservationHandler {
    
    private static final TagKey<Item> TOOLS_TAG = ItemTags.create(new ResourceLocation("forge", "tools"));
    private static final TagKey<Item> PICKAXES_TAG = ItemTags.create(new ResourceLocation("minecraft", "pickaxes"));
    private static final TagKey<Item> AXES_TAG = ItemTags.create(new ResourceLocation("minecraft", "axes"));
    private static final TagKey<Item> SHOVELS_TAG = ItemTags.create(new ResourceLocation("minecraft", "shovels"));
    private static final TagKey<Item> HOES_TAG = ItemTags.create(new ResourceLocation("minecraft", "hoes"));
    private static final TagKey<Item> SWORDS_TAG = ItemTags.create(new ResourceLocation("minecraft", "swords"));
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!ItemPreservationConfig.VALUES.enabled.get()) {
            return;
        }
        
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }
        
        // Check armor slots
        if (ItemPreservationConfig.VALUES.preserveArmor.get()) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                    continue;
                }
                
                ItemStack equipmentStack = player.getItemBySlot(slot);
                
                if (equipmentStack.isEmpty() || !equipmentStack.isDamageableItem()) {
                    continue;
                }
                
                if (!canEquipInSlot(equipmentStack, slot)) {
                    continue;
                }
                
                if (isDamagedBeyondThreshold(equipmentStack)) {
                    ItemStack preservedEquipment = equipmentStack.copy();
                    
                    player.setItemSlot(slot, ItemStack.EMPTY);
                    
                    if (!player.getInventory().add(preservedEquipment)) {
                        player.drop(preservedEquipment, false);
                    }
                    
                    player.displayClientMessage(Component.literal("Your armor was preserved and moved to inventory!"), true);
                }
            }
        }
        
        // Check tools in hand
        if (ItemPreservationConfig.VALUES.preserveTools.get()) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack heldItem = player.getItemInHand(hand);
                
                if (heldItem.isEmpty() || !heldItem.isDamageableItem()) {
                    continue;
                }
                
                if (isTool(heldItem) && isDamagedBeyondThreshold(heldItem)) {
                    ItemStack preservedTool = heldItem.copy();
                    
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    
                    if (!player.getInventory().add(preservedTool)) {
                        player.drop(preservedTool, false);
                    }
                    
                    player.displayClientMessage(Component.literal("Your tool was preserved and moved to inventory!"), true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ItemPreservationConfig.VALUES.enabled.get()) {
            return;
        }
        
        ItemStack itemStack = event.getItemStack();
        if (!itemStack.isDamageableItem()) {
            return;
        }
        
        boolean shouldShowTooltip = false;
        
        if (ItemPreservationConfig.VALUES.preserveArmor.get() && hasEquipmentSlot(itemStack)) {
            shouldShowTooltip = true;
        }
        
        if (ItemPreservationConfig.VALUES.preserveTools.get() && isTool(itemStack)) {
            shouldShowTooltip = true;
        }
        
        if (shouldShowTooltip && isDamagedBeyondThreshold(itemStack)) {
            String colorName = ItemPreservationConfig.VALUES.textColor.get();
            ChatFormatting color = getChatFormattingFromString(colorName);
            String text = ItemPreservationConfig.VALUES.unusableText.get();
            
            event.getToolTip().add(Component.literal(text).withStyle(color));
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArmorInventoryUpdate(InventoryUpdateEvent.Armor event) {
        if (!ItemPreservationConfig.VALUES.enabled.get() || !ItemPreservationConfig.VALUES.preserveArmor.get()) {
            return;
        }
        
        ItemStack newStack = event.getNewStack();
        if (newStack.isEmpty() || !newStack.isDamageableItem()) {
            return;
        }
        
        EquipmentSlot equipmentSlot = event.getEquipmentSlot();
        if (!canEquipInSlot(newStack, equipmentSlot)) {
            return;
        }
        
        if (isDamagedBeyondThreshold(newStack)) {
            Player player = event.getPlayer();
            String text = ItemPreservationConfig.VALUES.unusableText.get();
            player.displayClientMessage(Component.literal("Cannot equip: " + text), true);
            
            // Schedule removal on next tick
            if (!player.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.getServer().tell(new TickTask(serverLevel.getServer().getTickCount() + 1, () -> {
                    player.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                    if (!player.getInventory().add(newStack)) {
                        player.drop(newStack, false);
                    }
                }));
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!shouldPreventToolUse()) {
            return;
        }
        
        ItemStack stack = event.getItemStack();
        if (shouldBlockToolUse(stack)) {
            event.setCanceled(true);
            notifyToolNeedsRepair(event.getEntity());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerInteractItem(PlayerInteractEvent.RightClickItem event) {
        if (!shouldPreventToolUse()) {
            return;
        }
        
        ItemStack stack = event.getItemStack();
        if (shouldBlockToolUse(stack)) {
            event.setCanceled(true);
            notifyToolNeedsRepair(event.getEntity());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!shouldPreventToolUse()) {
            return;
        }
        
        ItemStack stack = event.getEntity().getMainHandItem();
        if (shouldBlockToolUse(stack)) {
            event.setCanceled(true);
            notifyToolNeedsRepair(event.getEntity());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!shouldPreventToolUse()) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (shouldBlockToolUse(stack)) {
            event.setCanceled(true);
            notifyToolNeedsRepair(player);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (!shouldPreventToolUse()) {
            return;
        }
        
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        ItemStack stack = event.getItem();
        if (shouldBlockToolUse(stack)) {
            event.setCanceled(true);
            notifyToolNeedsRepair(player);
        }
    }
    
    private static boolean shouldPreventToolUse() {
        return ItemPreservationConfig.VALUES.enabled.get() && 
               ItemPreservationConfig.VALUES.preserveTools.get() && 
               ItemPreservationConfig.VALUES.preventToolUse.get();
    }
    
    private static boolean shouldBlockToolUse(ItemStack stack) {
        return !stack.isEmpty() && stack.isDamageableItem() && 
               isTool(stack) && isDamagedBeyondThreshold(stack);
    }
    
    private static void notifyToolNeedsRepair(Player player) {
        String text = ItemPreservationConfig.VALUES.unusableText.get();
        player.displayClientMessage(Component.literal("Tool " + text.toLowerCase()), true);
    }
    
    private static boolean isTool(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        
        Item item = itemStack.getItem();
        
        // Check instanceof for common tool types
        if (item instanceof TieredItem ||
            item instanceof SwordItem ||
            item instanceof AxeItem ||
            item instanceof PickaxeItem ||
            item instanceof ShovelItem ||
            item instanceof HoeItem ||
            item instanceof TridentItem ||
            item instanceof BowItem ||
            item instanceof CrossbowItem ||
            item instanceof FishingRodItem ||
            item instanceof FlintAndSteelItem ||
            item instanceof ShearsItem) {
            return true;
        }
        
        // Check vanilla tags
        if (ItemPreservationConfig.VALUES.useVanillaTags.get()) {
            if (itemStack.is(PICKAXES_TAG) ||
                itemStack.is(AXES_TAG) ||
                itemStack.is(SHOVELS_TAG) ||
                itemStack.is(HOES_TAG) ||
                itemStack.is(SWORDS_TAG)) {
                return true;
            }
        }
        
        // Check Forge tags
        if (ItemPreservationConfig.VALUES.useForgeTags.get()) {
            if (itemStack.is(TOOLS_TAG)) {
                return true;
            }
        }
        
        // Check additional configured tools
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        if (itemId != null) {
            String itemIdString = itemId.toString();
            for (String additionalTool : ItemPreservationConfig.VALUES.additionalTools.get()) {
                if (itemIdString.equals(additionalTool)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private static boolean isDamagedBeyondThreshold(ItemStack itemStack) {
        if (itemStack.isEmpty() || !itemStack.isDamageableItem()) {
            return false;
        }
        
        double threshold = ItemPreservationConfig.VALUES.damageThreshold.get();
        int maxDamage = itemStack.getMaxDamage();
        int currentDamage = itemStack.getDamageValue();
        double durabilityPercent = (double)(maxDamage - currentDamage) / maxDamage;
        
        return durabilityPercent <= threshold;
    }
    
    private static boolean canEquipInSlot(ItemStack itemStack, EquipmentSlot slot) {
        if (itemStack.getItem() instanceof Equipable equipable) {
            return equipable.getEquipmentSlot() == slot;
        }
        return false;
    }
    
    private static boolean hasEquipmentSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof Equipable;
    }
    
    private static ChatFormatting getChatFormattingFromString(String colorName) {
        try {
            return ChatFormatting.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatFormatting.DARK_RED;
        }
    }
}