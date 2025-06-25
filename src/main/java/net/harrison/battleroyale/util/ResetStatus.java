package net.harrison.battleroyale.util;

import net.harrison.battleroyaleitem.capabilities.armorplate.NumofArmorPlate;
import net.harrison.battleroyaleitem.capabilities.armorplate.NumofArmorPlateProvider;
import net.harrison.battleroyaleitem.init.ModMessages;
import net.harrison.battleroyaleitem.networking.s2cpacket.ArmorPlateSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;

public class ResetStatus {


    public static void ResetPlayerStatus(ServerPlayer player) {
        LazyOptional<NumofArmorPlate> armorCapability = player.getCapability(
                NumofArmorPlateProvider.NUMOF_ARMOR_PLATE_CAPABILITY);

        player.getInventory().clearContent();
        player.setHealth(player.getMaxHealth());
        player.removeAllEffects();


        armorCapability.ifPresent(numofArmorPlate -> {
            numofArmorPlate.subAllArmorPlate();
            ModMessages.sendToPlayer(new ArmorPlateSyncS2CPacket(numofArmorPlate.getNumofArmorPlate()), player);
        });
    }
}
