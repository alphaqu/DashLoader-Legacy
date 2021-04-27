package net.quantumfusion.dashloader.cache.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.font.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.mixin.FontManagerAccessor;
import net.quantumfusion.dashloader.mixin.UnicodeTextureFontAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
            final Map<Identifier, FontStorage> fontStorages = fontManager.getFontStorages();
            fontStorages.values().forEach(FontStorage::close);
            fontStorages.clear();
            profiler.swap("reloading");
            map.forEach((identifier, list) -> {
                FontStorage fontStorage = new FontStorage(fontManager.getTextureManager(), identifier);
                fontStorage.setFonts(Lists.reverse(list));
                System.out.println(identifier);
                fontStorages.put(identifier, fontStorage);
            });
            profiler.pop();
            profiler.endTick();
        }

        public String getName() {
            return "FontManager";
        }
    };

    public FastFontManager(FontManagerAccessor fontManager) {
        this.fontManager = fontManager;
    }
}
