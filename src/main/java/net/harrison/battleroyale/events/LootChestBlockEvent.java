package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.data.LootChestBlockPosData;
import net.harrison.battleroyaleitem.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootChestBlockEvent {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!event.getLevel().isClientSide()) {

            if (event.getPlacedBlock().is(ModBlocks.LOOT_CHEST.get())) {
                ServerLevel serverLevel = (ServerLevel) event.getLevel();
                LootChestBlockPosData data = LootChestBlockPosData.get(serverLevel);
                data.addLocation(event.getPos());

                if (event.getEntity() instanceof Player player) {
                    player.sendSystemMessage(Component.literal("已添加"));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!event.getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();

            if (event.getState().getBlock() == ModBlocks.LOOT_CHEST.get()) {
                LootChestBlockPosData data = LootChestBlockPosData.get(serverLevel);
                data.removeLocation(pos);

                event.getPlayer().sendSystemMessage(Component.literal("已清除"));
            }
        }
    }

}
