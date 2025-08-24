package com.leclowndu93150.bobo_tweaks.additional.villager.command;

import com.leclowndu93150.bobo_tweaks.additional.villager.config.VillagerModuleConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class VillagerShelterCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("villager-shelter")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                    .executes(VillagerShelterCommand::addItemInHand)
                    .then(Commands.argument("pattern", StringArgumentType.greedyString())
                        .executes(VillagerShelterCommand::addShelterBlock)))
                .then(Commands.literal("remove")
                    .then(Commands.argument("pattern", StringArgumentType.greedyString())
                        .executes(VillagerShelterCommand::removeShelterBlock)))
                .then(Commands.literal("list")
                    .executes(VillagerShelterCommand::listShelterBlocks))
                .then(Commands.literal("clear")
                    .executes(VillagerShelterCommand::clearShelterBlocks))
                .then(Commands.literal("reload")
                    .executes(VillagerShelterCommand::reloadConfig))
                .then(Commands.literal("hotbar")
                    .executes(VillagerShelterCommand::addHotbar))
        );
    }
    
    private static int addShelterBlock(CommandContext<CommandSourceStack> context) {
        String pattern = StringArgumentType.getString(context, "pattern");
        VillagerModuleConfig.addShelterBlock(pattern);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Added shelter block pattern: ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(pattern).withStyle(ChatFormatting.YELLOW)),
            true
        );
        
        return 1;
    }
    
    private static int removeShelterBlock(CommandContext<CommandSourceStack> context) {
        String pattern = StringArgumentType.getString(context, "pattern");
        
        List<String> patterns = VillagerModuleConfig.getShelterBlockPatterns();
        if (patterns.contains(pattern)) {
            VillagerModuleConfig.removeShelterBlock(pattern);
            context.getSource().sendSuccess(() -> 
                Component.literal("Removed shelter block pattern: ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(pattern).withStyle(ChatFormatting.YELLOW)),
                true
            );
        } else {
            context.getSource().sendFailure(
                Component.literal("Pattern not found: ")
                    .withStyle(ChatFormatting.RED)
                    .append(Component.literal(pattern).withStyle(ChatFormatting.YELLOW))
            );
        }
        
        return 1;
    }
    
    private static int listShelterBlocks(CommandContext<CommandSourceStack> context) {
        List<String> patterns = VillagerModuleConfig.getShelterBlockPatterns();
        
        if (patterns.isEmpty()) {
            context.getSource().sendSuccess(() -> 
                Component.literal("No shelter block patterns configured.").withStyle(ChatFormatting.YELLOW),
                false
            );
        } else {
            context.getSource().sendSuccess(() -> 
                Component.literal("Shelter block patterns (").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(String.valueOf(patterns.size())).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("):").withStyle(ChatFormatting.GREEN)),
                false
            );
            
            for (String pattern : patterns) {
                context.getSource().sendSuccess(() -> 
                    Component.literal("  - ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(pattern).withStyle(ChatFormatting.WHITE)),
                    false
                );
            }
        }
        
        return 1;
    }
    
    private static int clearShelterBlocks(CommandContext<CommandSourceStack> context) {
        int count = VillagerModuleConfig.getShelterBlockPatterns().size();
        VillagerModuleConfig.clearShelterBlocks();
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Cleared ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.valueOf(count)).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" shelter block patterns.").withStyle(ChatFormatting.GREEN)),
            true
        );
        
        return 1;
    }
    
    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        VillagerModuleConfig.load();
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Reloaded villager module configuration.").withStyle(ChatFormatting.GREEN),
            true
        );
        
        return 1;
    }
    
    private static int addItemInHand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof Player player)) {
            context.getSource().sendFailure(
                Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            context.getSource().sendFailure(
                Component.literal("You must be holding an item.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        if (!(heldItem.getItem() instanceof BlockItem blockItem)) {
            context.getSource().sendFailure(
                Component.literal("You must be holding a block item.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        Block block = blockItem.getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        String pattern = blockId.toString();
        
        VillagerModuleConfig.addShelterBlock(pattern);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Added shelter block: ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(pattern).withStyle(ChatFormatting.YELLOW)),
            true
        );
        
        return 1;
    }
    
    private static int addHotbar(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof Player player)) {
            context.getSource().sendFailure(
                Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        int addedCount = 0;
        
        // Check all hotbar slots (0-8)
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
                String pattern = blockId.toString();
                
                List<String> existingPatterns = VillagerModuleConfig.getShelterBlockPatterns();
                if (!existingPatterns.contains(pattern)) {
                    VillagerModuleConfig.addShelterBlock(pattern);
                    addedCount++;
                    
                    context.getSource().sendSuccess(() -> 
                        Component.literal("  + ").withStyle(ChatFormatting.GREEN)
                            .append(Component.literal(pattern).withStyle(ChatFormatting.YELLOW)),
                        false
                    );
                }
            }
        }
        
        if (addedCount > 0) {
            int finalAddedCount = addedCount;
            context.getSource().sendSuccess(() ->
                Component.literal("Added ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(String.valueOf(finalAddedCount)).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" shelter blocks from hotbar.").withStyle(ChatFormatting.GREEN)),
                true
            );
        } else {
            context.getSource().sendSuccess(() -> 
                Component.literal("No new blocks found in hotbar (all blocks already added or no block items).").withStyle(ChatFormatting.YELLOW),
                false
            );
        }
        
        return 1;
    }
}