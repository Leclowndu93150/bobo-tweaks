package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class ManaDrainEffect extends MobEffect {
    private static final float MANA_DRAIN_PER_LEVEL = 10.0f;
    
    public ManaDrainEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
        if (!pLivingEntity.level().isClientSide() && ModList.get().isLoaded("irons_spellbooks")) {
            try {
                var magicData = MagicData.getPlayerMagicData(pLivingEntity);
                float currentMana = magicData.getMana();
                float manaToDrain = MANA_DRAIN_PER_LEVEL * (pAmplifier + 1);
                
                magicData.setMana(Math.max(0, currentMana - manaToDrain));
                
                BoboTweaks.getLogger().debug("Drained {} mana from {}", manaToDrain, pLivingEntity.getName().getString());
            } catch (Exception e) {
                BoboTweaks.getLogger().warn("Failed to drain mana from entity", e);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return false;
    }
}