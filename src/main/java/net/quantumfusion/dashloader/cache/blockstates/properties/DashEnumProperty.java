package net.quantumfusion.dashloader.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashEnumProperty implements DashProperty {

    @Serialize(order = 0)
    public String value;

    @Serialize(order = 1)
    public List<String> values;

    @Serialize(order = 2)
    public String className;

    @Serialize(order = 3)
    public String name;

    private Map<String, Class> cache = new HashMap<>();


    public DashEnumProperty(@Deserialize("value") String value,
                            @Deserialize("values") List<String> values,
                            @Deserialize("className") String className,
                            @Deserialize("name") String name) {
        this.value = value;
        this.values = values;
        this.className = className;
        this.name = name;
    }

    public DashEnumProperty(EnumProperty property, Enum value) {
        className = property.getType().getName();
        name = property.getName();
        values = new ArrayList<>();
        property.getValues().forEach(valuee -> values.add(valuee.toString()));
        this.value = value.name();
    }

    @Override
    public <T extends Enum<T> & StringIdentifiable> MutablePair<EnumProperty<T>, Enum<T>> toUndash() {
        final MutablePair<EnumProperty<T>, Enum<T>> out = new MutablePair<>();
        Class<T> type = getClass(className);
        out.setLeft(EnumProperty.of(name, type));
        out.setRight(Enum.valueOf(type, value));
        return out;
    }

    private Class getClass(String className) {
        Class clz = cache.get(className);
        if (clz != null) return clz;
        try {
            clz = Class.forName(className);
            cache.put(className, clz);
        } catch (Exception ignored) {
        }
        return clz;
    }
}
