package com.leclowndu93150.bobo_tweaks.additional.exclusiveitems;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.additional.exclusiveitems.config.ExclusiveItemsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import java.util.*;

public class ExclusiveItemsHandler {

    private static final Map<UUID, Integer> playerTickCounters = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!ExclusiveItemsConfig.ENABLE_MODULE.get()) {
            return;
        }

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }

        UUID playerId = player.getUUID();
        int tickCount = playerTickCounters.getOrDefault(playerId, 0);
        tickCount++;

        // Check every 20 ticks (1 second)
        if (tickCount >= 20) {
            checkAndEnforceExclusivity(player);
            playerTickCounters.put(playerId, 0);
        } else {
            playerTickCounters.put(playerId, tickCount);
        }
    }

    private void checkAndEnforceExclusivity(Player player) {
        for (String tagString : ExclusiveItemsConfig.EXCLUSIVE_TAGS.get()) {
            TagKey<Item> tag = getItemTag(tagString);
            if (tag != null) {
                enforceTagExclusivity(player, tag, tagString);
            }
        }
    }

    private void enforceTagExclusivity(Player player, TagKey<Item> tag, String tagString) {
        boolean foundOne = false;
        List<ItemStack> toDrop = new ArrayList<>();

        // Check main inventory
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty() && stack.is(tag)) {
                if (!foundOne) {
                    // First item found - check if it's stacked
                    if (stack.getCount() > 1) {
                        // Split the stack - keep 1, drop the rest
                        ItemStack dropStack = stack.copy();
                        dropStack.setCount(stack.getCount() - 1);
                        toDrop.add(dropStack);
                        stack.setCount(1);
                    }
                    foundOne = true;
                } else {
                    // Additional item found - drop entire stack
                    toDrop.add(stack.copy());
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                }
            }
        }

        // Check armor slots
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().armor.get(i);
            if (!stack.isEmpty() && stack.is(tag)) {
                if (!foundOne) {
                    if (stack.getCount() > 1) {
                        ItemStack dropStack = stack.copy();
                        dropStack.setCount(stack.getCount() - 1);
                        toDrop.add(dropStack);
                        stack.setCount(1);
                    }
                    foundOne = true;
                } else {
                    toDrop.add(stack.copy());
                    player.getInventory().armor.set(i, ItemStack.EMPTY);
                }
            }
        }

        // Check offhand
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.isEmpty() && offhand.is(tag)) {
            if (!foundOne) {
                if (offhand.getCount() > 1) {
                    ItemStack dropStack = offhand.copy();
                    dropStack.setCount(offhand.getCount() - 1);
                    toDrop.add(dropStack);
                    offhand.setCount(1);
                }
                foundOne = true;
            } else {
                toDrop.add(offhand.copy());
                player.getInventory().offhand.set(0, ItemStack.EMPTY);
            }
        }

        // Check Curios if enabled
        if (ExclusiveItemsConfig.CHECK_CURIOS.get() && ModList.get().isLoaded("curios")) {
            List<ItemStack> curiosItems = CuriosIntegration.getCuriosItemsFromTag(player, tag);
            for (ItemStack curiosItem : curiosItems) {
                if (!foundOne) {
                    if (curiosItem.getCount() > 1) {
                        ItemStack dropStack = curiosItem.copy();
                        dropStack.setCount(curiosItem.getCount() - 1);
                        toDrop.add(dropStack);
                        curiosItem.setCount(1);
                    }
                    foundOne = true;
                } else {
                    toDrop.add(curiosItem.copy());
                    CuriosIntegration.removeCuriosItem(player, curiosItem);
                }
            }
        }

        // Drop all excess items
        for (ItemStack stackToDrop : toDrop) {
            dropItemNearPlayer(player, stackToDrop);

            if (ExclusiveItemsConfig.NOTIFY_PLAYER.get()) {
                notifyPlayer(player, stackToDrop, tagString);
            }
        }
    }

    private void dropItemNearPlayer(Player player, ItemStack item) {
        ItemEntity itemEntity = new ItemEntity(player.level(),
                player.getX(), player.getY() + 0.5, player.getZ(), item);
        itemEntity.setPickUpDelay(40);
        player.level().addFreshEntity(itemEntity);
    }

    private void notifyPlayer(Player player, ItemStack item, String tag) {
        MutableComponent message = Component.literal("Dropped ")
                .withStyle(ChatFormatting.YELLOW);
        message.append(item.getDisplayName().copy().withStyle(ChatFormatting.WHITE));

        if (item.getCount() > 1) {
            message.append(Component.literal(" x" + item.getCount()).withStyle(ChatFormatting.GRAY));
        }

        message.append(Component.literal(" - only one item from ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(tag).withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" allowed!").withStyle(ChatFormatting.YELLOW));

        player.sendSystemMessage(message);
    }

    private TagKey<Item> getItemTag(String tagString) {
        try {
            ResourceLocation tagLocation = new ResourceLocation(tagString);
            return TagKey.create(Registries.ITEM, tagLocation);
        } catch (Exception e) {
            BoboTweaks.getLogger().warn("Invalid item tag: {}", tagString);
            return null;
        }
    }
}