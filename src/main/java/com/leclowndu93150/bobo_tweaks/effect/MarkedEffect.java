package com.leclowndu93150.bobo_tweaks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MarkedEffect extends MobEffect {
    public MarkedEffect() {
        super(MobEffectCategory.HARMFUL, 0xDC143C);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}