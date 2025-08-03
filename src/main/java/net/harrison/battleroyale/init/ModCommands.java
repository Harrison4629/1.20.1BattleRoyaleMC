package net.harrison.battleroyale.init;

import com.mojang.brigadier.CommandDispatcher;
import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.harrison.battleroyale.data.LootChestBlockPosData;
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
                            BattleroyaleManager.startBattleRoyale();
                            return 1;
                        })
                )

                .then(Commands.literal("getStatus")
                        .executes(context -> {
                            if (BattleroyaleManager.getStatus()) {
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

                                    List<BlockPos> locations = LootChestBlockPosData.get(level).getLocations();

                                    int detected = 0;

                                    for (BlockPos lootChestPos: locations) {
                                        if (level.getBlockEntity(lootChestPos) instanceof LootChestBlockEntity lootChestBlockEntity) {
                                            BlockState state = level.getBlockState(lootChestPos);

                                            lootChestBlockEntity.setLootTable(Battleroyale.lootChestLootTable, 0);
                                            level.setBlock(lootChestPos, state.setValue(LootChestBlock.OPEN, false), 3);
                                            detected++;
                                        }
                                    }
                                    context.getSource().sendSystemMessage(Component.literal("重置数:" + detected));
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

                .then(Commands.literal("initialsettings")
                        .executes(context -> {

                            if (BattleroyaleManager.setHobby()){
                                context.getSource().sendSystemMessage(Component.literal("未检测到hobby"));
                                return 1;
                            }


                            if (BattleroyaleManager.setPlatform()) {
                                context.getSource().sendSystemMessage(Component.literal("未检测到platform"));
                                return 1;
                            }
                            context.getSource().sendSystemMessage(Component.literal("初始化成功！"));
                            return 1;
                        }))
        );
    }
}
