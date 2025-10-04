package net.chippymoo.creategolemsgalore.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.chippymoo.creategolemsgalore.CreateGolemsGalore;
import net.chippymoo.creategolemsgalore.entity.custom.AndesiteGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class AndesiteGolemModel <T extends AndesiteGolem > extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateGolemsGalore.MOD_ID, "andesite_golem"), "main");
    private final ModelPart Golem;
    private final ModelPart Body;
    private final ModelPart leftarm;
    private final ModelPart rightarm;
    private final ModelPart Pressarm;
    private final ModelPart rightleg;
    private final ModelPart leftleg;
    private final ModelPart face;
    private final ModelPart nose;
    private final ModelPart Hat;

    public AndesiteGolemModel(ModelPart root) {
        this.Golem = root.getChild("Golem");
        this.Body = this.Golem.getChild("Body");
        this.leftarm = this.Body.getChild("leftarm");
        this.rightarm = this.Body.getChild("rightarm");
        this.Pressarm = this.rightarm.getChild("Pressarm");
        this.rightleg = this.Body.getChild("rightleg");
        this.leftleg = this.Body.getChild("leftleg");
        this.face = this.Body.getChild("face");
        this.nose = this.face.getChild("nose");
        this.Hat = this.face.getChild("Hat");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Golem = partdefinition.addOrReplaceChild("Golem", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Body = Golem.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(46, 39).addBox(-5.4981F, 7.9564F, -4.5009F, 13.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -30.0F, 0.0F));

        PartDefinition leftarm = Body.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(0, 58).addBox(-0.5F, -3.0F, -2.5F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 58).addBox(0.5F, -3.0F, -2.5F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0019F, 11.9564F, -1.001F));

        PartDefinition rightarm = Body.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(24, 60).addBox(-6.5F, -2.0F, -2.5F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 58).addBox(-0.5F, -2.0F, -2.5F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.9981F, 10.9564F, -1.001F));

        PartDefinition Pressarm = rightarm.addOrReplaceChild("Pressarm", CubeListBuilder.create().texOffs(72, 60).addBox(-13.5F, 16.0F, -4.5F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(72, 60).addBox(-6.5F, 16.0F, -4.5F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(68, 15).addBox(-13.5F, 16.0F, -5.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 35).addBox(-13.5F, 16.0F, 1.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 18).addBox(-13.5F, 19.0F, -5.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -11.0F, 2.0F));

        PartDefinition rightleg = Body.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(48, 60).addBox(-2.5F, 0.0F, -2.5F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.9981F, 20.9564F, -1.001F));

        PartDefinition leftleg = Body.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(68, 0).addBox(-3.5F, 0.0F, -3.5F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0019F, 20.9564F, -0.0009F));

        PartDefinition face = Body.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, 0.0F, -8.0F, 17.0F, 8.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0019F, -0.0436F, 0.999F));

        PartDefinition nose = face.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(36, 39).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, -9.0F));

        PartDefinition Hat = face.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(0, 0).addBox(-8.5F, -3.0F, -9.0F, 19.0F, 3.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 39).addBox(-5.5F, -12.0F, -6.5F, 13.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0019F, 0.0436F, 0.001F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(AndesiteGolem entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        if (entity.isPressing()) {
            // Priority: pressing animation
            this.animate(entity.pressingAnimationState, AndesiteGolemAnimations.ANDESITEGOLEM_PRESS_ANIM, ageInTicks, 1f);
        } else {
            // Walk + idle animations only if not pressing
            this.animateWalk(AndesiteGolemAnimations.ANDESITEGOLEM_WALK_ANIM, limbSwing, limbSwingAmount, 2f, 2.5f);
            this.animate(entity.idleAnimationState, AndesiteGolemAnimations.ANDESITEGOLEM_IDLE_ANIM, ageInTicks, 1f);
        }
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.face.yRot = headYaw * ((float)Math.PI / 180f);
        this.face.xRot = headPitch *  ((float)Math.PI / 180f);
    }
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        Golem.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return Golem;
    }
}
