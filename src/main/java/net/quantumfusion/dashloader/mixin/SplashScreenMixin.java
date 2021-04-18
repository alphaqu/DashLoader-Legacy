package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashCacheState;
import net.quantumfusion.dashloader.client.DashWindow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SplashScreen.class)
public class SplashScreenMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow private float progress;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V", shift = At.Shift.BEFORE), cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (DashLoader.getInstance().state != DashCacheState.LOADED) {
            this.client.setOverlay(null);
            client.openScreen(new DashWindow(Text.of("dash")));
            ci.cancel();
        } else {
            this.client.setOverlay(null);
            this.client.openScreen(new TitleScreen(false));
            this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
            ci.cancel();
        }
    }
}
