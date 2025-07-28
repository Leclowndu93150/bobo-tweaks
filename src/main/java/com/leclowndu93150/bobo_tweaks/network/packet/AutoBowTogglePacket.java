package com.leclowndu93150.bobo_tweaks.network.packet;

import com.leclowndu93150.bobo_tweaks.additional.autobow.config.AutoBowConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AutoBowTogglePacket {
    private final boolean enabled;
    
    public AutoBowTogglePacket(boolean enabled) {
        this.enabled = enabled;
    }
    
    public AutoBowTogglePacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            AutoBowConfig.VALUES.autoBowEnabled.set(enabled);

            if (Minecraft.getInstance().player != null) {
                String messageKey = enabled ? "msg.bobo_tweaks.auto_bow.enabled" : "msg.bobo_tweaks.auto_bow.disabled";
                Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(messageKey), true
                );
            }
        });
        return true;
    }
}