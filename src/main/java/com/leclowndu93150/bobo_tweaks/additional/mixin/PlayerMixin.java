package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.config.ModConfig;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.network.packet.ClientboundJumpPacket;
import com.leclowndu93150.bobo_tweaks.registry.ModAttributes;
import com.leclowndu93150.bobo_tweaks.util.PlayerJumpAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerJumpAccess {
    @Unique
    private int boboTweaks$jumpsUsed = 0;
    
    @Unique
    private boolean boboTweaks$jumpKeyPressed = false;
    
    @Unique
    private boolean boboTweaks$wasOnGround = true;
    
    @Unique
    private boolean boboTweaks$jumpKeyReleasedAfterLanding = false;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void boboTweaks$setJumpKeyPressed(boolean pressed) {
        if (!pressed && this.onGround()) {
            this.boboTweaks$jumpKeyReleasedAfterLanding = true;
        }
        
        this.boboTweaks$jumpKeyPressed = pressed;
    }

    @Override
    public boolean boboTweaks$isJumpKeyPressed() {
        return this.boboTweaks$jumpKeyPressed;
    }
    
    @Override
    public int boboTweaks$getJumpsUsed() {
        return this.boboTweaks$jumpsUsed;
    }
    
    @Override
    public void boboTweaks$setJumpsUsed(int jumps) {
        this.boboTweaks$jumpsUsed = jumps;
    }
    
    @Override
    public boolean boboTweaks$canMultiJump() {
        int maxJumps = boboTweaks$getMaxJumps();
        return !this.onGround() && this.boboTweaks$jumpsUsed < maxJumps - 1;
    }
    
    @Unique
    private int boboTweaks$getMaxJumps() {
        AttributeInstance jumpCountAttr = this.getAttribute(ModAttributes.JUMP_COUNT.get());
        if (jumpCountAttr != null) {
            return (int) Math.floor(jumpCountAttr.getValue());
        }
        return 1;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        
        if (player.level().isClientSide) return;
        
        boolean currentlyOnGround = player.onGround();
        
        if (currentlyOnGround && !boboTweaks$wasOnGround) {
            boboTweaks$jumpsUsed = 0;
            boboTweaks$jumpKeyReleasedAfterLanding = false;
        }

        if (!currentlyOnGround && boboTweaks$jumpKeyPressed && boboTweaks$canMultiJump()) {
            if (boboTweaks$jumpKeyReleasedAfterLanding || !boboTweaks$wasOnGround) {
                performAirJump(player);
                boboTweaks$jumpKeyPressed = false;
            }
        }
        
        boboTweaks$wasOnGround = currentlyOnGround;
    }
    
    @Unique
    private void performAirJump(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            boboTweaks$jumpsUsed++;

            float baseJumpPower = ModConfig.COMMON.airJumpVelocity.get().floatValue();
            float jumpBoostPower = player.getJumpBoostPower();
            double totalJumpPower = baseJumpPower + (jumpBoostPower * 0.1F);
            
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x(), totalJumpPower, motion.z());
            player.hasImpulse = true;
            
            for (int i = 0; i < 20; ++i) {
                double offsetX = serverLevel.random.nextGaussian() * 0.02D;
                double offsetY = serverLevel.random.nextGaussian() * 0.02D;
                double offsetZ = serverLevel.random.nextGaussian() * 0.02D;

                serverLevel.sendParticles(ParticleTypes.POOF, player.getX() + (player.getRandom().nextFloat() * player.getBbWidth() * 2.0F) - player.getBbWidth(), player.getY(), player.getZ() + (player.getRandom().nextFloat() * player.getBbWidth() * 2.0F) - player.getBbWidth(), 1, offsetX, offsetY, offsetZ, 0.0D);
            }
            
            if (player instanceof ServerPlayer serverPlayer) {
                ModNetworking.sendToPlayer(new ClientboundJumpPacket(totalJumpPower), serverPlayer);
            }
        }
    }
}