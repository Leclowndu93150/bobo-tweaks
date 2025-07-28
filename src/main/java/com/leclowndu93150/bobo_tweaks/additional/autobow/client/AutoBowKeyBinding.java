package com.leclowndu93150.bobo_tweaks.additional.autobow.client;

import com.leclowndu93150.bobo_tweaks.additional.autobow.config.AutoBowConfig;
import com.leclowndu93150.bobo_tweaks.network.ModNetworking;
import com.leclowndu93150.bobo_tweaks.network.packet.AutoBowTogglePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class AutoBowKeyBinding {
    public static final String CATEGORY = "key.categories.bobo_tweaks";
    
    public static final KeyMapping AUTO_BOW_TOGGLE = new KeyMapping(
            "key.bobo_tweaks.auto_bow_toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );
    
    public static void toggleAutoBow() {
        boolean currentValue = AutoBowConfig.VALUES.autoBowEnabled.get();
        boolean newValue = !currentValue;
        
        ModNetworking.sendToServer(new AutoBowTogglePacket(newValue));
    }
}