package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.font.Font;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashCacheState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {


    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;"),
            cancellable = true)
    private void waterMark(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (DashLoader.getInstance().state == DashCacheState.LOADED) {
            drawStringWithShadow(matrices, this.textRenderer, "DashLoader (" + DashLoader.version  + ")", 2, this.height - 12 - textRenderer.fontHeight, 16777215);
        }
    }
}