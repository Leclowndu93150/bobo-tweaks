package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.CAST_TIME_REDUCTION;

@Mixin(value = AbstractSpell.class, remap = false)
public abstract class AbstractSpellCastTimeMixin {
    
    @Shadow
    public abstract CastType getCastType();
    
    @ModifyVariable(
        method = "getEffectiveCastTime", 
        at = @At("STORE"),
        ordinal = 0
    )
    private double modifyEntityCastTimeModifier(double entityCastTimeModifier, int spellLevel, LivingEntity entity) {
        if (entity != null) {
            AbstractSpell spell = (AbstractSpell)(Object)this;
            SchoolType school = spell.getSchoolType();
            double schoolSpecificCTR = bobo_tweaks$getSchoolSpecificCTR(entity, school);
            
            if (schoolSpecificCTR != 1.0) {
                if (getCastType() != CastType.CONTINUOUS) {
                    // For non-continuous spells: stack school-specific CTR with general CTR
                    // Both use the 2 - Utils.softCapFormula() approach, so we need to reverse it first
                    double generalCTR = 2.0 - entityCastTimeModifier;
                    double combinedCTR = generalCTR * schoolSpecificCTR;
                    return 2 - Utils.softCapFormula(combinedCTR);
                } else {
                    // For continuous spells: stack by multiplying
                    return entityCastTimeModifier * schoolSpecificCTR;
                }
            }
        }
        
        return entityCastTimeModifier;
    }
    
    @Unique
    private static double bobo_tweaks$getSchoolSpecificCTR(LivingEntity entity, SchoolType school) {
        if (school == null) return 1.0;
        
        RegistryObject<Attribute> schoolAttribute = null;
        
        if (school.equals(SchoolRegistry.FIRE.get())) {
            schoolAttribute = ModAttributes.FIRE_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.ICE.get())) {
            schoolAttribute = ModAttributes.ICE_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.LIGHTNING.get())) {
            schoolAttribute = ModAttributes.LIGHTNING_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.HOLY.get())) {
            schoolAttribute = ModAttributes.HOLY_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.ENDER.get())) {
            schoolAttribute = ModAttributes.ENDER_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.BLOOD.get())) {
            schoolAttribute = ModAttributes.BLOOD_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.EVOCATION.get())) {
            schoolAttribute = ModAttributes.EVOCATION_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.NATURE.get())) {
            schoolAttribute = ModAttributes.NATURE_CAST_TIME_REDUCTION;
        } else if (school.equals(SchoolRegistry.ELDRITCH.get())) {
            schoolAttribute = ModAttributes.ELDRITCH_CAST_TIME_REDUCTION;
        }
        
        if (schoolAttribute != null) {
            return entity.getAttributeValue(schoolAttribute.get());
        }
        
        return 1.0;
    }
}