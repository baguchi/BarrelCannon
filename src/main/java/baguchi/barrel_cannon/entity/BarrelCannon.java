package baguchi.barrel_cannon.entity;

import baguchi.barrel_cannon.attachment.BlastAttachment;
import baguchi.barrel_cannon.registry.ModAttachments;
import baguchi.barrel_cannon.registry.ModEntities;
import baguchi.barrel_cannon.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class BarrelCannon extends VehicleEntity implements PlayerRideableJumping {
    protected static final EntityDataAccessor<Float> DATA_ID_POWER = SynchedEntityData.defineId(BarrelCannon.class, EntityDataSerializers.FLOAT);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_POWER, 1F);
    }

    public void setPower(float power) {
        this.entityData.set(DATA_ID_POWER, power);
    }

    public float getPower() {
        return this.entityData.get(DATA_ID_POWER);
    }


    @Override
    protected Item getDropItem() {
        return ModItems.BARREL_CANNON.get();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.placedCooldown = compoundTag.getInt("placedCooldown");
        this.setPower(compoundTag.getFloat("power"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("placedCooldown", this.placedCooldown);
        compoundTag.putFloat("power", this.getPower());

    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 61) {
            for (int i = 0; i < 8; i++) {
                Vec3 vec3 = new Vec3(((double) this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
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
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.WIND_CHARGE) && this.getPower() < 2.0F) {
            this.setPower(this.getPower() + 0.1F);
            player.getItemInHand(hand).shrink(1);
            this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 0.1F + this.getPower());
            return InteractionResult.SUCCESS;
        } else if (player.getItemInHand(hand).is(Items.WIND_CHARGE)) {
            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }


    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }


    @Override
    public void animateHurt(float yaw) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    @Override
    public void tick() {

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
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.1F, 0F, 0.1F), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

                for (Entity entity : list) {
                    BlastAttachment blastAttachment = entity.getData(ModAttachments.BLAST.get());
                    if (!entity.hasPassenger(this)) {
                        if (!entity.isShiftKeyDown() && flag
                                && this.getPassengers().size() < this.getMaxPassengers()
                                && !entity.isPassenger()
                                && this.hasEnoughSpaceFor(entity)
                                && entity instanceof LivingEntity
                                && !(entity instanceof WaterAnimal)
                                && (!(entity instanceof Player) || blastAttachment.canRideBarrel())) {
                            entity.startRiding(this);
                            this.syncData(ModAttachments.BLAST.get());
                            this.playSound(SoundEvents.BARREL_OPEN, 1.0F, 1.0F);
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

        this.level().broadcastEntityEvent(this, (byte) 61);
        entity.stopRiding();
        this.playSound(SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), 1.5F, 1.0F);

        entity.setYRot(this.getYRot());
        blastAttachment.blast(this.getLookAngle().scale(this.getPower()));

        this.syncData(ModAttachments.BLAST.get());
    }

    @Override
    public void handleStopJump() {

    }
}
