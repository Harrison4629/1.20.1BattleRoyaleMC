package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.util.ResetStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
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

        Scoreboard scoreboard = event.getEntity().getScoreboard();
        PlayerTeam team = scoreboard.getPlayersTeam(player.getName().getString());
        if (team != null) {
            scoreboard.removePlayerFromTeam(player.getName().getString(), team);
        }

        player.kill();
        player.getTags().remove("inGame");
        ResetStatus.ResetPlayerStatus(player);
    }
}
