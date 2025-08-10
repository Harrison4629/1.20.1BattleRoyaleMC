package net.harrison.battleroyale.manager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;


public class TeamManager {

    private static final ChatFormatting[] colors = {
            ChatFormatting.BLACK,
            ChatFormatting.DARK_BLUE,
            ChatFormatting.DARK_GREEN,
            ChatFormatting.DARK_AQUA,
            ChatFormatting.DARK_RED,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.GOLD,
            ChatFormatting.GRAY,
            ChatFormatting.DARK_GRAY,
            ChatFormatting.BLUE,
            ChatFormatting.GREEN,
            ChatFormatting.AQUA,
            ChatFormatting.RED,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.YELLOW
    };


    public static void createTeams(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        for (ChatFormatting color : colors) {
            String colorName = color.getName();

            if (scoreboard.getPlayerTeam(colorName) == null) {
                PlayerTeam team = scoreboard.addPlayerTeam(colorName);
                team.setColor(color);
                team.setAllowFriendlyFire(false);
            }
        }
    }

    public static void removeAllPlayersFromTeams(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        for (Player player : server.getPlayerList().getPlayers()) {
            scoreboard.removePlayerFromTeam(player.getName().getString());
        }
    }
}
