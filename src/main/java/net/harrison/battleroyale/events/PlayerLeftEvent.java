package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.harrison.battleroyale.util.ResetStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerLeftEvent {

    @SubscribeEvent
    public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)){
            return;
        }

        if (!BattleroyaleManager.getStatus()){
            return;
        }

        if (!player.getTags().contains("inGame")) {
            return;
        }

        player.getTags().remove("inGame");
        ResetStatus.ResetPlayerStatus(player);
    }
}
