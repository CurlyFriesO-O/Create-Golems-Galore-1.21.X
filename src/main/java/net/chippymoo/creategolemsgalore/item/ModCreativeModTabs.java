package net.chippymoo.creategolemsgalore.item;

import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateGolemsGalore.MOD_ID);

    public static final Supplier<CreativeModeTab> CREATE_GOLEMS_GALORE_TAB = CREATIVE_MODE_TAB.register("create_golems_galore_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.COPPER_CALIBRATOR.get()))
                    .title(Component.translatable("Create: Golems Galore"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.COPPER_CALIBRATOR);
                        output.accept(ModBlocks.INDUSTRIAL_IRON_HAT);
                        output.accept(ModItems.ANDESITE_GOLEM_SPAWN_EGG);

                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register((eventBus));
    }

}
