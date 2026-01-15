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

    @Override
    public void render(T entity, float partialTicks, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 8F / 16F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.lerp(g, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, entity.xRotO, entity.getXRot()) - 90));
        float f2 = (float) entity.getHurtTime() - partialTicks;
        float f1 = entity.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (partialTicks > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(partialTicks) * f2 * f1 / 10.0F * (float) entity.getHurtDir()));
        }
        poseStack.translate(0.0F, -1.501F + 8F / 16F, 0F);
        this.model.setupAnim(entity, g, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, partialTicks, g, poseStack, multiBufferSource, i);
    }

    public ResourceLocation getTextureLocation(T llamaSpit) {
        return LOCATION;
    }
}