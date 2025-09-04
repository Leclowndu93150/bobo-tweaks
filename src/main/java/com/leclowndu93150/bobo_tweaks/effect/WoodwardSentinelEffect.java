package com.leclowndu93150.bobo_tweaks.effect;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class WoodwardSentinelEffect extends MobEffect {
    
    public WoodwardSentinelEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B4513);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}