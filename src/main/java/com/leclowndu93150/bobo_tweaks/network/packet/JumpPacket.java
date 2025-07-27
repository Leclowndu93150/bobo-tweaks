package com.leclowndu93150.bobo_tweaks.network.packet;

import com.leclowndu93150.bobo_tweaks.util.PlayerJumpAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JumpPacket {
    private final boolean isJumping;

    public JumpPacket(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public JumpPacket(FriendlyByteBuf buf) {
        this.isJumping = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isJumping);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player instanceof PlayerJumpAccess jumpAccess) {
                jumpAccess.boboTweaks$setJumpKeyPressed(isJumping);
            }
        });
        return true;
    }
}