package net.quantumfusion.dashloader.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class DashCacheOverlay extends Overlay {
    private final MinecraftClient client;
    private final boolean monochromeLogo;

    public DashCacheOverlay(MinecraftClient client) {
        this.client = client;
        this.monochromeLogo = client.options.monochromeLogo;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        final Window window = client.getWindow();
        fill(matrices, 0, 0, window.getScaledWidth(), window.getScaledHeight(), monochromeLogo ? 0xff000000 : 0xffff323d);
    }
}
