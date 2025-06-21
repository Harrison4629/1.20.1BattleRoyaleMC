package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GamingStartFallImmuneEvent {
    private static final Map<UUID, Boolean> FALL_IMMUNE = new HashMap<>();

    private static boolean isImmune(UUID playerId) {
        return FALL_IMMUNE.getOrDefault(playerId, false);
    }

    public static void setImmune(UUID playerId) {
        FALL_IMMUNE.put(playerId, true);
    }

    public static void resetImmune(UUID playerId) {
        FALL_IMMUNE.remove(playerId);
    }

    @SubscribeEvent
    public static void onGamingStartFallImmune(LivingFallEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (isImmune(player.getUUID())) {
            event.setCanceled(true);
            resetImmune(player.getUUID());
        }
    }
}
