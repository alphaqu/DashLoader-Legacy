package net.quantumfusion.dashloader.cache.font;

import com.google.common.collect.Lists;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.mixin.FontManagerAccessor;
import net.quantumfusion.dashloader.mixin.UnicodeTextureFontAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class FastFontManager {

    private final FontManagerAccessor fontManager;
    public final ResourceReloadListener resourceReloadListener = new SinglePreparationResourceReloadListener<Map<Identifier, List<Font>>>() {
        protected Map<Identifier, List<Font>> prepare(ResourceManager resourceManager, Profiler profiler) {
            final Map<Identifier, List<Font>> fontsOut = DashLoader.getInstance().fontsOut;
            fontsOut.forEach((identifier, list) -> list.forEach(font -> {
                        if (font instanceof UnicodeTextureFont) {
                            ((UnicodeTextureFontAccessor) font).setResourceManager(resourceManager);
                        }
                    }
            ));
            return fontsOut;
        }

        protected void apply(Map<Identifier, List<Font>> map, ResourceManager resourceManager, Profiler profiler) {
            profiler.startTick();
            profiler.push("closing");
            fontManager.getFontStorages().values().forEach(FontStorage::close);
            fontManager.getFontStorages().clear();
            profiler.swap("reloading");
            map.forEach((identifier, list) -> {
                FontStorage fontStorage = new FontStorage(fontManager.getTextureManager(), identifier);
                fontStorage.setFonts(Lists.reverse(list));
                fontManager.getFontStorages().put(identifier, fontStorage);
            });
            profiler.pop();
            profiler.endTick();
        }

        public String getName() {
            return "DashFontManager";
        }
    };

    public FastFontManager(FontManagerAccessor fontManager) {
        this.fontManager = fontManager;
    }
}
