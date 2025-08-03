package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerJoinEvent {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {

        Player player = event.getEntity();
        ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();

        if (BattleroyaleManager.getStatus()){
            serverPlayer.setGameMode(GameType.SPECTATOR);

            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.battleroyale.player_spectator").withStyle(ChatFormatting.AQUA)));

            player.playNotifySound(SoundEvents.VILLAGER_TRADE, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else {
            if (BattleroyaleManager.getHobby() != null) {
                player.teleportTo(BattleroyaleManager.getHobby().x, BattleroyaleManager.getHobby().y, BattleroyaleManager.getHobby().z);
                player.setHealth(player.getMaxHealth());
            }
        }
    }
}
