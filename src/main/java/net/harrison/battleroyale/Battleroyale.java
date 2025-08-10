package net.harrison.battleroyale;

import net.harrison.battleroyale.init.ModCommands;
import net.harrison.battleroyale.init.ModItems;
import net.harrison.battleroyale.init.ModMessages;
import net.harrison.battleroyale.manager.TeamManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Battleroyale.MODID)
public class Battleroyale {

    public static final String MODID = "battleroyale";

    public static ResourceLocation lootChestLootTable = ResourceLocation.parse("battleroyale:chests");

    public static ResourceLocation commonLootChestLootTable = ResourceLocation.parse("battleroyale:common");

    public static ResourceLocation rareLootChestLootTable = ResourceLocation.parse("battleroyale:rare");

    public static ResourceLocation epicLootChestLootTable = ResourceLocation.parse("battleroyale:epic");

    public Battleroyale() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        ModItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        TeamManager.createTeams(event.getServer());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
