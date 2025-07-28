package com.leclowndu93150.bobo_tweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AutoBowReleasePacket {
    
    private final InteractionHand hand;
    private final int drawTicks;
    
    public AutoBowReleasePacket(InteractionHand hand, int drawTicks) {
        this.hand = hand;
        this.drawTicks = drawTicks;
    }
    
    public AutoBowReleasePacket(FriendlyByteBuf buf) {
        this.hand = buf.readEnum(InteractionHand.class);
        this.drawTicks = buf.readInt();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(hand);
        buf.writeInt(drawTicks);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof BowItem && player.isUsingItem()) {
                    int useDuration = stack.getUseDuration();
                    int timeLeft = useDuration - drawTicks;
                    
                    stack.getItem().releaseUsing(stack, player.level(), player, timeLeft);
                    player.stopUsingItem();
                }
            }
        });
        return true;
    }
}