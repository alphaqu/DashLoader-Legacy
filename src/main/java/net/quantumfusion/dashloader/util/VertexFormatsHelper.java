package net.quantumfusion.dashloader.util;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.HashMap;
import java.util.Map;

public class VertexFormatsHelper {

    private static Map<VertexFormat, Value> cache;

    public static Value getEnum(VertexFormat format) {
        if (cache == null) {
            cache = new HashMap<>();
            for (Value value : Value.values()) {
                cache.put(value.format, value);
            }
        }
        return cache.get(format);
    }

    public enum Value {
        BLIT_SCREEN(VertexFormats.BLIT_SCREEN),
        POSITION_COLOR_TEXTURE_LIGHT_NORMAL(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
        POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
        POSITION_TEXTURE_COLOR_LIGHT(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT),
        POSITION(VertexFormats.POSITION),
        POSITION_COLOR(VertexFormats.POSITION_COLOR),
        LINES(VertexFormats.LINES),
        POSITION_COLOR_LIGHT(VertexFormats.POSITION_COLOR_LIGHT),
        POSITION_TEXTURE(VertexFormats.POSITION_TEXTURE),
        POSITION_COLOR_TEXTURE(VertexFormats.POSITION_COLOR_TEXTURE),
        POSITION_TEXTURE_COLOR(VertexFormats.POSITION_TEXTURE_COLOR),
        POSITION_COLOR_TEXTURE_LIGHT(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
        POSITION_TEXTURE_LIGHT_COLOR(VertexFormats.POSITION_TEXTURE_LIGHT_COLOR),
        POSITION_TEXTURE_COLOR_NORMAL(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        VertexFormat format;

        Value(VertexFormat format) {
            this.format = format;
        }

        public VertexFormat getFormat() {
            return format;
        }
    }
}
