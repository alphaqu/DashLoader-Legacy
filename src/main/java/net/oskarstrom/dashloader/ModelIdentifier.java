package net.oskarstrom.dashloader;

import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Objects;

public class ModelIdentifier extends Identifier {

    static final char SEPARATOR = '#';
    private final String variant;

    protected ModelIdentifier(String[] strings) {
        super(strings);
        this.variant = strings[2].toLowerCase(Locale.ROOT);
    }

    public ModelIdentifier(String string, String string2, String string3) {
        this(new String[]{string, string2, string3});
    }

    public ModelIdentifier(String string) {
        this(split(string));
    }

    public ModelIdentifier(Identifier id, String variant) {
        this(id.toString(), variant);
    }

    public ModelIdentifier(String string, String string2) {
        this(split(string + "#" + string2));
    }

    protected static String[] split(String id) {
        String[] strings = new String[]{null, id, ""};
        int i = id.indexOf(35);
        String string = id;
        if (i >= 0) {
            strings[2] = id.substring(i + 1, id.length());
            if (i > 1) {
                string = id.substring(0, i);
            }
        }

        System.arraycopy(Identifier.split(string, ':'), 0, strings, 0, 2);
        return strings;
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModelIdentifier that = (ModelIdentifier) o;
        return Objects.equals(variant, that.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), variant);
    }

    public String toString() {
        String var10000 = super.toString();
        return var10000 + "#" + this.variant;
    }
}
