package dev.mariany.arcanity.screen;

import dev.mariany.arcanity.Arcanity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public final class ArcanityScreenHandlers {
    public static final ScreenHandlerType<ArcaneTableScreenHandler> ARCANE_TABLE =
            register("arcane_table", ArcaneTableScreenHandler::new);

    private ArcanityScreenHandlers() {
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(
            String id,
            ScreenHandlerType.Factory<T> factory
    ) {
        return Registry.register(
                Registries.SCREEN_HANDLER,
                Arcanity.id(id),
                new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
        );
    }

    public static void bootstrap() {
        Arcanity.bootstrapLog("Screen Handlers");
    }
}
