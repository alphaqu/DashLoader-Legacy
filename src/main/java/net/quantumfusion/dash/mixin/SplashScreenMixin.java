package net.quantumfusion.dash.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.quantumfusion.dash.cache.DashCacheState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.quantumfusion.dash.Dash.*;

@Mixin(SplashScreen.class)
public class SplashScreenMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V",shift = At.Shift.BEFORE))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (loader.state != DashCacheState.LOADED) {
            loader.serialize();
        }
        System.out.println(client);
    }
}