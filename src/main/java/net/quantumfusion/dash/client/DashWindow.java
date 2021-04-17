package net.quantumfusion.dash.client;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.quantumfusion.dash.cache.DashCache;

import static net.quantumfusion.dash.Dash.loader;

public class DashWindow extends Screen {

    private final int endFrames = 120;
    float currentProgress = 0f;
    int framesLeftToEnd;
    boolean started = false;

    public DashWindow(Text title) {
        super(title);
        framesLeftToEnd = endFrames;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        final int tasksComplete = DashCache.tasksComplete;
        final float progress = tasksComplete == 0 ? 0 : tasksComplete / (float) DashCache.totalTasks;
        final float result = (progress - currentProgress);
        currentProgress += result == 0 ? 0 : result / 20;
        renderProgressBar(matrices, framesLeftToEnd == 0 ? 0 : framesLeftToEnd / (float) endFrames);
        if (progress == 1) {
            framesLeftToEnd--;
            if (framesLeftToEnd <= 0) {
                client.openScreen(new TitleScreen());
            }
        }
        if (!started) {
            new Thread(() -> loader.serialize()).start();
            started = true;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void renderProgressBar(MatrixStack matrices, float opacity) {
        int j = Math.round(255.0F * opacity);
        final float progress = currentProgress;
        final float progressTimesTwo = progress * 2;
        int green = progress > 0.5f ? 255 : (int) (255 * progressTimesTwo);
        int red = progress < 0.5f ? 255 : (int) (255 * (1 - (progressTimesTwo - 1)));
        green = progress >= 1 ? 255 : green;
        red = progress >= 1 ? 0 : red;
        int progressColor = BackgroundHelper.ColorMixer.getArgb(255, red, green, 0);
        int underbarColor = BackgroundHelper.ColorMixer.getArgb(j, 20, 20, 20);
        float end = (1 - opacity);
        int screenHeight = (client.getWindow().getScaledHeight());
        int screenWidth = client.getWindow().getScaledWidth();
        final int progressBarHeight = 3;
        DrawableHelper.fill(matrices, 0, 0, screenWidth, screenHeight, BackgroundHelper.ColorMixer.getArgb(150, 40, 40, 60));
        DrawableHelper.fill(matrices, (int) (screenWidth * progress), (screenHeight - progressBarHeight), screenWidth, screenHeight, underbarColor);
        DrawableHelper.fill(matrices, (int) (screenWidth * end), (screenHeight - progressBarHeight), (int) (screenWidth * progress), screenHeight, progressColor);
        DrawableHelper.drawTextWithShadow(
                matrices,
                textRenderer,
                Text.of("(" + DashCache.tasksComplete + "/" + DashCache.totalTasks + ") " + DashCache.task),
                10,
                screenHeight - progressBarHeight - textRenderer.fontHeight,
                BackgroundHelper.ColorMixer.getArgb(255, 220, 220, 220));
    }
}
