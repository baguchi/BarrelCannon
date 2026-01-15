package baguchi.barrel_cannon.registry;

import baguchi.barrel_cannon.BarrelCannonMod;
import baguchi.barrel_cannon.entity.BarrelCannon;
import baguchi.barrel_cannon.item.BarrelCannonItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, BarrelCannonMod.MODID);

    public static final DeferredHolder<Item, Item> BARREL_CANNON = ITEMS.register("barrel_cannon", () -> new BarrelCannonItem((new Item.Properties())));

}
