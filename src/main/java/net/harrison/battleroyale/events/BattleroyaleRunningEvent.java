package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.harrison.battleroyale.capabilities.temporary.GameBeginClearElytra;
import net.harrison.soundmanager.init.ModMessages;
import net.harrison.soundmanager.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BattleroyaleRunningEvent {
    private static boolean endCelebration = false;

    private static int delay = 0;
    private final static int delayTime = 160;

    @SubscribeEvent
    public static void onBattleroyaleRunning(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.side.isClient()) {
            return;
        }

        if (!BattleroyaleManager.getStatus()) {
            return;
        }

        int numOfPlayerInGame = 0;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.getTags().contains("inGame")) {
                numOfPlayerInGame++;
            }
        }

        if (numOfPlayerInGame < 2) {
            delay++;
            if (!endCelebration) {
                for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                    if (player.getTags().contains("inGame")) {
                        BattleroyaleManager.endCelebration(player);
                    }
                }
                endCelebration = true;
            }

            if (delay >= delayTime) {
                BattleroyaleManager.endBattleRoyale();
                delay = 0;
                endCelebration = false;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!BattleroyaleManager.getStatus()) {
            return;
        }

        List<ItemStackInSlot> allItems = new ArrayList<>();
        Inventory inventory = player.getInventory();

        if (player.getTags().contains("inGame")) {
            player.getTags().remove("inGame");

            for (int i = 0;i < inventory.items.size(); i++) {
                ItemStack stack = inventory.items.get(i);
                if (!stack.isEmpty()) {
                    allItems.add(new ItemStackInSlot(stack, i, InventorySection.MAIN));
                }
            }
            for (int i = 0; i < inventory.armor.size(); ++i) {
                ItemStack stack = inventory.armor.get(i);
                if (!stack.isEmpty()) {
                    allItems.add(new ItemStackInSlot(stack, i, InventorySection.ARMOR));
                }
            }
            if (!inventory.offhand.get(0).isEmpty()) {
                allItems.add(new ItemStackInSlot(inventory.offhand.get(0), 0, InventorySection.OFFHAND));
            }
            Collections.shuffle(allItems);
            int numOfItemToDrop = (int) Math.ceil(allItems.size() * 0.4);

            int count = 0;
            for (ItemStackInSlot itemToDrop : allItems) {
                if (count >= numOfItemToDrop) {
                    break;
                }

                ItemEntity item = new ItemEntity(player.level(), player.getX(), player.getY() + 1, player.getZ(),
                        itemToDrop.stack);
                item.setPickUpDelay(5);
                player.level().addFreshEntity(item);
                count++;
            }

            player.getInventory().clearContent();

            player.setGameMode(GameType.SPECTATOR);

            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("§e您已阵亡，仍可继续观战")));

            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.VILLAGER_DEATH, 1.0F, 1.0F), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.side.isClient()) {
            return;
        }

        if (!BattleroyaleManager.getStatus()) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.getTags().contains("inGame")) {
            if (GameBeginClearElytra.getClearElytra(player.getUUID())) {
                if (player.getY() < BattleroyaleManager.getLowestPlatforms().y - 10 && player.onGround()) {
                        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                        GameBeginClearElytra.resetClearElytra(player.getUUID());
                }
            }
        }
    }

    private record ItemStackInSlot(ItemStack stack, int slot, InventorySection section) {
    }

    private enum InventorySection {
        MAIN, ARMOR, OFFHAND
    }
}
