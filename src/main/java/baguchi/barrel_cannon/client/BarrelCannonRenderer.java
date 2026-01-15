package baguchi.barrel_cannon.client;

import baguchi.barrel_cannon.BarrelCannonMod;
import baguchi.barrel_cannon.entity.BarrelCannon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BarrelCannonRenderer<T extends BarrelCannon> extends EntityRenderer<T> {
    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(BarrelCannonMod.MODID, "textures/entity/barrel_cannon.png");
    private final BarrelCannonModel<T> model;

    public BarrelCannonRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BarrelCannonModel<>(context.bakeLayer(ModModelLayers.BARREL_CANNON));
    }

    public void render(T llamaSpit, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 4F / 16F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, llamaSpit.yRotO, llamaSpit.getYRot()) - 180F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(g, llamaSpit.xRotO, llamaSpit.getXRot())));
        poseStack.translate(0.0F, -1.501F + 8F / 16F, -2.5F / 16F);
        this.model.setupAnim(llamaSpit, g, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(llamaSpit, f, g, poseStack, multiBufferSource, i);
    }

    public ResourceLocation getTextureLocation(T llamaSpit) {
        return LOCATION;
    }
}