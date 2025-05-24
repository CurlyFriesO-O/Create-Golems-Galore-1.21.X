package net.chippymoo.creategolemsgalore.item;

import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateGolemsGalore.MOD_ID);


    public static final DeferredItem<Item> COPPER_CALIBRATOR = ITEMS.register("copper_calibrator",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
