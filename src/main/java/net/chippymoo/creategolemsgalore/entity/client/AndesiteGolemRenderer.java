package net.chippymoo.creategolemsgalore.entity.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.entity.AndesiteGolemVariants;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class AndesiteGolemRenderer extends MobRenderer <AndesiteGolem, AndesiteGolemModel<AndesiteGolem>> {

    private static final Map<AndesiteGolemVariants, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(AndesiteGolemVariants.class), map -> {
                map.put(AndesiteGolemVariants.BLUE,
                        ResourceLocation.fromNamespaceAndPath(CreateGolemsGalore.MOD_ID, "textures/entity/andesite_golem/andesitegolemblue.png"));
                map.put(AndesiteGolemVariants.GREEN,
                        ResourceLocation.fromNamespaceAndPath(CreateGolemsGalore.MOD_ID, "textures/entity/andesite_golem/andesitegolemgreen.png"));
                map.put(AndesiteGolemVariants.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(CreateGolemsGalore.MOD_ID, "textures/entity/andesite_golem/andesitegolemorange.png"));
                map.put(AndesiteGolemVariants.RED,
                        ResourceLocation.fromNamespaceAndPath(CreateGolemsGalore.MOD_ID, "textures/entity/andesite_golem/andesitegolemred.png"));
            });


    public AndesiteGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new AndesiteGolemModel<>(context.bakeLayer(AndesiteGolemModel.LAYER_LOCATION)),  0.75f);
    }

    @Override
    public ResourceLocation getTextureLocation(AndesiteGolem andesiteGolem) {
        return LOCATION_BY_VARIANT.get(andesiteGolem.getVariant());
    }

    @Override
    public void render(AndesiteGolem entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {




        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

}
