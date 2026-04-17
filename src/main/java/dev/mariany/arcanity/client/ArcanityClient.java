package dev.mariany.arcanity.client;

import dev.mariany.arcanity.client.event.TooltipComponentHandler;
import dev.mariany.arcanity.client.gui.screen.ingame.ArcaneTableScreen;
import dev.mariany.arcanity.screen.ArcanityScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class ArcanityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipComponentHandler.bootstrap();
        registerScreenHandlers();
    }

    private static void registerScreenHandlers() {
        HandledScreens.register(ArcanityScreenHandlers.ARCANE_TABLE, ArcaneTableScreen::new);
    }
}
