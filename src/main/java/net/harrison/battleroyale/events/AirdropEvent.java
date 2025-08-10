package net.harrison.battleroyale.events;

import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyaleitem.entities.AirdropEntity;
import net.harrison.battleroyaleitem.init.ModEntities;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.harrison.beaconbeamdisplay.manager.BeaconBeamData;
import net.harrison.beaconbeamdisplay.networking.s2cpacket.BeamPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AirdropEvent {
    private static final int AIRDROP_HEIGHT = 80;
    private static final float[] BEACON_BEAM_COLOR = {1.0F, 0.0F, 1.0F};
    //private static final int[] EXCLUDED_STAGE = {0, ZoneData.getMaxStage() - 2, ZoneData.getMaxStage() - 1, ZoneData.getMaxStage()};
    private static final int[] EXCLUDED_STAGE = {0, 6, 7, 8, 9, 10};
    private static final String AIRDROP_TAG = "gameAirdrop";

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        ServerLevel level = (ServerLevel) event.getLevel();

        if (!event.getRunningState()){
            clearAirdrop(level);
            return;
        }


        if (event.getState() == ZoneStateEnum.IDLE) {
            summonAirdrop(event);
        }
    }

    private static void summonAirdrop(ZoneStageEvent event) {
        ServerLevel level = (ServerLevel) event.getLevel();

        for (int excludedStage : EXCLUDED_STAGE) {
            if (event.getStage() == excludedStage) {
                return;
            }
        }

        AirdropEntity airdrop = new AirdropEntity(ModEntities.AIRDROP.get(), level);
        ResourceLocation lootTableId = ResourceLocation.fromNamespaceAndPath(Battleroyale.MODID, "airdrop");
        airdrop.setLootTable(lootTableId, 0);
        airdrop.addTag(AIRDROP_TAG);

        double maxOffset = ZoneData.getZoneSize(event.getStage()) * 0.9;
        double x = event.getNextZoneCenter().x + (level.getRandom().nextDouble() - 0.5) * maxOffset;
        double z = event.getNextZoneCenter().z + (level.getRandom().nextDouble() - 0.5) * maxOffset;

        airdrop.setPos(x, event.getNextZoneCenter().y + AIRDROP_HEIGHT, z);

        UUID randomUUID = UUID.randomUUID();
        BeaconBeamData beamData = new BeaconBeamData(new Vec3(x, 0, z), BEACON_BEAM_COLOR, 1.0F, 2.0F, 200);
        BeaconBeamData.DATA.put(randomUUID, beamData);
        net.harrison.beaconbeamdisplay.init.ModMessages.sendToAllPlayer(new BeamPacket(randomUUID, beamData));

        level.addFreshEntity(airdrop);

        Objects.requireNonNull(event.getLevel().getServer()).getPlayerList().broadcastSystemMessage(
                Component.translatable("airdrop.battleroyale.landed").withStyle(ChatFormatting.YELLOW).append(String.format("§l x:%.1f z:%.1f !", x, z)),
                false
        );

        for (ServerPlayer player : level.players()) {
            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F), player);
        }
    }

    private static void clearAirdrop(ServerLevel level) {
        List<Entity> airdropToClear = new ArrayList<>(level.getEntities(ModEntities.AIRDROP.get(),
                airdropEntity -> airdropEntity.getTags().contains(AIRDROP_TAG)
        ));

        for (Entity airdrop : airdropToClear) {
            airdrop.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
