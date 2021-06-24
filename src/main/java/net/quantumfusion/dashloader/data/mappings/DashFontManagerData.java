package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.util.TaskHandler;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashFontManagerData implements Dashable {

    @Serialize(order = 0)
    public Pointer2ObjectMap<List<Integer>> fontMap;

    public DashFontManagerData(@Deserialize("fontMap") Pointer2ObjectMap<List<Integer>> fontMap) {
        this.fontMap = fontMap;
    }

    public DashFontManagerData(VanillaData data, DashRegistry registry, TaskHandler taskHandler) {
        fontMap = new Pointer2ObjectMap<>();
        int amount = 0;
        final Map<Identifier, List<Font>> fonts = data.getFonts();
        for (List<Font> value : fonts.values()) {
            amount += value.size();
        }
        taskHandler.setSubtasks(amount);
        fonts.forEach((identifier, fontList) -> {
            List<Integer> fontsOut = new ArrayList<>();
            fontList.forEach(font -> {
                fontsOut.add(registry.createFontPointer(font));
                taskHandler.completedSubTask();
            });
            fontMap.put(registry.createIdentifierPointer(identifier), fontsOut);
        });

    }

    public Map<Identifier, List<Font>> toUndash(DashRegistry registry) {
        Map<Identifier, List<Font>> out = new HashMap<>();
        fontMap.forEach((entry) -> {
            List<Font> fontsOut = new ArrayList<>();
            entry.value.forEach(fontPointer -> fontsOut.add(registry.getFont(fontPointer)));
            out.put(registry.getIdentifier(entry.key), fontsOut);
        });
        return out;
    }
}
