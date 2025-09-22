package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraftforge.registries.RegistryObject;

@Mixin(value = MagicManager.class, remap = false)
public class MagicManagerMixin {
    
    @ModifyVariable(
        method = "getEffectiveSpellCooldown",
        at = @At("STORE"),
        ordinal = 0
    )
    private static double modifyPlayerCooldownModifier(double playerCooldownModifier, AbstractSpell spell, Player player, io.redspace.ironsspellbooks.api.spells.CastSource castSource) {
        SchoolType school = spell.getSchoolType();
        double schoolSpecificCDR = bobo_tweaks$getSchoolSpecificCDR(player, school);
        
        if (schoolSpecificCDR != 1.0) {
            // Stack school-specific CDR with general CDR by multiplying them
            return playerCooldownModifier * schoolSpecificCDR;
        }
        
        return playerCooldownModifier;
    }
    
    @Unique
    private static double bobo_tweaks$getSchoolSpecificCDR(Player player, SchoolType school) {
        if (school == null) return 1.0;
        
        RegistryObject<Attribute> schoolAttribute = null;
        
        if (school.equals(SchoolRegistry.FIRE.get())) {
            schoolAttribute = ModAttributes.FIRE_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.ICE.get())) {
            schoolAttribute = ModAttributes.ICE_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.LIGHTNING.get())) {
            schoolAttribute = ModAttributes.LIGHTNING_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.HOLY.get())) {
            schoolAttribute = ModAttributes.HOLY_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.ENDER.get())) {
            schoolAttribute = ModAttributes.ENDER_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.BLOOD.get())) {
            schoolAttribute = ModAttributes.BLOOD_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.EVOCATION.get())) {
            schoolAttribute = ModAttributes.EVOCATION_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.NATURE.get())) {
            schoolAttribute = ModAttributes.NATURE_COOLDOWN_REDUCTION;
        } else if (school.equals(SchoolRegistry.ELDRITCH.get())) {
            schoolAttribute = ModAttributes.ELDRITCH_COOLDOWN_REDUCTION;
        }
        
        if (schoolAttribute != null) {
            return player.getAttributeValue(schoolAttribute.get());
        }
        
        return 1.0;
    }
}