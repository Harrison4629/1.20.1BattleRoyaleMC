package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.harrison.soundmanager.init.ModMessages;
import net.harrison.soundmanager.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerJoinEvent {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)){
            return;
        }

        if (BattleroyaleManager.getStatus()){
            player.setGameMode(GameType.SPECTATOR);

            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("§b您正在观战一场游戏")));

            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.VILLAGER_TRADE, 1.0F, 1.0F), player);
        } else {
            if (BattleroyaleManager.getHobby() != null) {
                player.teleportTo(BattleroyaleManager.getHobby().x, BattleroyaleManager.getHobby().y, BattleroyaleManager.getHobby().z);
                player.setHealth(player.getMaxHealth());
            }
        }
    }
}
