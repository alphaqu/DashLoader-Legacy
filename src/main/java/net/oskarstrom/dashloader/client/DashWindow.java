package net.oskarstrom.dashloader.client;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;

public class DashWindow extends Screen {
	private final int endFrames = 120;
	private final Screen previousScreen;
	private float currentProgress = 0f;
	private int framesLeftToEnd;
	private boolean started = false;
	private DashRegistry dashRegistry;

	public DashWindow(Text title, Screen previousScreen) {
		super(title);
		framesLeftToEnd = endFrames;
		this.previousScreen = previousScreen;
	}

	@Override
	protected void init() {
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		final double result = (DashLoader.TASK_HANDLER.getProgress() - currentProgress);
		currentProgress += result == 0 ? 0 : result / (DashLoader.TASK_HANDLER.isFailed() ? 40 : 20);
		renderProgressBar(matrices, framesLeftToEnd == 0 ? 0 : framesLeftToEnd / (float) endFrames);
		if (DashLoader.TASK_HANDLER.getProgress() == 1) {
			framesLeftToEnd--;
			if (framesLeftToEnd <= 0) {
				client.openScreen(previousScreen);
			}
		}

		if (DashLoader.TASK_HANDLER.isFailed()) {
			currentProgress -= 0.0001;
			if (currentProgress <= 0) {
				client.openScreen(previousScreen);
			}
		}

		if (!started) {
			final Thread thread = new Thread(() -> {
				DashLoader.getInstance().saveDashCache();
			});
			thread.setContextClassLoader(DashLoader.getInstance().getAssignedClassLoader());
			thread.start();
			started = true;
		}
		super.render(matrices, mouseX, mouseY, delta);
	}

	private void renderProgressBar(MatrixStack matrices, float endBar) {
		final float progress = currentProgress;
		final float progressTimesTwo = progress * 2;
		int green = progress > 0.5f ? 255 : (int) (255 * progressTimesTwo);
		int red = progress < 0.5f ? 255 : (int) (255 * (1 - (progressTimesTwo - 1)));
		green = progress >= 1 ? 255 : green;
		red = progress >= 1 ? 0 : red;
		int progressColor = BackgroundHelper.ColorMixer.getArgb(255, red, green, 0);
		int underbarColor = 0xff001f14;
		float end = (1 - endBar);
		int screenHeight = (client.getWindow().getScaledHeight());
		int screenWidth = client.getWindow().getScaledWidth();
		final int progressBarHeight = 3;
		fillGradient(matrices, 0, 0, screenWidth, screenHeight, 0xff000c08, 0xff001f14);
		DrawableHelper.fill(matrices, (int) (screenWidth * progress), (screenHeight - progressBarHeight), screenWidth, screenHeight, underbarColor);
		DrawableHelper.fill(matrices, (int) (screenWidth * end), (screenHeight - progressBarHeight), (int) (screenWidth * progress), screenHeight, progressColor);
		DrawableHelper.drawTextWithShadow(
				matrices,
				textRenderer,
				DashLoader.TASK_HANDLER.getText(),
				10,
				screenHeight - progressBarHeight - textRenderer.fontHeight,
				BackgroundHelper.ColorMixer.getArgb(255, 220, 220, 220));

		final Text subText = DashLoader.TASK_HANDLER.getSubText();
		DrawableHelper.drawTextWithShadow(matrices,
				textRenderer,
				subText,
				(screenWidth - 10) - textRenderer.getWidth(subText),
				screenHeight - progressBarHeight - textRenderer.fontHeight,
				BackgroundHelper.ColorMixer.getArgb(255, 220, 220, 220));

	}


	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
