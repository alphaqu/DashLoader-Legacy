package net.quantumfusion.dashloader.cache.font;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashFontManagerData {

    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {1, 0})
    public Map<Long, List<Long>> fontMap;

    public DashFontManagerData(@Deserialize("fontMap") Map<Long, List<Long>> fontMap) {
        this.fontMap = fontMap;
    }

    public DashFontManagerData(Map<Identifier, List<Font>> fontList, DashRegistry registry){
        fontMap = new HashMap<>();
        fontList.forEach((identifier, fonts) -> {
            List<Long> fontsOut = new ArrayList<>();
            fonts.forEach(font -> fontsOut.add(registry.createFontPointer(font)));
            fontMap.put(registry.createIdentifierPointer(identifier),fontsOut);
        });

    }
    public Map<Identifier, List<Font>> toUndash(DashRegistry registry) {
        Map<Identifier, List<Font>> out = new HashMap<>();
        fontMap.forEach((identifier, fontPointers) -> {
            List<Font> fontsOut = new ArrayList<>();
            fontPointers.forEach(fontPointer -> fontsOut.add(registry.getFont(fontPointer)));
            out.put(registry.getIdentifier(identifier),fontsOut);
        });
        return out;
    }
}
