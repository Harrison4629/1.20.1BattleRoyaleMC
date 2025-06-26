package net.harrison.battleroyale.events;

import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyaleitem.entities.airdrop.AirdropEntity;
import net.harrison.battleroyaleitem.init.ModEntities;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.harrison.beaconbeamdisplay.manager.BeaconBeamData;
import net.harrison.beaconbeamdisplay.networking.s2cpacket.BeamPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AirdropEvent {
    private static boolean hasSummoned = false;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        if (event.getServer() == null) {
            return;
        }

        //只支持主世界生成空投
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);

        if (!event.getRunningState()){
            if (level != null) {
                clearAirdrop(level);
            }
            return;
        }


        if(event.getStage() >= ZoneConfig.getMaxStage() - 1 - 2) {
            return;
        }

        ZoneStateEnum state = event.getState();
        Vec3 center = event.getZoneCenter();



        switch (state) {

            case IDLE:
                summonAirdrop(center, level, event);
                break;

            case WARNING:
                break;

            case SHRINKING:
                hasSummoned = false;
                break;

            default:
                break;
        }

    }

    private static void summonAirdrop(Vec3 center, ServerLevel level, ZoneStageEvent event) {
        if (hasSummoned) {
            return;
        }

        AirdropEntity airdrop = new AirdropEntity(ModEntities.AIRDROP.get(), level);
        ResourceLocation lootTableId = ResourceLocation.fromNamespaceAndPath("battleroyale", "airdrop");
        airdrop.setLootTable(lootTableId, level.getRandom().nextLong());

        double maxOffset = ZoneConfig.getZoneSize(event.getStage()) / 2.0 * 0.9;

        double x = center.x + (level.getRandom().nextDouble() - 0.5) * maxOffset;
        double z = center.z + (level.getRandom().nextDouble() - 0.5) * maxOffset;

        airdrop.setPos(x, center.y + 80, z);

        UUID randomUUID = UUID.randomUUID();
        float[] colors = {1.0F, 0.0F, 1.0F};
        BeaconBeamData beamData = new BeaconBeamData(new Vec3(x, 0, z), colors, 1.0F, 2.0F, 200);
        BeaconBeamData.DATA.put(randomUUID, beamData);
        net.harrison.beaconbeamdisplay.init.ModMessages.sendToAllPlayer(new BeamPacket(randomUUID, beamData));

        level.addFreshEntity(airdrop);

        event.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal(String.format("§e§l空投已降落在 x:%.1f z:%.1f 附近！", x, z)),
                false
        );

        for (ServerPlayer player : level.players()) {
            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F), player);
        }

        hasSummoned = true;
    }

    private static void clearAirdrop(ServerLevel level) {
        List<Entity> airdropToClear = new ArrayList<>(level.getEntities(ModEntities.AIRDROP.get(),
                airdropEntity -> true
        ));

        for (Entity airdrop : airdropToClear) {
            airdrop.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
