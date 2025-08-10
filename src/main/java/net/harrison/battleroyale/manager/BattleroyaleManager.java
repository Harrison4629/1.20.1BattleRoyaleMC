package net.harrison.battleroyale.manager;

import net.harrison.battleroyale.data.RankData;
import net.harrison.battleroyale.data.ServerData;
import net.harrison.battleroyale.events.CelebrationEvent;
import net.harrison.battleroyale.util.ResetStatus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.*;

public class BattleroyaleManager {
    private boolean isGaming;
    private final ServerLevel level;
    private int leftTicks;
    private static final int ticksBetweenCelebrationAndEnd = 100;

    private boolean isEndgameCountdownActive = false;


    public BattleroyaleManager(Level level) {
        this.level = (ServerLevel) level;
        this.isGaming = false;
        this.leftTicks = ticksBetweenCelebrationAndEnd;
    }


    public void tick() {
        if (!isGaming) {
            return;
        }

        if (isEndgameCountdownActive) {
            if (leftTicks > 0) {
                leftTicks--;
            } else {
                endGame();
            }
        } else {
            PlayerTeam winningTeam = getActiveTeamIfOnlyOneLeft();
            if (winningTeam != null) {
                celebrationInTheEnd(winningTeam);
                isEndgameCountdownActive = true;
            }
        }
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public void onPlayerDeath(ServerPlayer deathPlayer, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer sourcePlayer) {//击杀者加分
            MinecraftServer server =  sourcePlayer.level().getServer();
            if (server != null) {
                server.getPlayerList().broadcastSystemMessage(Component.translatable( "message.battleroyale.earn_score", sourcePlayer.getName().toString()), false);
                RankData.addPlayerScore(sourcePlayer, 1);
            }
        }

        //Todo:玩家传送回原地，而不是大厅
        if (deathPlayer.getTags().contains("inGame")) {
            deathPlayer.getTags().remove("inGame");
            deathPlayer.getInventory().dropAll();
            deathPlayer.setGameMode(GameType.SPECTATOR);
            deathPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.battleroyale.player_dead").withStyle(ChatFormatting.YELLOW)));
            deathPlayer.playNotifySound(SoundEvents.VILLAGER_DEATH, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    public void startGame() {
        Random random = new Random();
        ServerData data =  ServerData.get(level);

        if (data.getPlatformLocations().isEmpty()) {
            this.level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.armor_stand_platform_not_found"), false
            );
            return;
        }
        if (data.getHobbyLocation() == null) {
            this.level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.armor_stand_hobby_not_found"), false
            );
            return;
        }

        if (isGaming) {
            this.level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.game_running"), false
            );
            for (ServerPlayer player : this.level.getServer().getPlayerList().getPlayers()) {
                player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.HOSTILE, 1.0F, 1.0F);
            }
            return;
        }

        if (!EnoughPreparedPlayer()){
            this.level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.not_enough_player_number").withStyle(ChatFormatting.RED), false);
            for (ServerPlayer player : this.level.getServer().getPlayerList().getPlayers()) {
                player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.HOSTILE, 1.0F, 1.0F);
            }
            return;
        }

        for (ServerPlayer player : this.level.getServer().getPlayerList().getPlayers()) {

            if (player.getTags().contains("prepared")) {
                player.getTags().remove("prepared");
                player.getTags().add("inGame");

                ResetStatus.ResetPlayerStatus(player);

                List<BlockPos> list = new ArrayList<>(data.getPlatformLocations());
                Vec3 targetPlatform = list.get(random.nextInt(list.size())).getCenter();
                player.teleportTo(targetPlatform.x, targetPlatform.y, targetPlatform.z);

                player.playNotifySound(SoundEvents.ANVIL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
            } else {
                player.setGameMode(GameType.SPECTATOR);

                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.battleroyale.player_spectator")));
            }
        }

        this.level.getServer().getCommands().performPrefixedCommand(
                this.level.getServer().createCommandSourceStack().withSuppressedOutput(),
                "function battleroyale:game/start"
        );

        isGaming = true;
    }

    public void celebrationInTheEnd(PlayerTeam lastTeam) {
        MinecraftServer server = level.getServer();

        sendMessage(lastTeam.getName());

        RankData.addTeamPlayerScore(lastTeam, level, 3);

        //随便选一个玩家作为中心
        List<String> playerNames = new ArrayList<>(lastTeam.getPlayers());
        Collections.shuffle(playerNames);
        ServerPlayer player = server.getPlayerList().getPlayerByName(playerNames.get(0));
        if (player != null) {
            CelebrationEvent.setPlayTimesAndPos(12, player.getPosition(1.0F), level);
            around(player);
        }
        server.getPlayerList().getPlayers().forEach(
                (eachPlayer) -> eachPlayer.playNotifySound(SoundEvents.ENDER_DRAGON_AMBIENT, SoundSource.NEUTRAL, 1.0F, 1.0F)
        );
    }

    public void endGame() {
        MinecraftServer server = level.getServer();

        isGaming = false;
        level.getServer().getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                "function battleroyale:game/end"
        );

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.getTags().contains("inGame") ||
                    player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                player.getTags().remove("inGame");

                Vec3 hobby = ServerData.get(level).getHobbyLocation().getCenter();
                player.teleportTo(hobby.x, hobby.y, hobby.z);

                ResetStatus.ResetPlayerStatus(player);
            }
        }
        TeamManager.removeAllPlayersFromTeams(server);
    }

    public boolean isRunning() {
        return isGaming;
    }

    private boolean EnoughPreparedPlayer() {

        Scoreboard scoreboard = level.getScoreboard();

        Collection<PlayerTeam> playerTeams = scoreboard.getPlayerTeams();

        int teamsWithPlayersCount = 0;
        for (PlayerTeam team : playerTeams) {
            if (!team.getPlayers().isEmpty()) {
                teamsWithPlayersCount++;
            }
        }
        return teamsWithPlayersCount >= 2;
    }

    private PlayerTeam getActiveTeamIfOnlyOneLeft() {
        Scoreboard scoreboard = level.getScoreboard();

        Collection<PlayerTeam> playerTeams = scoreboard.getPlayerTeams();
        PlayerTeam lastActiveTeam = null;

        int teamsWithPlayersCount = 0;
        for (PlayerTeam team : playerTeams) {
            if (!team.getPlayers().isEmpty() && team.getPlayers().stream().anyMatch(
                    (playerName) -> {/* 队伍里面至少有1名不是观战的玩家*/
                ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(playerName);
                return player != null && player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR;
            })
            ) {
                lastActiveTeam = team;
                teamsWithPlayersCount++;
            }
        }

        if (teamsWithPlayersCount < 2) {
            return lastActiveTeam;
        } else {
            return null;
        }
    }

    private void around(Player player) {
        double radius = 8.0F;

        List<ServerPlayer> spectatorPlayers = level.getServer().getPlayerList().getPlayers().stream()
                .filter(serverPlayer -> serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
                .toList();

        if (spectatorPlayers.isEmpty()) return;

        Vec3 center = player.getPosition(1.0F);

        double angleStep = 2 * Math.PI / spectatorPlayers.size();

        for (int i = 0; i < spectatorPlayers.size(); i++) {
            ServerPlayer spectatorPlayer = spectatorPlayers.get(i);
            double angle = i * angleStep;

            Vec3 eyeDirection = spectatorPlayer.getEyePosition(1.0F).vectorTo(center).normalize();

            double yaw = Math.toDegrees(Math.atan2(eyeDirection.z, eyeDirection.x)) - 90.0F;
            Vec3 helpVec = new Vec3(eyeDirection.x, 0, eyeDirection.z);
            double pitch = -Math.toDegrees(Math.atan2(eyeDirection.y, helpVec.length()));

            spectatorPlayer.teleportTo(
                    level,
                    center.x + radius * Math.cos(angle),
                    center.y + 5,
                    center.z + radius * Math.sin(angle),
                    (float) yaw,
                    (float) pitch
            );
        }
    }

    private void sendMessage(String teamName) {
        MinecraftServer server = level.getServer();
        Component teamNameComponent = Component.literal(teamName);
        MutableComponent message = Component.empty();

        message.append(Component.literal("A")
                .withStyle(ChatFormatting.OBFUSCATED));
        message.append(Component.literal("队")
                .withStyle(ChatFormatting.YELLOW));
        message.append(Component.literal("伍")
                .withStyle(ChatFormatting.DARK_GREEN));
        message.append(Component.literal(" ")
                .withStyle(ChatFormatting.DARK_AQUA));
        message.append(teamNameComponent);
        message.append(Component.literal("胜")
                .withStyle(ChatFormatting.DARK_RED));
        message.append(Component.literal("利!")
                .withStyle(ChatFormatting.DARK_PURPLE));
        message.append(Component.literal("A")
                .withStyle(ChatFormatting.OBFUSCATED));

        server.getPlayerList().broadcastSystemMessage(message, false);

        server.getPlayerList().broadcastSystemMessage(Component.literal("同队伍玩家获得分数: +3"), false);
    }
}
