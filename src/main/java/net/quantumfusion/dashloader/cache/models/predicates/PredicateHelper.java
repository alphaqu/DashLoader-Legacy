package net.quantumfusion.dashloader.cache.models.predicates;

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
import net.quantumfusion.dashloader.cache.blockstates.properties.*;

public class PredicateHelper {


    public static <T extends Enum<T> & StringIdentifiable>  DashProperty getProperty(Property<?> property, Comparable<?> value) {
        if (property instanceof BooleanProperty) {
            return new DashBooleanProperty((BooleanProperty) property, (Boolean) value);
        } else if (property instanceof DirectionProperty) {
            return new DashDirectionProperty((DirectionProperty) property, (Direction) value);
        } else if (property instanceof EnumProperty) {
            return new DashEnumProperty((EnumProperty<T>) property, (Enum<T>) value);
        } else if (property instanceof IntProperty) {
            return new DashIntProperty((IntProperty) property, value.toString());
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <P extends DashPredicate> P getPredicate(MultipartModelSelector selector, StateManager<Block, BlockState> stateManager) {
        if (selector instanceof SimpleMultipartModelSelector) {
            return (P) new DashSimplePredicate((SimpleMultipartModelSelector) selector,stateManager);
        } else if (selector instanceof AndMultipartModelSelector) {
            return (P) new DashAndPredicate((AndMultipartModelSelector) selector,stateManager);
        } else if (selector instanceof OrMultipartModelSelector) {
            return (P) new DashOrPredicate((OrMultipartModelSelector) selector,stateManager);
        } else if(selector instanceof MultipartModelSelector){
            return (P) new DashStaticPredicate(selector);
        } else {
            System.out.println();
        }
        return null;
    }
}
