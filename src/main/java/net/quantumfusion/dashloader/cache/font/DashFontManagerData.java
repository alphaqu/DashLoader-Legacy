package net.quantumfusion.dashloader.cache.font;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.DashRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashFontManagerData {

    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {1, 0})
    public Map<Integer, List<Integer>> fontMap;

    public DashFontManagerData(@Deserialize("fontMap") Map<Integer, List<Integer>> fontMap) {
        this.fontMap = fontMap;
    }

    public static DashFontManagerData toDash(Map<Identifier, List<Font>> fonts, DashRegistry registry){
        Map<Integer, List<Integer>> fontMapOut = new HashMap<>();
        fonts.forEach((identifier, fonts1) -> {
            List<Integer> fontsOut = new ArrayList<>();
            fonts1.forEach(font -> fontsOut.add(registry.createFontPointer(font)));
            fontMapOut.put(registry.createIdentifierPointer(identifier),fontsOut);
        });
        return new DashFontManagerData(fontMapOut);
    }

    public Map<Identifier, List<Font>> toUndash(DashRegistry registry) {
        Map<Identifier, List<Font>> out = new HashMap<>();
        fontMap.forEach((integer, integers) -> {
            List<Font> fontsOut = new ArrayList<>();
            integers.forEach(integer1 -> fontsOut.add(registry.getFont(integer1)));
            out.put(registry.getIdentifier(integer),fontsOut);
        });
        return out;
    }
}
