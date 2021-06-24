package net.quantumfusion.dashloader.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class DashCacheOverlay extends Overlay {
    private final MinecraftClient client;

    public DashCacheOverlay(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        final Window window = client.getWindow();
        fill(matrices, 0, 0, window.getScaledWidth(), window.getScaledHeight(), 0xff000c08);
    }
}
