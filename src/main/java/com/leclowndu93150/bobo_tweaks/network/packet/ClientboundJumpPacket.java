package com.leclowndu93150.bobo_tweaks.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundJumpPacket {
    private final double jumpVelocity;

    public ClientboundJumpPacket(double jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    public ClientboundJumpPacket(FriendlyByteBuf buf) {
        this.jumpVelocity = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(jumpVelocity);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(this::handleClientSide);
        return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    private void handleClientSide() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x(), jumpVelocity, motion.z());
        }
    }
}