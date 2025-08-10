package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ImmuneDamageBeforeStartEvent {

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event){
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer)){
            return;
        }

        if (GamingEvent.getBattleroyaleManager(event.getEntity().level()) != null) {
            return;
        }

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)){
            return;
        }
        event.setCanceled(true);
    }
}
