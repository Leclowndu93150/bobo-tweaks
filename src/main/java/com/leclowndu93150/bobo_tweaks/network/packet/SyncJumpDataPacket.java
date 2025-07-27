package com.leclowndu93150.bobo_tweaks.network.packet;

import com.leclowndu93150.bobo_tweaks.util.JumpTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncJumpDataPacket {
    private final int jumpsUsed;
    private final int maxJumps;

    public SyncJumpDataPacket(int jumpsUsed, int maxJumps) {
        this.jumpsUsed = jumpsUsed;
        this.maxJumps = maxJumps;
    }

    public SyncJumpDataPacket(FriendlyByteBuf buf) {
        this.jumpsUsed = buf.readInt();
        this.maxJumps = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(jumpsUsed);
        buf.writeInt(maxJumps);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                JumpTracker.updateClientJumpData(Minecraft.getInstance().player.getUUID(), jumpsUsed, maxJumps);
            }
        });
        return true;
    }
}