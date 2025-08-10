package net.harrison.battleroyale.data;

import net.harrison.battleroyale.manager.ScoreBoardManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.PlayerTeam;

import java.util.*;

public class RankData extends SavedData {
    private static final String ID = "player_score_data";
    private final Map<UUID, Integer> scores = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        for (Map.Entry<UUID, Integer> score : scores.entrySet()) {
            CompoundTag playerDataTag = new CompoundTag();
            playerDataTag.putUUID("uuid", score.getKey());
            playerDataTag.putInt("score", score.getValue());
            list.add(playerDataTag);
        }
        tag.put("player_scores", list);
        return tag;
    }


    public void addScore(Player player, int num) {
        UUID playerUUID = player.getUUID();
        int currentScore = scores.getOrDefault(playerUUID, 0);
        scores.put(playerUUID, currentScore + num);


        //Todo:临时使用原版的计分板
        ScoreBoardManager.addScore(player, currentScore + num);


        setDirty();
    }

    public static RankData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(RankData::create, RankData::new, ID);
    }

    private static RankData create(CompoundTag tag) {
        RankData data = new RankData();
        ListTag list = tag.getList("player_scores", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag playerDataTag = list.getCompound(i);
            UUID playerUUID = playerDataTag.getUUID("uuid");
            int score = playerDataTag.getInt("score");
            data.scores.put(playerUUID, score);
        }
        return data;
    }

    public static void addPlayerScore(Player player, int num) {
        RankData.get((ServerLevel) player.level()).addScore(player, num);
    }

    public static void addTeamPlayerScore(PlayerTeam team, ServerLevel level, int num) {
        for (String teamPlayerName : team.getPlayers()) {
            Player player = level.getServer().getPlayerList().getPlayerByName(teamPlayerName);
            if (player != null) {
                RankData.get(level).addScore(player, num);
            }
        }
    }
}
