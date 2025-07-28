package com.leclowndu93150.bobo_tweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class AutoCrossbowReleasePacket {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutoCrossbowReleasePacket");
    
    private final InteractionHand hand;
    private final int chargeTicks;
    
    public AutoCrossbowReleasePacket(InteractionHand hand, int chargeTicks) {
        this.hand = hand;
        this.chargeTicks = chargeTicks;
    }
    
    public AutoCrossbowReleasePacket(FriendlyByteBuf buf) {
        this.hand = buf.readEnum(InteractionHand.class);
        this.chargeTicks = buf.readInt();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(hand);
        buf.writeInt(chargeTicks);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                LOGGER.info("Received AutoCrossbowReleasePacket for player: {}, hand: {}, chargeTicks: {}", player.getName().getString(), hand, chargeTicks);
                
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof CrossbowItem && player.isUsingItem()) {
                    LOGGER.info("Server releasing crossbow for player: {}", player.getName().getString());
                    
                    // Calculate time left based on charge ticks
                    int useDuration = stack.getUseDuration();
                    int timeLeft = useDuration - chargeTicks;
                    
                    // Release the crossbow on the server
                    stack.getItem().releaseUsing(stack, player.level(), player, timeLeft);
                    player.stopUsingItem();
                    
                    LOGGER.info("Server crossbow release completed");
                } else {
                    LOGGER.warn("Player not using crossbow or invalid item when trying to auto-release");
                }
            }
        });
        return true;
    }
}