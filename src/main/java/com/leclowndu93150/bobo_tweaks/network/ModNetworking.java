package com.leclowndu93150.bobo_tweaks.network;

import com.leclowndu93150.bobo_tweaks.BoboTweaks;
import com.leclowndu93150.bobo_tweaks.network.packet.ClientboundJumpPacket;
import com.leclowndu93150.bobo_tweaks.network.packet.JumpPacket;
import com.leclowndu93150.bobo_tweaks.network.packet.SyncJumpDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(BoboTweaks.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Register jump packets
        net.messageBuilder(JumpPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(JumpPacket::new)
                .encoder(JumpPacket::toBytes)
                .consumerMainThread(JumpPacket::handle)
                .add();
                
        net.messageBuilder(ClientboundJumpPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundJumpPacket::new)
                .encoder(ClientboundJumpPacket::toBytes)
                .consumerMainThread(ClientboundJumpPacket::handle)
                .add();
        
        net.messageBuilder(SyncJumpDataPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncJumpDataPacket::new)
                .encoder(SyncJumpDataPacket::toBytes)
                .consumerMainThread(SyncJumpDataPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}