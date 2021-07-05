package net.oskarstrom.dashloader.blockstate.property;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.FactoryConstructor;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    @DashConstructor(ConstructorMode.OBJECT)
    public DashEnumProperty(EnumProperty property) {
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
    public FactoryConstructor overrideMethodHandleForValue() {
        return DashLoader.getInstance().getApi().propertyValueMappings.get(Enum.class);
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
