package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.manager.BattleroyaleManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GamingEvent {
    private static final Map<Level, BattleroyaleManager> ActiveManager = new HashMap<>();

    @SubscribeEvent
    public static void onGaming(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Iterator<Map.Entry<Level, BattleroyaleManager>> iterator = ActiveManager.entrySet().iterator();
        while (iterator.hasNext()) {
            BattleroyaleManager battleroyaleManager = iterator.next().getValue();
            if (battleroyaleManager.isRunning()) {
                battleroyaleManager.tick();
            } else {
                iterator.remove();
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer deathPlayer)) {
            return;
        }
        BattleroyaleManager battleroyaleManager = ActiveManager.get(deathPlayer.level());

        if (battleroyaleManager != null) {
            battleroyaleManager.onPlayerDeath(deathPlayer, event.getSource());
        }
    }

    public static BattleroyaleManager getBattleroyaleManager(Level level) {
        return ActiveManager.get(level);
    }


    public static void startOneGame(Level level) {
        if (ActiveManager.containsKey(level)) {
            return;
        }
        BattleroyaleManager battleroyaleManager = new BattleroyaleManager(level);
        ActiveManager.put(level, battleroyaleManager);
        battleroyaleManager.startGame();
    }
}
