package net.quantumfusion.dash.cache.blockstates.properties;

import net.minecraft.state.property.IntProperty;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class DashIntProperty extends DashProperty {
    List<Integer> values;
    int value;


    public DashIntProperty(String type, String name, List<Integer> values, int value) {
        super(type, name);
        this.values = values;
        this.value = value;
    }


    public DashIntProperty(IntProperty property, String value) {
        super(property);
        values = new ArrayList<>();
        values.addAll(property.getValues());
        this.value = Integer.parseInt(value);
    }

    public MutablePair<IntProperty, Integer> toUndash() {
        MutablePair<IntProperty, Integer> out = new MutablePair<>();
        int lowest = -1;
        int highest = -1;
        for (Integer integer : values) {
            if(integer > highest|| highest == -1){
                highest = integer;
            }
            if(integer < lowest || lowest == -1){
                lowest = integer;
            }
        }
        out.setLeft(IntProperty.of(name, lowest, highest));
        out.setRight(value);
        return out;
    }


}
