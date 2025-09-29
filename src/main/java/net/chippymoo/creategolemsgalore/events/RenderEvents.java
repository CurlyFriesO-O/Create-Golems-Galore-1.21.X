package net.chippymoo.creategolemsgalore.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.entity.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;


@EventBusSubscriber(modid = CreateGolemsGalore.MOD_ID, value = Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();


        if (entity.getType() == ModEntities.ANDESITEGOLEM.get()) {
            float scale = 0.75F;
            poseStack.scale(scale, scale, scale);
        }
    }
}