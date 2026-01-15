package baguchi.barrel_cannon.registry;

import baguchi.barrel_cannon.BarrelCannonMod;
import baguchi.barrel_cannon.entity.BarrelCannon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BarrelCannonMod.MODID);
    public static final Supplier<EntityType<BarrelCannon>> BARREL_CANNON = ENTITIES.register("barrel_cannon", () -> EntityType.Builder.<BarrelCannon>of(BarrelCannon::new, MobCategory.MISC)
            .sized(1.0F, 1.0F).eyeHeight(0.5F).clientTrackingRange(10).passengerAttachments(1.0F).ridingOffset(0.0F).build(prefix("barrel_cannon").location().toString()));

    private static ResourceKey<EntityType<?>> prefix(String path) {
        return ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(BarrelCannonMod.MODID, path));
    }

}