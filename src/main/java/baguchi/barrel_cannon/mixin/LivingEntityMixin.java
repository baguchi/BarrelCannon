package baguchi.barrel_cannon.mixin;

import baguchi.barrel_cannon.attachment.BlastAttachment;
import baguchi.barrel_cannon.entity.BarrelCannon;
import baguchi.barrel_cannon.registry.ModAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract void setDiscardFriction(boolean discardFriction);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "dismountVehicle", at = @At("HEAD"), cancellable = true)
    private void dismountVehicle(Entity vehicle, CallbackInfo ci) {
        if (vehicle instanceof BarrelCannon living) {
            BlastAttachment blastAttachment = this.getData(ModAttachments.BLAST.get());
            blastAttachment.blastCooldown();
            ci.cancel();
        }
    }
}
