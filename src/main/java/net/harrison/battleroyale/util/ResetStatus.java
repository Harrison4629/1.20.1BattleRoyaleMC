package net.harrison.battleroyale.util;

import net.harrison.battleroyaleitem.capabilities.armorplate.ArmorPlate;
import net.harrison.battleroyaleitem.capabilities.armorplate.ArmorPlateProvider;
import net.harrison.battleroyaleitem.capabilities.phasecore.PhaseCore;
import net.harrison.battleroyaleitem.capabilities.phasecore.PhaseCoreProvider;
import net.harrison.battleroyaleitem.init.ModMessages;
import net.harrison.battleroyaleitem.networking.s2cpacket.ArmorPlateSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.util.LazyOptional;

public class ResetStatus {
    public static void ResetPlayerStatus(ServerPlayer player) {
        LazyOptional<ArmorPlate> armorCapability = player.getCapability(ArmorPlateProvider.ARMOR_PLATE_CAPABILITY);
        LazyOptional<PhaseCore> phaseCoreCapability = player.getCapability(PhaseCoreProvider.PHASE_CORE_CAPABILITY);

        player.getInventory().clearContent();
        player.setHealth(player.getMaxHealth());
        player.removeAllEffects();
        player.setGameMode(GameType.ADVENTURE);

        armorCapability.ifPresent(armorPlate -> {
            armorPlate.subAllArmorPlate();
            ModMessages.sendToPlayer(new ArmorPlateSyncS2CPacket(armorPlate.getNumOfArmorPlate()), player);
        });

        phaseCoreCapability.ifPresent(PhaseCore::reset);
    }
}
