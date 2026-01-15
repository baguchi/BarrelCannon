package baguchi.barrel_cannon;

import baguchi.barrel_cannon.registry.ModAttachments;
import baguchi.barrel_cannon.registry.ModCreativeTabs;
import baguchi.barrel_cannon.registry.ModEntities;
import baguchi.barrel_cannon.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BarrelCannonMod.MODID)
public class BarrelCannonMod {
    public static final Logger LOGGER = LogUtils.getLogger();

    // Define mod id in a common place for everything to reference
    public static final String MODID = "barrel_cannon";

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public BarrelCannonMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

}
