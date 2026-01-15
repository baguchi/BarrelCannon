package baguchi.barrel_cannon;

import baguchi.barrel_cannon.attachment.BlastAttachment;
import baguchi.barrel_cannon.registry.ModAttachments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = BarrelCannonMod.MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        BlastAttachment blastAttachment = event.getEntity().getData(ModAttachments.BLAST.get());

        if(blastAttachment != null){
            blastAttachment.tick(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        BlastAttachment blastAttachment = event.getEntity().getData(ModAttachments.BLAST.get());

        if(blastAttachment != null && blastAttachment.isBlasted()){
            event.setCanceled(true);
        }
    }
}
