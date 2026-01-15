package baguchi.barrel_cannon.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BlastAttachment implements INBTSerializable<CompoundTag> {
    private boolean blasted;
    private Vec3 motion;
    private int blastCooldown;

    public void setBlasted(boolean blasted) {
        this.blasted = blasted;
    }

    public void blast(Vec3 motion) {
        this.blasted = true;
        this.blastCooldown = 4;
        this.motion = motion;
    }

    public Vec3 getMotion() {
        return motion;
    }

    public void setMotion(Vec3 motion) {
        this.motion = motion;
    }

    public void blastCooldown() {
        this.blastCooldown = 60;
    }

    public void tick(Entity entity) {
        if (this.blastCooldown > 0) {
            --this.blastCooldown;
        }

        if (motion != null && entity instanceof LivingEntity living) {
            living.setDiscardFriction(true);
            living.setDeltaMovement(motion);
            if (living instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            }
            living.hasImpulse = true;
            motion = null;
        }

        if(this.blasted && entity.onGround()) {
            this.blasted = false;
            if (entity instanceof LivingEntity living) {
                living.setDiscardFriction(false);
            }
        }
    }

    public boolean canRideBarrel() {
        return this.blastCooldown <= 0;
    }

    public boolean isBlasted() {
        return blasted;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("blasted", blasted);
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

        blasted = nbt.getBoolean("blasted");
    }
}
