package net.chippymoo.creategolemsgalore.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AndesiteGolemRenderer extends MobRenderer <AndesiteGolem, AndesiteGolemModel<AndesiteGolem>> {


    public AndesiteGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new AndesiteGolemModel<>(context.bakeLayer(AndesiteGolemModel.LAYER_LOCATION)),  0.75f);
    }

    @Override
    public ResourceLocation getTextureLocation(AndesiteGolem andesiteGolem) {
        return ResourceLocation.fromNamespaceAndPath(CreateGolemsGalore.MOD_ID, "textures/entity/andesite_golem/andesitegolemorange.png");
    }

    @Override
    public void render(AndesiteGolem entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {




        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

}
