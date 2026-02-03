package baguchi.barrel_cannon.registry;

import baguchi.barrel_cannon.BarrelCannonMod;
import baguchi.barrel_cannon.attachment.BlastAttachment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BarrelCannonMod.MODID);

    public static final Supplier<AttachmentType<BlastAttachment>> BLAST = ATTACHMENT_TYPES.register(
            "tofu_living", () -> AttachmentType.serializable(BlastAttachment::new).sync(new AttachmentSyncHandler<>() {
                @Override
                public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf, BlastAttachment attachment, boolean b) {
                    registryFriendlyByteBuf.writeBoolean(attachment.isBlasted());
                }

                @Override
                public @Nullable BlastAttachment read(IAttachmentHolder iAttachmentHolder, RegistryFriendlyByteBuf registryFriendlyByteBuf, @Nullable BlastAttachment attachment) {
                    BlastAttachment attachment2 = new BlastAttachment();
                    attachment2.setBlasted(registryFriendlyByteBuf.readBoolean());
                    return attachment2;
                }
            }).build());
}
