package baguchi.barrel_cannon.mixin.client;

import baguchi.barrel_cannon.attachment.BlastAttachment;
import baguchi.barrel_cannon.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "setupRotations", at = @At("TAIL"))
    private void setupRotations(T entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
        BlastAttachment blastAttachment = entity.getData(ModAttachments.BLAST.get());

        if (blastAttachment.isBlasted()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
        }
    }
}
