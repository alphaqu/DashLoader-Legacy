package net.quantumfusion.dashloader.cache.blockstates.properties;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DashEnumProperty implements DashProperty {

    private static final Map<String, Class> cache = new ConcurrentHashMap<>();

    @Serialize(order = 0)
    public final String value;
    @Serialize(order = 1)
    public final List<String> values;
    @Serialize(order = 2)
    public final String className;
    @Serialize(order = 3)
    public final String name;

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
        final Class<T> type = getClass(className);
        out.setLeft(EnumProperty.of(name, type, Arrays.asList(type.getEnumConstants())));
        out.setRight(Enum.valueOf(type, value));
        return out;
    }

    private Class getClass(final String className) {
        final Class closs = cache.get(className);
        if (closs != null) return closs;
        try {
            final Class clz = Class.forName(className);
            cache.put(className, clz);
            return clz;
        } catch (Exception ignored) { }
        return null;
    }
}
