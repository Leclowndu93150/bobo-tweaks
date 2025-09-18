package com.leclowndu93150.bobo_tweaks.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaySoundPacket {
    private final ResourceLocation soundLocation;
    private final SoundSource soundSource;
    private final double x, y, z;
    private final float volume, pitch;

    public PlaySoundPacket(SoundEvent sound, SoundSource source, double x, double y, double z, float volume, float pitch) {
        this.soundLocation = sound.getLocation();
        this.soundSource = source;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public PlaySoundPacket(FriendlyByteBuf buf) {
        this.soundLocation = buf.readResourceLocation();
        this.soundSource = buf.readEnum(SoundSource.class);
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(soundLocation);
        buf.writeEnum(soundSource);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(this::handleClientSide);
        return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    private void handleClientSide() {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundLocation);
            if (sound != null) {
                level.playLocalSound(x, y, z, sound, soundSource, volume, pitch, false);
            }
        }
    }
}