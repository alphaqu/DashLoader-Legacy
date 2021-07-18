package net.oskarstrom.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;
import net.oskarstrom.dashloader.data.VanillaData;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.util.ThreadHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashFontManagerData implements Dashable {

    @Serialize(order = 0)
    public final Pointer2ObjectMap<List<Integer>> fontMap;

    public DashFontManagerData(@Deserialize("fontMap") Pointer2ObjectMap<List<Integer>> fontMap) {
        this.fontMap = fontMap;
    }

    public DashFontManagerData(VanillaData data, DashRegistry registry, DashLoader.TaskHandler taskHandler) {
        fontMap = new Pointer2ObjectMap<>();
        int amount = 0;
        final Map<Identifier, List<Font>> fonts = data.getFonts();
        for (List<Font> value : fonts.values()) {
            amount += value.size();
        }
        taskHandler.setSubtasks(amount);
        ThreadHelper.execForEach(fonts, (identifier, fontList) -> {
            List<Integer> fontsOut = new ArrayList<>();
            fontList.forEach(font -> {
                fontsOut.add(registry.fonts.register(font));
                taskHandler.completedSubTask();
            });
            fontMap.put(registry.identifiers.register(identifier), fontsOut);
        });
    }

    public Map<Identifier, List<Font>> toUndash(DashRegistry registry) {
        Map<Identifier, List<Font>> out = new HashMap<>();
        fontMap.forEach((entry) -> {
            List<Font> fontsOut = new ArrayList<>();
            entry.value.forEach(fontPointer -> fontsOut.add(registry.fonts.getObject(fontPointer)));
            out.put(registry.identifiers.getObject(entry.key), fontsOut);
        });
        return out;
    }
}
