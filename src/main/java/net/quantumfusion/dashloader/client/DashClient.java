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

    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting");
        Thread.sleep(6000);
        System.out.println("started");
        Instant start = Instant.now();
        IntStream.range(0, 10000000).forEach(DashClient::getImageId);
        Instant stop = Instant.now();
        System.out.println(Duration.between(start, stop).toMillis());
    }

    private static Identifier getImageId(final int codePoint) {
        final String id = Integer.toHexString((codePoint & (~0xFF)) >> 8);
        return new Identifier("textures/font/unicode_page_" + (id.length() == 1 ? 0 + id : id) + ".png");
    }

    @Override
    public void onInitializeClient() {

    }


}
