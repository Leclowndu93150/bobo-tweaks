package com.leclowndu93150.bobo_tweaks.additional.parrysystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParryData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File DATA_FILE = new File("config/bobotweaks/parry_data.json");
    private static final Map<UUID, ParryAbilityData> playerData = new HashMap<>();
    
    public static class ParryAbilityData {
        public String abilityType = "none";
        public long lastShieldRaiseTime = 0;
        
        public ParryAbilityData() {}
        
        public ParryAbilityData(String abilityType) {
            this.abilityType = abilityType;
        }
    }
    
    public static ParryAbilityData getPlayerData(Player player) {
        return playerData.computeIfAbsent(player.getUUID(), uuid -> new ParryAbilityData());
    }
    
    public static void setPlayerAbility(UUID playerUuid, String abilityType) {
        ParryAbilityData data = playerData.computeIfAbsent(playerUuid, uuid -> new ParryAbilityData());
        data.abilityType = abilityType;
        save();
    }
    
    public static void load() {
        if (DATA_FILE.exists()) {
            try (FileReader reader = new FileReader(DATA_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                
                if (json != null && json.has("players")) {
                    JsonObject players = json.getAsJsonObject("players");
                    for (String uuidStr : players.keySet()) {
                        try {
                            UUID uuid = UUID.fromString(uuidStr);
                            JsonObject playerJson = players.getAsJsonObject(uuidStr);
                            ParryAbilityData data = new ParryAbilityData();
                            
                            if (playerJson.has("ability_type")) {
                                data.abilityType = playerJson.get("ability_type").getAsString();
                            }
                            
                            playerData.put(uuid, data);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void save() {
        try {
            DATA_FILE.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            JsonObject players = new JsonObject();
            
            for (Map.Entry<UUID, ParryAbilityData> entry : playerData.entrySet()) {
                JsonObject playerJson = new JsonObject();
                playerJson.addProperty("ability_type", entry.getValue().abilityType);
                players.add(entry.getKey().toString(), playerJson);
            }
            
            json.add("players", players);
            
            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                GSON.toJson(json, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateShieldRaiseTime(Player player, long gameTime) {
        ParryAbilityData data = getPlayerData(player);
        data.lastShieldRaiseTime = gameTime;
    }
    
    public static boolean hasParryAbility(Player player) {
        ParryAbilityData data = getPlayerData(player);
        return !data.abilityType.equals("none");
    }
    
    public static String getAbilityType(Player player) {
        return getPlayerData(player).abilityType;
    }
}
