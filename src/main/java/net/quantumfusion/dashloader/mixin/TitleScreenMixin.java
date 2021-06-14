package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.util.DashCacheState;
import net.quantumfusion.dashloader.util.DashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {


    protected TitleScreenMixin(Text title) {
        super(title);
    }

    private static boolean printed = false;

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        if (!printed) {
            DashReport.printReport();
            printed = true;
        }
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;"),
            cancellable = true)
    private void waterMark(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (DashLoader.getInstance().state == DashCacheState.LOADED) {
            drawStringWithShadow(matrices, this.textRenderer, "DashLoader (" + DashLoader.VERSION + ")", 2, this.height - 12 - textRenderer.fontHeight, 16777215);
        }
    }
}
