package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.effect.RejuvenatingShotsEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;canHarmPlayer(Lnet/minecraft/world/entity/player/Player;)Z"
        )
    )
    private boolean onCanHarmPlayer(Player shooter, Player target) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;

        if (RejuvenatingShotsEffect.tryHealAlly(arrow, shooter, target)) {
            arrow.discard();
            return false;
        }

        return shooter.canHarmPlayer(target);
    }
}
