package net.harrison.battleroyale.init;

import com.mojang.brigadier.CommandDispatcher;
import net.harrison.battleroyale.BattleroyaleManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

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
                            context.getSource().sendSuccess(() ->Component.literal(BattleroyaleManager.getStatus() ? "§6运行中" : "§b空闲状态"), true);
                            return 1;
                        })
                )
        );

    }
}
