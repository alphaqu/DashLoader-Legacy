package net.quantumfusion.dashloader.blockstate.property;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.blockstate.property.value.DashEnumValue;

import java.lang.invoke.MethodHandle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.quantumfusion.dashloader.api.DashLoaderAPI.createConstructor;

@DashObject(EnumProperty.class)
public class DashEnumProperty implements DashProperty {

    private static final Map<String, Class> cache = new ConcurrentHashMap<>();

    @Serialize(order = 0)
    public final List<String> values;
    @Serialize(order = 1)
    public final String className;
    @Serialize(order = 2)
    public final String name;

    public Class<?> type;

    public DashEnumProperty(@Deserialize("values") List<String> values,
                            @Deserialize("className") String className,
                            @Deserialize("name") String name) {
        this.values = values;
        this.className = className;
        this.name = name;
    }

    public DashEnumProperty(EnumProperty property, DashRegistry registry, Integer value) {
        className = property.getType().getName();
        name = property.getName();
        values = new ArrayList<>();
        property.getValues().forEach(valuee -> values.add(valuee.toString()));
    }

    @Override
    public EnumProperty<?> toUndash(DashRegistry registry) {
        return get();
    }

    public <T extends Enum<T> & StringIdentifiable> EnumProperty<T> get() {
        type = getClass(className);
        return EnumProperty.of(name, (Class<T>) type, Arrays.asList(((Class<T>) type).getEnumConstants()));
    }

    private Class getClass(final String className) {
        final Class closs = cache.get(className);
        if (closs != null) return closs;
        try {
            final Class clz = Class.forName(className);
            cache.put(className, clz);
            return clz;
        } catch (Exception ignored) {
        }
        return null;
    }


    @Override
    public MethodHandle overrideMethodHandleForValue() {
        try {
            return createConstructor(DashEnumValue.class, Enum.class, FactoryType.PROPERTY_VALUE);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashEnumProperty that = (DashEnumProperty) o;
        return Objects.equals(values, that.values) && Objects.equals(className, that.className) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, className, name);
    }
}
