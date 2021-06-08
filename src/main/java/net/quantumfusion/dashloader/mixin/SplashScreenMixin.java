package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.client.DashWindow;
import net.quantumfusion.dashloader.util.DashCacheState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SplashScreen.class)
public class SplashScreenMixin {


    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;throwException()V", shift = At.Shift.BEFORE), cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (DashLoader.getInstance().state == DashCacheState.LOADED && client.world == null) {
            client.setOverlay(null);
            client.openScreen(new TitleScreen(false));
            client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        } else {
            this.client.setOverlay(null);
            client.openScreen(new DashWindow(Text.of("dash")));
        }
        ci.cancel();
    }
}
