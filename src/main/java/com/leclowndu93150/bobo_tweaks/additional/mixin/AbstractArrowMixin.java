package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.effect.RejuvenatingShotsEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;canHarmPlayer(Lnet/minecraft/world/entity/player/Player;)Z"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void onBeforeCanHarmPlayerCheck(CallbackInfo ci,
            boolean flag,
            Vec3 vec3,
            BlockPos blockpos,
            BlockState blockstate,
            Vec3 vec32,
            Vec3 vec33,
            HitResult hitresult,
            EntityHitResult entityhitresult,
            Entity entity,
            Entity entity1
    ) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;

        if (!(entity instanceof LivingEntity target)) {
            return;
        }

        if (!(entity1 instanceof Player shooter)) {
            return;
        }

        if (RejuvenatingShotsEffect.tryHealAlly(arrow, shooter, target)) {
            arrow.discard();
            ci.cancel();
        }
    }
}
