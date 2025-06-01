package net.chippymoo.creategolemsgalore.entity;

import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CreateGolemsGalore.MOD_ID);


    public static final Supplier<EntityType<AndesiteGolem>> ANDESITEGOLEM =
            ENTITY_TYPES.register("andesite_golem", () -> EntityType.Builder.of(AndesiteGolem::new, MobCategory.CREATURE)
                    .sized(0.75f, 1.75f).build("andesite_golem"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
