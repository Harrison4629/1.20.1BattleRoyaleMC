package net.harrison.battleroyale;

import net.harrison.battleroyale.capabilities.temporary.GameBeginClearElytra;
import net.harrison.battleroyale.events.FireWorkEvent;
import net.harrison.battleroyale.events.GamingStartFallImmuneEvent;
import net.harrison.battleroyaleitem.capabilities.armorplate.NumofArmorPlate;
import net.harrison.battleroyaleitem.capabilities.armorplate.NumofArmorPlateProvider;
import net.harrison.battleroyaleitem.networking.s2cpacket.ArmorPlateSyncS2CPacket;
import net.harrison.soundmanager.init.ModMessages;
import net.harrison.soundmanager.networking.s2cpacket.PlaySoundToClientS2CPacket;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BattleroyaleManager {
    private static boolean isBattleRoyaleActive = false;
    private static MinecraftServer serverInstance;

    private static Vec3 hobby;
    private static Vec3 platform;

    public static Vec3 getHobby() {
        return hobby;
    }

    public static Vec3 getPlatform() {
        return platform;
    }

    public static void setHobby() {
        ServerLevel level = serverInstance.getLevel(ServerLevel.OVERWORLD);
        if (level != null) {
            for (Entity entity : level.getAllEntities()){
                if (entity instanceof ArmorStand armorStand) {
                    String tags = armorStand.getTags().toString();

                    if (tags.contains("hobby")) {
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
                    String tags = armorStand.getTags().toString();

                    if (tags.contains("platform")) {
                        platform = armorStand.position();
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

        setHobby();
        setPlatform();

        //***************************************************
        if (platform == null) {
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.literal("未找到带有'platform'标签的盔甲架"), false
            );
            return;
        }
        if (hobby == null) {
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.literal("未找到带有'hobby'标签的盔甲架"), false
            );
            return;
        }
        //***************************************************

        if (isBattleRoyaleActive) {
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.literal("大逃杀正在运行中！请等待其结束"), false
            );
            for (ServerPlayer player : serverInstance.getPlayerList().getPlayers()) {
                ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(
                        SoundEvents.VILLAGER_NO, 1.0F, 1.0F), player);
            }
            return;
        }

        if (!EnoughPreparedPlayer()){
            serverInstance.getPlayerList().broadcastSystemMessage(
                    Component.literal("玩家数量不足！"), false);
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
                GamingStartFallImmuneEvent.setImmune(player.getUUID());
                player.teleportTo(platform.x, platform.y, platform.z);
                player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
                GameBeginClearElytra.setClearElytra(player.getUUID(), true);
            }

            serverInstance.getCommands().performPrefixedCommand(
                    serverInstance.createCommandSourceStack().withSuppressedOutput(),
                    "function battleroyale:game/start"
            );

            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("§6Go! Go! Go!")));

            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.ANVIL_USE, 1.0F, 1.0F), player);
        }

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

            perPlayer.moveTo(
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
            LazyOptional<NumofArmorPlate> armorCapability = player.getCapability(
                    NumofArmorPlateProvider.NUMOF_ARMOR_PLATE_CAPABILITY);

            if (player.getTags().contains("inGame") ||
                    player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                player.getTags().remove("inGame");
                GamingStartFallImmuneEvent.resetImmune(player.getUUID());
                player.moveTo(hobby);

                player.setGameMode(GameType.ADVENTURE);
                player.getInventory().clearContent();
                player.setHealth(player.getMaxHealth());
                player.removeAllEffects();

                armorCapability.ifPresent(numofArmorPlate -> {
                    numofArmorPlate.subAllArmorPlate();
                    net.harrison.battleroyaleitem.init
                            .ModMessages.sendToPlayer(new ArmorPlateSyncS2CPacket(numofArmorPlate.getNumofArmorPlate()), player);
                });
            }
        }
    }
}
