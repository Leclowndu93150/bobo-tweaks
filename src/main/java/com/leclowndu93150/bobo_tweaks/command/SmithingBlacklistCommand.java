package com.leclowndu93150.bobo_tweaks.command;

import com.leclowndu93150.bobo_tweaks.additional.smithing.SmithingBlacklistManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SmithingBlacklistCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("smithingblacklist")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("add")
                .executes(context -> addHeldItem(context.getSource()))
                .then(Commands.argument("item", StringArgumentType.string())
                    .executes(context -> addItem(context.getSource(), StringArgumentType.getString(context, "item")))))
            .then(Commands.literal("remove")
                .executes(context -> removeHeldItem(context.getSource()))
                .then(Commands.argument("item", StringArgumentType.string())
                    .executes(context -> removeItem(context.getSource(), StringArgumentType.getString(context, "item")))))
            .then(Commands.literal("list")
                .executes(context -> listItems(context.getSource())))
            .then(Commands.literal("reload")
                .executes(context -> reload(context.getSource()))));
    }
    
    private static int addHeldItem(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.isEmpty()) {
                source.sendFailure(Component.literal("You must be holding an item to add it to the blacklist")
                    .withStyle(ChatFormatting.RED));
                return 0;
            }
            
            Item item = heldItem.getItem();
            SmithingBlacklistManager.addItem(item);
            
            source.sendSuccess(() -> Component.literal("Added ")
                .withStyle(ChatFormatting.GREEN)
                .append(heldItem.getDisplayName())
                .append(Component.literal(" to smithing blacklist"))
                .withStyle(ChatFormatting.GREEN), true);
            return 1;
        }
        
        source.sendFailure(Component.literal("This command can only be run by a player")
            .withStyle(ChatFormatting.RED));
        return 0;
    }
    
    private static int addItem(CommandSourceStack source, String itemId) {
        try {
            ResourceLocation resLoc = new ResourceLocation(itemId);
            Item item = ForgeRegistries.ITEMS.getValue(resLoc);
            
            if (item == null) {
                source.sendFailure(Component.literal("Unknown item: " + itemId)
                    .withStyle(ChatFormatting.RED));
                return 0;
            }
            
            SmithingBlacklistManager.addItem(item);
            source.sendSuccess(() -> Component.literal("Added " + itemId + " to smithing blacklist")
                .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid item ID: " + itemId)
                .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    private static int removeHeldItem(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.isEmpty()) {
                source.sendFailure(Component.literal("You must be holding an item to remove it from the blacklist")
                    .withStyle(ChatFormatting.RED));
                return 0;
            }
            
            Item item = heldItem.getItem();
            SmithingBlacklistManager.removeItem(item);
            
            source.sendSuccess(() -> Component.literal("Removed ")
                .withStyle(ChatFormatting.YELLOW)
                .append(heldItem.getDisplayName())
                .append(Component.literal(" from smithing blacklist"))
                .withStyle(ChatFormatting.YELLOW), true);
            return 1;
        }
        
        source.sendFailure(Component.literal("This command can only be run by a player")
            .withStyle(ChatFormatting.RED));
        return 0;
    }
    
    private static int removeItem(CommandSourceStack source, String itemId) {
        try {
            ResourceLocation resLoc = new ResourceLocation(itemId);
            Item item = ForgeRegistries.ITEMS.getValue(resLoc);
            
            if (item == null) {
                source.sendFailure(Component.literal("Unknown item: " + itemId)
                    .withStyle(ChatFormatting.RED));
                return 0;
            }
            
            SmithingBlacklistManager.removeItem(item);
            source.sendSuccess(() -> Component.literal("Removed " + itemId + " from smithing blacklist")
                .withStyle(ChatFormatting.YELLOW), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid item ID: " + itemId)
                .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
    
    private static int listItems(CommandSourceStack source) {
        var items = SmithingBlacklistManager.getBlacklistedItems();
        
        if (items.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Smithing blacklist is empty")
                .withStyle(ChatFormatting.GRAY), false);
            return 1;
        }
        
        source.sendSuccess(() -> Component.literal("Smithing blacklist (" + items.size() + " items):")
            .withStyle(ChatFormatting.GOLD), false);
        
        for (Item item : items) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId != null) {
                source.sendSuccess(() -> Component.literal("  - " + itemId)
                    .withStyle(ChatFormatting.GRAY), false);
            }
        }
        
        return 1;
    }
    
    private static int reload(CommandSourceStack source) {
        SmithingBlacklistManager.reload();
        source.sendSuccess(() -> Component.literal("Reloaded smithing blacklist")
            .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }
}