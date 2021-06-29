package net.quantumfusion.dashloader.util;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.Map;

public class VertexFormatsHelper {

    private static Map<VertexFormat, Value> cache;

    public static Value getEnum(VertexFormat format) {
        //i tried having a cache but something in mojangs hashcode chain made it not have all of them
        if (format == VertexFormats.BLIT_SCREEN) {
            return Value.BLIT_SCREEN;
        }
        if (format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL) {
            return Value.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
        }
        if (format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL) {
            return Value.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
        }
        if (format == VertexFormats.POSITION_TEXTURE_COLOR_LIGHT) {
            return Value.POSITION_TEXTURE_COLOR_LIGHT;
        }
        if (format == VertexFormats.POSITION) {
            return Value.POSITION;
        }
        if (format == VertexFormats.POSITION_COLOR) {
            return Value.POSITION_COLOR;
        }
        if (format == VertexFormats.LINES) {
            return Value.LINES;
        }
        if (format == VertexFormats.POSITION_COLOR_LIGHT) {
            return Value.POSITION_COLOR_LIGHT;
        }
        if (format == VertexFormats.POSITION_TEXTURE) {
            return Value.POSITION_TEXTURE;
        }
        if (format == VertexFormats.POSITION_COLOR_TEXTURE) {
            return Value.POSITION_COLOR_TEXTURE;
        }
        if (format == VertexFormats.POSITION_TEXTURE_COLOR) {
            return Value.POSITION_TEXTURE_COLOR;
        }
        if (format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT) {
            return Value.POSITION_COLOR_TEXTURE_LIGHT;
        }
        if (format == VertexFormats.POSITION_TEXTURE_LIGHT_COLOR) {
            return Value.POSITION_TEXTURE_LIGHT_COLOR;
        }
        if (format == VertexFormats.POSITION_TEXTURE_COLOR_NORMAL) {
            return Value.POSITION_TEXTURE_COLOR_NORMAL;
        }

        System.out.println("wot");
        return null;


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
