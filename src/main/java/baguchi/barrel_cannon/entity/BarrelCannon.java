package baguchi.barrel_cannon.entity;

import baguchi.barrel_cannon.attachment.BlastAttachment;
import baguchi.barrel_cannon.registry.ModAttachments;
import baguchi.barrel_cannon.registry.ModEntities;
import baguchi.barrel_cannon.registry.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BarrelCannon extends VehicleEntity implements PlayerRideableJumping {

    private int placedCooldown;

    public BarrelCannon(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public BarrelCannon(Level level, double x, double y, double z) {
        this(ModEntities.BARREL_CANNON.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.placedCooldown = 100;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.BARREL_CANNON.get();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.placedCooldown = compoundTag.getInt("placedCooldown");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("placedCooldown", this.placedCooldown);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 61) {
            for (int i = 0; i < 8; i++) {
                Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                        .xRot(-this.getXRot() * (float) (Math.PI / 180.0))
                        .yRot(-this.getYRot() * (float) (Math.PI / 180.0));
                this.level()
                        .addParticle(
                                ParticleTypes.CLOUD,
                                this.getX() + this.getLookAngle().x / 2.0,
                                this.getY() + this.getLookAngle().y / 2.0,
                                this.getZ() + this.getLookAngle().z / 2.0,
                                vec3.x * 5,
                                vec3.y * 5,
                                vec3.z * 5
                        );
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    public void setPlacedCooldown(int placedCooldown) {
        this.placedCooldown = placedCooldown;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }


    public static boolean canVehicleCollide(Entity vehicle, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void animateHurt(float yaw) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    @Override
    public void tick() {

        if (!this.level().isClientSide) {
            this.ejectPassengers();
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.setDeltaMovement(Vec3.ZERO);

        this.checkInsideBlocks();
        if (this.placedCooldown-- <= 0) {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

                for (Entity entity : list) {
                    BlastAttachment blastAttachment = entity.getData(ModAttachments.BLAST.get());
                    if (!entity.hasPassenger(this)) {
                        if (flag
                                && this.getPassengers().size() < this.getMaxPassengers()
                                && !entity.isPassenger()
                                && this.hasEnoughSpaceFor(entity)
                                && entity instanceof LivingEntity
                                && !(entity instanceof WaterAnimal)
                                && (!(entity instanceof Player) || blastAttachment.canRideBarrel())) {
                            entity.startRiding(this);
                            blastAttachment.blastCooldown();
                            this.syncData(ModAttachments.BLAST.get());
                            this.playSound(SoundEvents.BARREL_OPEN, 1.0F, 1.0F);
                        } else {
                            this.push(entity);
                        }
                    }
                }
            }
        }
    }

    public boolean hasEnoughSpaceFor(Entity entity) {
        return entity.getBbWidth() < this.getBbWidth();
    }


    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }


    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < this.getMaxPassengers();
    }

    protected int getMaxPassengers() {
        return 1;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity livingentity ? livingentity : super.getControllingPassenger();
    }

    @Override
    public void onPlayerJump(int jumpPower) {

    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        Entity entity = this.getControllingPassenger();

        BlastAttachment blastAttachment = entity.getData(ModAttachments.BLAST.get());

        blastAttachment.blast();

        this.syncData(ModAttachments.BLAST.get());
        this.level().broadcastEntityEvent(this, (byte) 61);
        this.ejectPassengers();
        if (entity instanceof LivingEntity living) {
            Optional<Vec3> optional = calculateJumpVectorForAngle(living, entity.position().add(this.getViewVector(1.0F).scale(10.0F)), 1.4F, this.getXRot());
            if (optional.isPresent()) {
                this.playSound(SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), 1.5F, 1.0F);
                entity.setYRot(living.yBodyRot);
                living.setDiscardFriction(true);
                entity.setDeltaMovement(optional.get());
            }
        }
    }

    public static Optional<Vec3> calculateJumpVectorForAngle(LivingEntity mob, Vec3 target, float maxJumpVelocity, float angle) {
        Vec3 vec3 = mob.position();
        Vec3 vec31 = new Vec3(target.x - vec3.x, 0.0, target.z - vec3.z).normalize().scale(0.5);
        Vec3 vec32 = target.subtract(vec31);
        Vec3 vec33 = vec32.subtract(vec3);
        float f = (float) angle * (float) Math.PI / 180.0F;
        double d0 = Math.atan2(vec33.z, vec33.x);
        double d1 = vec33.subtract(0.0, vec33.y, 0.0).lengthSqr();
        double d2 = Math.sqrt(d1);
        double d3 = vec33.y;
        double d4 = mob.getGravity();
        double d5 = Math.sin((double) (2.0F * f));
        double d6 = Math.pow(Math.cos((double) f), 2.0);
        double d7 = Math.sin((double) f);
        double d8 = Math.cos((double) f);
        double d9 = Math.sin(d0);
        double d10 = Math.cos(d0);
        double d11 = d1 * d4 / (d2 * d5 - 2.0 * d3 * d6);
        if (d11 < 0.0) {
            return Optional.empty();
        } else {
            double d12 = Math.sqrt(d11);
            if (d12 > (double) maxJumpVelocity) {
                return Optional.empty();
            } else {
                double d13 = d12 * d8;
                double d14 = d12 * d7;

                return Optional.of(new Vec3(d13 * d10, d14, d13 * d9).scale(0.95F));
            }
        }
    }

    @Override
    public void handleStopJump() {

    }
}
