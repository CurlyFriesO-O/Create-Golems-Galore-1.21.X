package net.chippymoo.creategolemsgalore.events;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.entity.ModEntities;
import net.chippymoo.creategolemsgalore.entity.client.AndesiteGolemModel;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = CreateGolemsGalore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AndesiteGolemModel.LAYER_LOCATION, AndesiteGolemModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ANDESITEGOLEM.get(), AndesiteGolem.createAttributes().build());
    }
}
