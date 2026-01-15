package baguchi.barrel_cannon.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BlastAttachment implements INBTSerializable<CompoundTag> {
    private boolean blasted;
    private int blastCooldown;

    public void setBlasted(boolean blasted) {
        this.blasted = blasted;
    }

    public void blast() {
        this.blasted = true;
        this.blastCooldown = 100;
    }

    public void blastCooldown() {
        this.blastCooldown = 100;
    }

    public void tick(Entity entity) {
        if (this.blastCooldown > 0) {
            --this.blastCooldown;
        }

        if(this.blasted && entity.onGround()) {
            this.blasted = false;
        }
    }

    public boolean canRideBarrel() {
        return blasted || !blasted && this.blastCooldown <= 0;
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
