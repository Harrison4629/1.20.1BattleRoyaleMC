package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ImmuneStarveEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side.isServer()) {
            Player player = event.player;
            FoodData foodData = player.getFoodData();

            if (foodData.getFoodLevel() < 20 || foodData.getSaturationLevel() < 20.0f) {
                foodData.setFoodLevel(20);
                foodData.setSaturation(20.0f);
            }
        }
    }
}
