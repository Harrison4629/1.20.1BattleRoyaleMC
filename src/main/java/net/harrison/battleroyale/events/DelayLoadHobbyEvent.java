package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.BattleroyaleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DelayLoadHobbyEvent {
    private static int delayTicks;
    private static boolean hasLoaded = false;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.side.isClient()) {
            return;
        }

        if (hasLoaded) {
            return;
        }

        if (delayTicks > 0) {
            delayTicks--;
        } else {
            BattleroyaleManager.setHobby();
            hasLoaded = true;
            MinecraftForge.EVENT_BUS.unregister(DelayLoadHobbyEvent.class);
        }
    }

    public static void setDelayTicks(int ticks) {
        delayTicks = ticks;
    }
}
