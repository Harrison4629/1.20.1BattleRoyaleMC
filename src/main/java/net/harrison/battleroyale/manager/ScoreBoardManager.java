package net.harrison.battleroyale.manager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreBoardManager {
    private static final String DISPLAY_NAME = "RANK";


    public static void addScore(Player player, int score) {
        ServerLevel level = (ServerLevel) player.level();

        Scoreboard scoreboard = level.getScoreboard();
        Objective objective = scoreboard.getObjective(DISPLAY_NAME);
        if (objective == null) {
            scoreboard.addObjective(
                    DISPLAY_NAME,
                    ObjectiveCriteria.DUMMY,
                    Component.literal(DISPLAY_NAME).withStyle(ChatFormatting.GOLD),
                    ObjectiveCriteria.RenderType.INTEGER
            );
            Objective newObject = scoreboard.getObjective(DISPLAY_NAME);
            scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST, newObject);
        }
        scoreboard.getOrCreatePlayerScore(player.getScoreboardName(), objective).setScore(score);
    }
}
