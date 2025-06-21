package net.harrison.battleroyale.capabilities.temporary;

import java.util.HashMap;
import java.util.UUID;

public class GameBeginClearElytra {
    static HashMap<UUID, Boolean> clearElytra = new HashMap<>();
    public static void setClearElytra(UUID playerId, boolean value) {
        clearElytra.put(playerId, value);
    }
    public static boolean getClearElytra(UUID playerId) {
        return clearElytra.getOrDefault(playerId, false);
    }
    public static void resetClearElytra(UUID playerId) {
        clearElytra.remove(playerId);
    }
}
