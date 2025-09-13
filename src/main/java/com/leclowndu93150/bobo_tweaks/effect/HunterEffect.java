package com.leclowndu93150.bobo_tweaks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HunterEffect extends MobEffect {
    public HunterEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x228B22);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}