package net.harrison.battleroyale;

import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.battleroyale.events.FireWorkEvent;
import net.harrison.battleroyale.util.ResetStatus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BattleroyaleManager {
    private static boolean isBattleRoyaleActive = false;
    private static MinecraftServer serverInstance;

    private static Vec3 hobby;

    private static final List<Vec3> platforms = new ArrayList<>();

    public static Vec3 getHobby() {
        return hobby;
    }


    public static void setHobby() {
        ServerLevel level = serverInstance.getLevel(ServerLevel.OVERWORLD);
        if (level != null) {
            for (Entity entity : level.getAllEntities()){
                if (entity instanceof ArmorStand armorStand) {
                    //String tags = armorStand.getTags().toString();

                    //if (tags.contains("hobby")) {
                    //    hobby = armorStand.position();
                    //}

                    if (armorStand.getTags().contains("hobby")) {
                        hobby = armorStand.position();
                    }
                }
            }
        }
    }

    private static void setPlatform() {
        ServerLevel level = serverInstance.getLevel(ServerLevel.OVERWORLD);
        if (level != null) {
            for (Entity entity : level.getAllEntities()){
                if (entity instanceof ArmorStand armorStand) {
                    //String tags = armorStand.getTags().toString();

                    //if (tags.contains("platform")) {
                    //    Vec3 position =armorStand.position();
                    //    platforms.add(position);
                    //}
                    if (armorStand.getTags().contains("platform")) {
                        Vec3 position = armorStand.position();
                        platforms.add(position);
                    }
                }
            }
        }
    }

    public static void getServer(MinecraftServer server){
        serverInstance = server;
    }

    public static boolean getStatus() {
        return isBattleRoyaleActive;
    }

    private static boolean EnoughPreparedPlayer() {
        int playerCount = 0;
        for (ServerPlayer player : serverInstance.getPlayerList().getPlayers()) {
            if (player.getTags().contains("prepared")) {
                playerCount++;
            }
        }
        return playerCount >= 2;
    }

    public static void startBattleRoyale() {
        Random random = new Random();

        setPlatform();

        if (platforms.isEmpty()) {
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.armor_stand_platform_not_found"), false
            );
            return;
        }
        if (hobby == null) {
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.armor_stand_hobby_not_found"), false
            );
            return;
        }


        if (isBattleRoyaleActive) {
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.game_running"), false
            );
            for (ServerPlayer player : serverInstance.getPlayerList().getPlayers()) {
                ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(
                        SoundEvents.VILLAGER_NO, 1.0F, 1.0F), player);
            }
            return;
        }

        if (!EnoughPreparedPlayer()){
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.battleroyale.not_enough_player_number").withStyle(ChatFormatting.RED), false);
            for (ServerPlayer player : serverInstance.getPlayerList().getPlayers()) {
                ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(
                        SoundEvents.VILLAGER_NO, 1.0F, 1.0F), player);
            }
            return;
        }

        for (ServerPlayer player : serverInstance.getPlayerList().getPlayers()) {
            
            if (player.getTags().contains("prepared")) {
                player.getTags().remove("prepared");
                player.getTags().add("inGame");

                ResetStatus.ResetPlayerStatus(player);

                int randomIndex = random.nextInt(platforms.size());
                Vec3 targetPlatform = platforms.get(randomIndex);
                player.teleportTo(targetPlatform.x, targetPlatform.y, targetPlatform.z);

                ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.ANVIL_USE, 1.0F, 1.0F), player);
            } else {
                player.setGameMode(GameType.SPECTATOR);

                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.battleroyale.player_spectator")));

            }

        }

        serverInstance.getCommands().performPrefixedCommand(
                serverInstance.createCommandSourceStack().withSuppressedOutput(),
                "function battleroyale:game/start"
        );

        isBattleRoyaleActive = true;
    }

    public static void endCelebration(ServerPlayer player) {
        double radius = 8.0F;

        Component playerNameComponent = player.getName();
        MutableComponent message = Component.empty();

        message.append(Component.literal("A")
                .withStyle(ChatFormatting.OBFUSCATED));
        message.append(Component.literal("玩")
                .withStyle(ChatFormatting.YELLOW));
        message.append(Component.literal("家")
                .withStyle(ChatFormatting.DARK_GREEN));
        message.append(Component.literal(" ")
                .withStyle(ChatFormatting.DARK_AQUA));
        message.append(playerNameComponent);
        message.append(Component.literal("胜")
                .withStyle(ChatFormatting.DARK_RED));
        message.append(Component.literal("利!")
                .withStyle(ChatFormatting.DARK_PURPLE));
        message.append(Component.literal("A")
                .withStyle(ChatFormatting.OBFUSCATED));

        serverInstance.getPlayerList().broadcastSystemMessage(message, false);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
        FireWorkEvent.setPlayTimesAndPos(12, player.getPosition(1.0F), player.level());

        List<ServerPlayer> spectatorPlayers = new ArrayList<>();
        for (ServerPlayer serverPlayer : serverInstance.getPlayerList().getPlayers()) {
            if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                spectatorPlayers.add(serverPlayer);
            }
        }

        if (spectatorPlayers.isEmpty()) return;

        Vec3 center = player.getPosition(1.0F);

        double angleStep = 2 * Math.PI / spectatorPlayers.size();

        for (int i = 0; i < spectatorPlayers.size(); i++) {
            ServerPlayer perPlayer = spectatorPlayers.get(i);
            double angle = i * angleStep;

            perPlayer.teleportTo(
                    center.x + radius * Math.cos(angle),
                    center.y + 5,
                    center.z + radius * Math.sin(angle)
            );

            Vec3 eyeDirection = perPlayer.getEyePosition(1.0F).vectorTo(center).normalize();


            double yaw = Math.toDegrees(Math.atan2(eyeDirection.z, eyeDirection.x)) - 90.0F;
            Vec3 helpVec = new Vec3(eyeDirection.x, 0 ,eyeDirection.z);
            double pitch = -Math.toDegrees(Math.atan2(eyeDirection.y, helpVec.length()));

            perPlayer.setYRot((float) yaw);
            perPlayer.setXRot((float) pitch);

            perPlayer.connection.send(new ClientboundPlayerPositionPacket(
                    perPlayer.getX(),
                    perPlayer.getY(),
                    perPlayer.getZ(),
                    (float)yaw,
                    (float)pitch,
                    Set.of(),
                    0
            ));
        }
    }

    public static void endBattleRoyale() {
        if (!isBattleRoyaleActive) {
            return;
        }

        isBattleRoyaleActive = false;
        serverInstance.getCommands().performPrefixedCommand(
                serverInstance.createCommandSourceStack().withSuppressedOutput(),
                "function battleroyale:game/end"
        );


        for (ServerPlayer player : serverInstance.getPlayerList().getPlayers()) {
            if (player.getTags().contains("inGame") ||
                    player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                player.getTags().remove("inGame");
                player.teleportTo(hobby.x, hobby.y, hobby.z);
                ResetStatus.ResetPlayerStatus(player);
            }
        }
    }
}
