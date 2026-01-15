package baguchi.barrel_cannon.registry;

import baguchi.barrel_cannon.BarrelCannonMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BarrelCannonMod.MODID);

    // Creates a creative tab with the id "barrel_cannon:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("barrel_cannon_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.barrel_cannon")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ModItems.BARREL_CANNON.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.BARREL_CANNON.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

}
