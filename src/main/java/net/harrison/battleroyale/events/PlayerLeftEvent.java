package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.harrison.battleroyale.capabilities.temporary.GameBeginClearElytra;
import net.harrison.battleroyaleitem.capabilities.armorplate.NumofArmorPlate;
import net.harrison.battleroyaleitem.capabilities.armorplate.NumofArmorPlateProvider;
import net.harrison.battleroyaleitem.init.ModMessages;
import net.harrison.battleroyaleitem.networking.s2cpacket.ArmorPlateSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerLeftEvent {

    @SubscribeEvent
    public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)){
            return;
        }

        if (!BattleroyaleManager.getStatus()){
            return;
        }

        if (!player.getTags().contains("inGame")) {
            return;
        }

        player.getTags().remove("inGame");
        GamingStartFallImmuneEvent.resetImmune(player.getUUID());
        player.getInventory().clearContent();

        LazyOptional<NumofArmorPlate> armorCapability = player.getCapability(
                NumofArmorPlateProvider.NUMOF_ARMOR_PLATE_CAPABILITY);

        armorCapability.ifPresent(numofArmorPlate -> {
            numofArmorPlate.subAllArmorPlate();
            ModMessages.sendToPlayer(new ArmorPlateSyncS2CPacket(numofArmorPlate.getNumofArmorPlate()), player);
        });

        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        GameBeginClearElytra.resetClearElytra(player.getUUID());
    }
}
