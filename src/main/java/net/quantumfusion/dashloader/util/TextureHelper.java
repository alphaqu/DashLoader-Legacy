package net.quantumfusion.dashloader.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

public class TextureHelper {

    public static void allocate(int id, int maxLevel, int width, int height) {
        allocateTexture(id, maxLevel, width, height);
    }

    public static void allocateTexture(int id, int maxLevel, int width, int height) {
        GL11.glBindTexture(3553, id);
        if (maxLevel >= 0) {
            GlStateManager.texParameter(3553, 33085, maxLevel);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, maxLevel);
            GlStateManager.texParameter(3553, 34049, 0.0F);
        }

        for(int i = 0; i <= maxLevel; ++i) {
            GL11.glTexImage2D(3553, i, 6408, width >> i, height >> i, 0, 6408, 5121, (IntBuffer)null);
        }
    }

}
