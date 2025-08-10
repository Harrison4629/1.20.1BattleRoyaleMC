package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.data.ServerData;
import net.harrison.battleroyale.init.ModMessages;
import net.harrison.battleroyale.networking.s2cpacket.MarkerDataS2CPacket;
import net.harrison.battleroyalezone.data.ServerMapData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerJoinEvent {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {

        ServerPlayer player = (ServerPlayer) event.getEntity();

        ServerMapData.get(player.serverLevel()).pushMapData(player);

        ServerData data = ServerData.get((ServerLevel) event.getEntity().level());

        if (GamingEvent.getBattleroyaleManager(event.getEntity().level()) != null){
            player.setGameMode(GameType.SPECTATOR);

            player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.battleroyale.player_spectator").withStyle(ChatFormatting.AQUA)));

            player.playNotifySound(SoundEvents.VILLAGER_TRADE, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else {
            if (data.getHobbyLocation() != null ) {
                Vec3 hobby = data.getHobbyLocation().getCenter();
                player.teleportTo(hobby.x, hobby.y, hobby.z);
                player.setHealth(player.getMaxHealth());
            }
        }


        ModMessages.sendToPlayer(new MarkerDataS2CPacket(data.getPlatformLocations(), data.getHobbyLocation()), player);
    }
}
