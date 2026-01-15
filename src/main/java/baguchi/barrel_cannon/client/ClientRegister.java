package baguchi.barrel_cannon.client;

import baguchi.barrel_cannon.BarrelCannonMod;
import baguchi.barrel_cannon.attachment.BlastAttachment;
import baguchi.barrel_cannon.entity.BarrelCannon;
import baguchi.barrel_cannon.registry.ModAttachments;
import baguchi.barrel_cannon.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = BarrelCannonMod.MODID, value = Dist.CLIENT)
public class ClientRegister {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BARREL_CANNON.get(), BarrelCannonRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.BARREL_CANNON, BarrelCannonModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRenderPre(RenderLivingEvent.Pre<?,?> event) {
        if(event.getEntity().getVehicle() instanceof BarrelCannon ){
            event.setCanceled(true);
        }
    }
}