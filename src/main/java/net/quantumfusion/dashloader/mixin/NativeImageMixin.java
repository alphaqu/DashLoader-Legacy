package net.quantumfusion.dashloader.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin {
    @Shadow protected abstract void checkAllocated();

    @Shadow
    private static void setTextureFilter(boolean blur, boolean mipmap) {
    }

    @Shadow
    private static void setTextureClamp(boolean clamp) {
    }

    @Shadow public abstract int getWidth();

    @Shadow @Final private NativeImage.Format format;

    @Shadow private long pointer;

    @Shadow public abstract void close();

    @Inject(method = "uploadInternal(IIIIIIIZZZZ)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void fastUpload(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean blur, boolean clamp, boolean mipmap, boolean close, CallbackInfo ci) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        checkAllocated();
        setTextureFilter(blur, mipmap);
        setTextureClamp(clamp);
        if (width == getWidth()) {
            GlStateManager.pixelStore(3314, 0);
        } else {
            GlStateManager.pixelStore(3314, getWidth());
        }
        GlStateManager.pixelStore(3316, unpackSkipPixels);
        GlStateManager.pixelStore(3315, unpackSkipRows);
        format.setUnpackAlignment();
        GL11C.glTexSubImage2D(3553, level, xOffset, yOffset, width, height,  format.getPixelDataFormat(), 5121, pointer);
        if (close) {
            this.close();
        }
        ci.cancel();
    }
}
