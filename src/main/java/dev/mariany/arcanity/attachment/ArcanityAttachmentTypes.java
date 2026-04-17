package dev.mariany.arcanity.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.arcanity.Arcanity;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public class ArcanityAttachmentTypes {
    public static final AttachmentType<Integer> ARCANE_PROGRESS = AttachmentRegistry.createPersistent(
            Arcanity.id("arcane_progress"),
            Codec.INT
    );

    public static void bootstrap() {
        Arcanity.bootstrapLog("Attachment Types");
    }
}
