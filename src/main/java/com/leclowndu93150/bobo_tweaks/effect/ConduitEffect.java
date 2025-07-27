package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConduitEffect extends MobEffect {
    private static final UUID CONDUIT_BOOST_UUID = UUID.fromString("af6c8b30-4d1e-4b8a-9f8e-2c5d4b3a2f21");
    private final Map<UUID, Map<UUID, Double>> activeConduits = new HashMap<>();
    
    public ConduitEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00CED1);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && entity instanceof ServerPlayer player) {
            Team team = player.getTeam();
            if (team != null) {
                AttributeInstance alchemicalBoostInstance = player.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
                double alchemicalBoost = alchemicalBoostInstance != null ? alchemicalBoostInstance.getValue() : 0.0D;

                double sharedBoost = alchemicalBoost * getSharePercentage(amplifier);
                
                // Share with all team members
                for (String memberName : team.getPlayers()) {
                    ServerPlayer teamMember = player.server.getPlayerList().getPlayerByName(memberName);
                    if (teamMember != null && teamMember != player) {
                        applyBoostToTeammate(teamMember, player.getUUID(), sharedBoost);
                    }
                }
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        if (!entity.level().isClientSide() && entity instanceof ServerPlayer player) {
            Team team = player.getTeam();
            if (team != null) {
                for (String memberName : team.getPlayers()) {
                    ServerPlayer teamMember = player.server.getPlayerList().getPlayerByName(memberName);
                    if (teamMember != null && teamMember != player) {
                        removeBoostFromTeammate(teamMember, player.getUUID());
                    }
                }
            }

            activeConduits.remove(player.getUUID());
        }
    }
    
    private void applyBoostToTeammate(ServerPlayer teammate, UUID sourcePlayerId, double boost) {
        AttributeInstance alchemicalBoostInstance = teammate.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
        if (alchemicalBoostInstance != null) {
            Map<UUID, Double> teamBoosts = activeConduits.computeIfAbsent(teammate.getUUID(), k -> new HashMap<>());
            teamBoosts.put(sourcePlayerId, boost);

            String modifierName = "Conduit boost from " + sourcePlayerId;
            UUID modifierUuid = UUID.nameUUIDFromBytes((CONDUIT_BOOST_UUID.toString() + sourcePlayerId).getBytes());
            
            AttributeModifier modifier = new AttributeModifier(modifierUuid, modifierName, boost, AttributeModifier.Operation.ADDITION);
            alchemicalBoostInstance.removeModifier(modifier);
            alchemicalBoostInstance.addTransientModifier(modifier);
        }
    }
    
    private void removeBoostFromTeammate(ServerPlayer teammate, UUID sourcePlayerId) {
        AttributeInstance alchemicalBoostInstance = teammate.getAttribute(ModAttributes.ALCHEMICAL_BOOST.get());
        if (alchemicalBoostInstance != null) {
            UUID modifierUuid = UUID.nameUUIDFromBytes((CONDUIT_BOOST_UUID.toString() + sourcePlayerId).getBytes());
            alchemicalBoostInstance.removeModifier(modifierUuid);

            Map<UUID, Double> teamBoosts = activeConduits.get(teammate.getUUID());
            if (teamBoosts != null) {
                teamBoosts.remove(sourcePlayerId);
                if (teamBoosts.isEmpty()) {
                    activeConduits.remove(teammate.getUUID());
                }
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    private double getSharePercentage(int amplifier) {
        return ModConfig.COMMON.conduitSharePercentage.get() * (amplifier + 1);
    }
}