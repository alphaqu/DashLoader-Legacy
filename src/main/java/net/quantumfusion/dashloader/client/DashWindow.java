package net.quantumfusion.dashloader.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;

public class DashWindow extends Screen {
    private static final Identifier LOGO = new Identifier("dashloader:textures/icon.png");

    private final int endFrames = 120;
    float currentProgress = 0f;
    int framesLeftToEnd;
    boolean started = false;

    public DashWindow(Text title) {
        super(title);
        framesLeftToEnd = endFrames;
    }

    @Override
    protected void init() {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        final double result = (DashLoader.TASK_HANDLER.getProgress() - currentProgress);
        currentProgress += result == 0 ? 0 : result / 20;
        renderProgressBar(matrices, framesLeftToEnd == 0 ? 0 : framesLeftToEnd / (float) endFrames);
        if (DashLoader.TASK_HANDLER.getProgress() == 1) {
            framesLeftToEnd--;
            if (framesLeftToEnd <= 0) {
                if (MinecraftClient.getInstance().world != null) {
                    client.openScreen(null);
                } else {
                    client.openScreen(new TitleScreen());
                }
            }
        }
        if (!started) {
            new Thread(() -> DashLoader.getInstance().saveDashCache()).start();
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
        DrawableHelper.fill(matrices, 0, 0, screenWidth, screenHeight, BackgroundHelper.ColorMixer.getArgb(150, 31, 0, 0));
        DrawableHelper.fill(matrices, (int) (screenWidth * progress), (screenHeight - progressBarHeight), screenWidth, screenHeight, underbarColor);
        DrawableHelper.fill(matrices, (int) (screenWidth * end), (screenHeight - progressBarHeight), (int) (screenWidth * progress), screenHeight, progressColor);
        DrawableHelper.drawTextWithShadow(
                matrices,
                textRenderer,
                DashLoader.TASK_HANDLER.getText(),
                10,
                screenHeight - progressBarHeight - textRenderer.fontHeight,
                BackgroundHelper.ColorMixer.getArgb(255, 220, 220, 220));
        //lets wait until the final logo is here
//        this.client.getTextureManager().bindTexture(LOGO);
//        drawTexture(matrices, (screenWidth / 2) - 100,(screenHeight / 2) - 100, 200,200, 0, 0, 800, 800, 800, 800);

    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
