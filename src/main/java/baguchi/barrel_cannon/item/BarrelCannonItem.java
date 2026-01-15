package baguchi.barrel_cannon.item;

import baguchi.barrel_cannon.entity.BarrelCannon;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class BarrelCannonItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public BarrelCannonItem(Item.Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vec3 = player.getViewVector(1.0F);
            double d0 = 5.0;
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 vec31 = player.getEyePosition();

                for (Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (hitresult.getType() == HitResult.Type.BLOCK) {
                BarrelCannon barrelCannon = this.getBarrelCannon(level, hitresult, itemstack, player);
                barrelCannon.setXRot(player.isShiftKeyDown() ? player.getXRot() : -player.getXRot());
                barrelCannon.setYRot(player.getYRot());
                if (!level.noCollision(barrelCannon, barrelCannon.getBoundingBox())) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(barrelCannon);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        itemstack.consume(1, player);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    private BarrelCannon getBarrelCannon(Level level, HitResult hitResult, ItemStack stack, Player player) {
        Vec3 vec3 = hitResult.getLocation();
        Direction direction = ((BlockHitResult) hitResult).getDirection();
        BarrelCannon boat = new BarrelCannon(level, vec3.x + direction.getStepX(), vec3.y + direction.getStepY(), vec3.z + direction.getStepZ());
        if (level instanceof ServerLevel serverlevel) {
            EntityType.<BarrelCannon>createDefaultStackConfig(serverlevel, stack, player).accept(boat);
        }

        return boat;
    }
}
