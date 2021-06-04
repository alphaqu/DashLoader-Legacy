package net.quantumfusion.dashloader.font;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.PairMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashFontManagerData {

    @Serialize(order = 0)
    public PairMap<Integer, List<Integer>> fontMap;

    public DashFontManagerData(@Deserialize("fontMap") PairMap<Integer, List<Integer>> fontMap) {
        this.fontMap = fontMap;
    }

    public DashFontManagerData(Map<Identifier, List<Font>> fontList, DashRegistry registry) {
        fontMap = new PairMap<>();
        fontList.forEach((identifier, fonts) -> {
            List<Integer> fontsOut = new ArrayList<>();
            fonts.forEach(font -> fontsOut.add(registry.createFontPointer(font)));
            fontMap.put(registry.createIdentifierPointer(identifier), fontsOut);
        });

    }

    public Map<Identifier, List<Font>> toUndash(DashRegistry registry) {
        Map<Identifier, List<Font>> out = new HashMap<>();
        fontMap.forEach((identifier, fontPointers) -> {
            List<Font> fontsOut = new ArrayList<>();
            fontPointers.forEach(fontPointer -> fontsOut.add(registry.getFont(fontPointer)));
            out.put(registry.getIdentifier(identifier), fontsOut);
        });
        return out;
    }
}
