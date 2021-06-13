package net.quantumfusion.dashloader.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.font.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.mixin.accessor.FontManagerAccessor;
import net.quantumfusion.dashloader.mixin.accessor.UnicodeTextureFontAccessor;
import net.quantumfusion.dashloader.util.DashCacheState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FastFontManager {

    private final FontManagerAccessor fontManager;
    private static final Logger LOGGER = LogManager.getLogger();

    public final ResourceReloader resourceReloadListener = new SinglePreparationResourceReloader<Map<Identifier, List<Font>>>() {
        protected Map<Identifier, List<Font>> prepare(ResourceManager resourceManager, Profiler profiler) {
            final Map<Identifier, List<Font>> fontsOut = DashLoader.getVanillaData().getFonts();
            if (fontsOut != null && DashLoader.getInstance().state == DashCacheState.LOADED) {
                fontsOut.forEach((identifier, list) -> list.forEach(font -> {
                            if (font instanceof UnicodeTextureFont) {
                                ((UnicodeTextureFontAccessor) font).setResourceManager(resourceManager);
                            }
                        }
                ));
                return fontsOut;
            } else {
                profiler.startTick();
                Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
                Map<Identifier, List<Font>> map = Maps.newHashMap();
                for (Identifier identifier : resourceManager.findResources("font", (stringx) -> stringx.endsWith(".json"))) {
                    String string = identifier.getPath();
                    Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("font/".length(), string.length() - ".json".length()));
                    List<Font> list = map.computeIfAbsent(identifier2, (identifierx) -> Lists.newArrayList((new BlankFont())));
                    profiler.push(identifier2::toString);
                    try {
                        Resource resource = resourceManager.getResource(identifier);
                        profiler.push(resource::getResourcePackName);
                        try {
                            try (InputStream inputStream = resource.getInputStream()) {
                                try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                                    profiler.push("reading");
                                    JsonArray jsonArray = JsonHelper.getArray(Objects.requireNonNull(JsonHelper.deserialize(gson, reader, JsonObject.class)), "providers");
                                    profiler.swap("parsing");

                                    for (int i = jsonArray.size() - 1; i >= 0; --i) {
                                        JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");
                                        try {
                                            String string2 = JsonHelper.getString(jsonObject, "type");
                                            profiler.push(string2);
                                            Font font = FontType.byId(string2).createLoader(jsonObject).load(resourceManager);
                                            if (font != null) {
                                                list.add(font);
                                            }
                                            profiler.pop();
                                        } catch (RuntimeException var49) {
                                            LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getResourcePackName(), var49.getMessage());
                                        }
                                    }

                                    profiler.pop();
                                }
                            }
                        } catch (RuntimeException var54) {
                            LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getResourcePackName(), var54.getMessage());
                        }
                        profiler.pop();
                    } catch (IOException var55) {
                        LOGGER.warn("Unable to load font '{}' in fonts.json: {}", identifier2, var55.getMessage());
                    }

                    profiler.push("caching");

                    profiler.pop();
                    profiler.pop();
                }
                return map;
            }
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
            DashLoader.getVanillaData().setFontAssets(map);
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
