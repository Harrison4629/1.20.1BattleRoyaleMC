package net.harrison.battleroyale.init;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.items.ServerSettingStickItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Battleroyale.MODID);


    public static final RegistryObject<Item> SERVER_SETTING_STICK = ITEMS.register("server_setting_stick",
            () -> new ServerSettingStickItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
