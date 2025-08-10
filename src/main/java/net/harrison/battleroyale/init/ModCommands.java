package net.harrison.battleroyale.init;

import com.mojang.brigadier.CommandDispatcher;
import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.data.lootChestData.CommonLootChestBlockPosData;
import net.harrison.battleroyale.data.lootChestData.EpicLootChestBlockPosData;
import net.harrison.battleroyale.data.lootChestData.LootChestBlockPosData;
import net.harrison.battleroyale.data.lootChestData.RareLootChestBlockPosData;
import net.harrison.battleroyale.events.GamingEvent;
import net.harrison.battleroyaleitem.block.LootChestBlock;
import net.harrison.battleroyaleitem.block.LootChestBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("battleroyale")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("start")
                        .executes(context -> {

                            GamingEvent.startOneGame(context.getSource().getLevel());

                            return 1;
                        })
                )

                .then(Commands.literal("getStatus")
                        .executes(context -> {
                            if (GamingEvent.getBattleroyaleManager(context.getSource().getLevel()) != null) {
                                context.getSource().sendSuccess(() ->Component.translatable("manager.battleroyale.running").withStyle(ChatFormatting.GOLD), true);
                            } else {
                                context.getSource().sendSuccess(() -> Component.translatable("manager.battleroyale.idle").withStyle(ChatFormatting.AQUA), true);
                            }
                            return 1;
                        })
                )

                .then(Commands.literal("resource")
                        .then(Commands.literal("reload")
                                .executes(context -> {


                                    ServerLevel level = context.getSource().getLevel();

                                    List<BlockPos> loot_chest_locations = LootChestBlockPosData.get(level).getLocations();
                                    List<BlockPos> common_loot_chest_locations = CommonLootChestBlockPosData.get(level).getLocations();
                                    List<BlockPos> rare_loot_chest_locations = RareLootChestBlockPosData.get(level).getLocations();
                                    List<BlockPos> epic_loot_chest_locations = EpicLootChestBlockPosData.get(level).getLocations();


                                    int detected = 0;

                                    for (BlockPos lootChestPos: loot_chest_locations) {
                                        if (level.getBlockEntity(lootChestPos) instanceof LootChestBlockEntity lootChestBlockEntity) {
                                            BlockState state = level.getBlockState(lootChestPos);

                                            lootChestBlockEntity.setLootTable(Battleroyale.lootChestLootTable, 0);
                                            level.setBlock(lootChestPos, state.setValue(LootChestBlock.OPEN, false), 3);
                                            detected++;
                                        }
                                    }
                                    context.getSource().sendSystemMessage(Component.literal("custom重置数:" + detected));
                                    for (BlockPos lootChestPos: common_loot_chest_locations) {
                                        if (level.getBlockEntity(lootChestPos) instanceof LootChestBlockEntity lootChestBlockEntity) {
                                            BlockState state = level.getBlockState(lootChestPos);

                                            lootChestBlockEntity.setLootTable(Battleroyale.commonLootChestLootTable, 0);
                                            level.setBlock(lootChestPos, state.setValue(LootChestBlock.OPEN, false), 3);
                                            detected++;
                                        }
                                    }
                                    context.getSource().sendSystemMessage(Component.literal("common重置数:" + detected));
                                    for (BlockPos lootChestPos: rare_loot_chest_locations) {
                                        if (level.getBlockEntity(lootChestPos) instanceof LootChestBlockEntity lootChestBlockEntity) {
                                            BlockState state = level.getBlockState(lootChestPos);

                                            lootChestBlockEntity.setLootTable(Battleroyale.rareLootChestLootTable, 0);
                                            level.setBlock(lootChestPos, state.setValue(LootChestBlock.OPEN, false), 3);
                                            detected++;
                                        }
                                    }
                                    context.getSource().sendSystemMessage(Component.literal("rare重置数:" + detected));
                                    for (BlockPos lootChestPos: epic_loot_chest_locations) {
                                        if (level.getBlockEntity(lootChestPos) instanceof LootChestBlockEntity lootChestBlockEntity) {
                                            BlockState state = level.getBlockState(lootChestPos);

                                            lootChestBlockEntity.setLootTable(Battleroyale.epicLootChestLootTable, 0);
                                            level.setBlock(lootChestPos, state.setValue(LootChestBlock.OPEN, false), 3);
                                            detected++;
                                        }
                                    }
                                    context.getSource().sendSystemMessage(Component.literal("epic重置数:" + detected));
                                    return 1;
                                })
                        )

                        .then(Commands.literal("clear")
                                .executes(context -> {
                                    ServerLevel level = context.getSource().getLevel();

                                    List<BlockPos> locations = LootChestBlockPosData.get(level).getLocations();

                                    for (BlockPos lootChestPos: locations) {
                                        if (level.getBlockEntity(lootChestPos) instanceof LootChestBlockEntity) {

                                            level.setBlockAndUpdate(lootChestPos, Blocks.AIR.defaultBlockState());

                                        }
                                    }
                                    context.getSource().sendSystemMessage(Component.literal("已将所有资源箱删除!"));

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("test")
                        .executes(context -> {
                            ServerLevel level = context.getSource().getLevel();
                            level.getServer().sendSystemMessage(Component.literal("Nothing Here..."));
                            return 1;
                        }))
        );
    }
}
