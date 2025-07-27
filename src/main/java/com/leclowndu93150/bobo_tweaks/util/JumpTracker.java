package com.leclowndu93150.bobo_tweaks.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JumpTracker {
    private static final Map<UUID, Integer> clientJumpsUsed = new HashMap<>();
    private static final Map<UUID, Integer> clientMaxJumps = new HashMap<>();
    
    public static void updateClientJumpData(UUID playerId, int jumpsUsed, int maxJumps) {
        clientJumpsUsed.put(playerId, jumpsUsed);
        clientMaxJumps.put(playerId, maxJumps);
    }
    
    public static int getClientJumpsUsed(UUID playerId) {
        return clientJumpsUsed.getOrDefault(playerId, 0);
    }
    
    public static int getClientMaxJumps(UUID playerId) {
        return clientMaxJumps.getOrDefault(playerId, 1);
    }
}