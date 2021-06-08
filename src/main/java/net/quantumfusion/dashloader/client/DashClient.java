package net.quantumfusion.dashloader.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class DashClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }
}
