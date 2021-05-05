package net.quantumfusion.dashloader.models.predicates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.blockstates.properties.*;
import net.quantumfusion.dashloader.blockstates.properties.value.*;

public class PredicateHelper {


    public static <T extends Enum<T> & StringIdentifiable> DashProperty getProperty(Property<?> property) {
        if (property instanceof BooleanProperty) {
            return new DashBooleanProperty((BooleanProperty) property);
        } else if (property instanceof DirectionProperty) {
            return new DashDirectionProperty((DirectionProperty) property);
        } else if (property instanceof EnumProperty) {
            return new DashEnumProperty((EnumProperty<T>) property);
        } else if (property instanceof IntProperty) {
            return new DashIntProperty((IntProperty) property);
        } else {
            System.out.println("[PROPERTY]" + property.getClass());
        }
        return null;
    }


    public static <T extends Enum<T> & StringIdentifiable> DashPropertyValue getPropertyValue(Comparable<?> value, long hashP) {
        if (value instanceof Boolean) {
            return new DashBooleanValue((Boolean) value);
        } else if (value instanceof Direction) {
            return new DashDirectionValue((Direction) value);
        } else if (value instanceof Integer) {
            return new DashIntValue((Integer) value);
        } else if (value instanceof Enum) {
            return new DashEnumValue(((Enum) value).name(), hashP);
        } else {
            System.out.println("[PROPERTYVALUE]" + value.getClass() + " / " + value);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <P extends DashPredicate> P getPredicate(MultipartModelSelector selector, StateManager<Block, BlockState> stateManager, DashRegistry registry) {
        if (selector instanceof SimpleMultipartModelSelector) {
            return (P) new DashSimplePredicate((SimpleMultipartModelSelector) selector, stateManager, registry);
        } else if (selector instanceof AndMultipartModelSelector) {
            return (P) new DashAndPredicate((AndMultipartModelSelector) selector, stateManager, registry);
        } else if (selector instanceof OrMultipartModelSelector) {
            return (P) new DashOrPredicate((OrMultipartModelSelector) selector, stateManager, registry);
        } else if (selector != null) {
            return (P) new DashStaticPredicate(selector);
        } else {
            System.out.println("[PREDICATE]" + selector.getClass() + " / " + selector);
        }
        return null;
    }
}
