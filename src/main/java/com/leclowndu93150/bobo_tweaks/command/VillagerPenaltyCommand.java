package com.leclowndu93150.bobo_tweaks.command;

import com.leclowndu93150.bobo_tweaks.additional.villager.VillagerPenaltyManager;
import com.leclowndu93150.bobo_tweaks.additional.villager.config.VillagerModuleConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

import java.util.Collection;

public class VillagerPenaltyCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("villagerpenalty")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("reset")
                .then(Commands.argument("targets", EntityArgument.entities())
                    .executes(ctx -> resetPenalties(
                        ctx.getSource(),
                        EntityArgument.getEntities(ctx, "targets")
                    ))
                )
            )
            .then(Commands.literal("set")
                .then(Commands.argument("targets", EntityArgument.entities())
                    .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("sleep");
                            builder.suggest("social");
                            builder.suggest("shelter");
                            builder.suggest("all");
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                            .executes(ctx -> setPenalties(
                                ctx.getSource(),
                                EntityArgument.getEntities(ctx, "targets"),
                                StringArgumentType.getString(ctx, "type"),
                                IntegerArgumentType.getInteger(ctx, "amount")
                            ))
                        )
                    )
                )
            )
            .then(Commands.literal("get")
                .then(Commands.argument("target", EntityArgument.entity())
                    .executes(ctx -> getPenalties(
                        ctx.getSource(),
                        EntityArgument.getEntity(ctx, "target")
                    ))
                )
            )
            .then(Commands.literal("refresh")
                .then(Commands.argument("targets", EntityArgument.entities())
                    .executes(ctx -> refreshPrices(
                        ctx.getSource(),
                        EntityArgument.getEntities(ctx, "targets")
                    ))
                )
            )
            .then(Commands.literal("reload")
                .executes(ctx -> reloadConfig(ctx.getSource()))
            )
        );
    }
    
    private static int resetPenalties(CommandSourceStack source, Collection<? extends Entity> targets) {
        int count = 0;
        
        for (Entity entity : targets) {
            if (entity instanceof Villager villager) {
                VillagerPenaltyManager.PenaltyData data = VillagerPenaltyManager.getPenaltyData(villager);
                data.removeSleepPenalty(Integer.MAX_VALUE);
                data.setSocialPenalty(0);
                data.setShelterPenalty(0);
                VillagerPenaltyManager.forceUpdatePrices(villager);
                count++;
            }
        }
        
        final int finalCount = count;
        if (finalCount > 0) {
            source.sendSuccess(() -> Component.literal("Reset penalties for " + finalCount + " villager(s)"), true);
        } else {
            source.sendFailure(Component.literal("No villagers found in selection"));
        }
        
        return finalCount;
    }
    
    private static int setPenalties(CommandSourceStack source, Collection<? extends Entity> targets, String type, int amount) {
        int count = 0;
        
        for (Entity entity : targets) {
            if (entity instanceof Villager villager) {
                VillagerPenaltyManager.PenaltyData data = VillagerPenaltyManager.getPenaltyData(villager);
                
                switch (type) {
                    case "sleep" -> {
                        data.removeSleepPenalty(Integer.MAX_VALUE);
                        data.addSleepPenalty(amount);
                    }
                    case "social" -> data.setSocialPenalty(amount);
                    case "shelter" -> data.setShelterPenalty(amount);
                    case "all" -> {
                        data.removeSleepPenalty(Integer.MAX_VALUE);
                        data.addSleepPenalty(amount);
                        data.setSocialPenalty(amount);
                        data.setShelterPenalty(amount);
                    }
                }
                
                VillagerPenaltyManager.forceUpdatePrices(villager);
                count++;
            }
        }
        
        final int finalCount = count;
        final String finalType = type;
        final int finalAmount = amount;
        if (finalCount > 0) {
            source.sendSuccess(() -> Component.literal("Set " + finalType + " penalties to " + finalAmount + " for " + finalCount + " villager(s)"), true);
        } else {
            source.sendFailure(Component.literal("No villagers found in selection"));
        }
        
        return finalCount;
    }
    
    private static int getPenalties(CommandSourceStack source, Entity target) {
        if (target instanceof Villager villager) {
            VillagerPenaltyManager.PenaltyData data = VillagerPenaltyManager.getPenaltyData(villager);
            
            source.sendSuccess(() -> Component.literal(String.format(
                "Villager Penalties:\n - Sleep: %d/%d\n - Social: %d/%d\n - Shelter: %d/%d\n - Total: %d\n - Price Modifier: +%.0f%%",
                data.getSleepPenalties(),
                VillagerModuleConfig.maxSleepPenaltyStacks,
                data.getSocialPenalties(),
                VillagerModuleConfig.maxSocialPenaltyStacks,
                data.getShelterPenalties(),
                VillagerModuleConfig.maxShelterPenaltyStacks,
                data.getTotalPenalties(),
                (VillagerPenaltyManager.getPriceModifier(villager) - 1) * 100
            )), false);
            
            return 1;
        } else {
            source.sendFailure(Component.literal("Target is not a villager"));
            return 0;
        }
    }
    
    private static int refreshPrices(CommandSourceStack source, Collection<? extends Entity> targets) {
        int count = 0;
        
        for (Entity entity : targets) {
            if (entity instanceof Villager villager) {
                VillagerPenaltyManager.forceUpdatePrices(villager);
                villager.getOffers().forEach(offer -> offer.resetSpecialPriceDiff());
                count++;
            }
        }
        
        final int finalCount = count;
        if (finalCount > 0) {
            source.sendSuccess(() -> Component.literal("Refreshed prices for " + finalCount + " villager(s)"), true);
        } else {
            source.sendFailure(Component.literal("No villagers found in selection"));
        }
        
        return finalCount;
    }
    
    private static int reloadConfig(CommandSourceStack source) {
        VillagerModuleConfig.load();
        source.sendSuccess(() -> Component.literal("Reloaded villager module configuration"), true);
        return 1;
    }
}