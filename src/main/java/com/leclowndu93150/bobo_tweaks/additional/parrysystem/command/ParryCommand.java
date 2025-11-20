package com.leclowndu93150.bobo_tweaks.additional.parrysystem.command;

import com.leclowndu93150.bobo_tweaks.additional.parrysystem.ParryData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ParryCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("bobo")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("parry")
                    .then(Commands.literal("energized")
                        .executes(ParryCommand::grantEnergizedToSelf)
                        .then(Commands.argument("targets", EntityArgument.players())
                            .executes(ParryCommand::grantEnergizedToTargets)))
                    .then(Commands.literal("remove")
                        .executes(ParryCommand::removeFromSelf)
                        .then(Commands.argument("targets", EntityArgument.players())
                            .executes(ParryCommand::removeFromTargets)))
                    .then(Commands.literal("check")
                        .executes(ParryCommand::checkSelf)
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ParryCommand::checkTarget))))
        );
    }
    
    private static int grantEnergizedToSelf(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(
                Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        ParryData.setPlayerAbility(player.getUUID(), "energized");
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Granted parry ability: ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal("energized").withStyle(ChatFormatting.YELLOW)),
            true
        );
        
        return 1;
    }
    
    private static int grantEnergizedToTargets(CommandContext<CommandSourceStack> context) {
        try {
            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
            int count = 0;
            
            for (ServerPlayer target : targets) {
                ParryData.setPlayerAbility(target.getUUID(), "energized");
                count++;
                
                target.sendSystemMessage(
                    Component.literal("You have been granted the parry ability: ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.literal("energized").withStyle(ChatFormatting.YELLOW))
                );
            }
            
            int finalCount = count;
            context.getSource().sendSuccess(() -> 
                Component.literal("Granted parry ability to ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(String.valueOf(finalCount)).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" player(s).").withStyle(ChatFormatting.GREEN)),
                true
            );
            
            return finalCount;
        } catch (Exception e) {
            context.getSource().sendFailure(
                Component.literal("Failed to grant parry ability.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
    }
    
    private static int removeFromSelf(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(
                Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        ParryData.setPlayerAbility(player.getUUID(), "none");
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Removed parry ability.").withStyle(ChatFormatting.GREEN),
            true
        );
        
        return 1;
    }
    
    private static int removeFromTargets(CommandContext<CommandSourceStack> context) {
        try {
            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
            int count = 0;
            
            for (ServerPlayer target : targets) {
                ParryData.setPlayerAbility(target.getUUID(), "none");
                count++;
                
                target.sendSystemMessage(
                    Component.literal("Your parry ability has been removed.")
                        .withStyle(ChatFormatting.YELLOW)
                );
            }
            
            int finalCount = count;
            context.getSource().sendSuccess(() -> 
                Component.literal("Removed parry ability from ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(String.valueOf(finalCount)).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" player(s).").withStyle(ChatFormatting.GREEN)),
                true
            );
            
            return finalCount;
        } catch (Exception e) {
            context.getSource().sendFailure(
                Component.literal("Failed to remove parry ability.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
    }
    
    private static int checkSelf(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(
                Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        String ability = ParryData.getAbilityType(player);
        
        if (ability.equals("none")) {
            context.getSource().sendSuccess(() -> 
                Component.literal("You do not have any parry abilities.").withStyle(ChatFormatting.YELLOW),
                false
            );
        } else {
            context.getSource().sendSuccess(() -> 
                Component.literal("Your parry ability: ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(ability).withStyle(ChatFormatting.YELLOW)),
                false
            );
        }
        
        return 1;
    }
    
    private static int checkTarget(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(context, "target");
            String ability = ParryData.getAbilityType(target);
            
            if (ability.equals("none")) {
                context.getSource().sendSuccess(() -> 
                    Component.literal(target.getName().getString())
                        .withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal(" does not have any parry abilities.").withStyle(ChatFormatting.GREEN)),
                    false
                );
            } else {
                context.getSource().sendSuccess(() -> 
                    Component.literal(target.getName().getString())
                        .withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal("'s parry ability: ").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(ability).withStyle(ChatFormatting.YELLOW)),
                    false
                );
            }
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                Component.literal("Failed to check parry ability.").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
    }
}
