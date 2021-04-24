package net.quantumfusion.dashloader.cache.models.predicates;

import com.google.common.base.Splitter;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.cache.blockstates.properties.*;
import net.quantumfusion.dashloader.mixin.SimpleMultipartModelSelectorAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashSimplePredicate implements DashPredicate {
    private static final Splitter VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();

    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeSubclasses(path = {0}, value = {
            DashBooleanProperty.class,
            DashDirectionProperty.class,
            DashEnumProperty.class,
            DashIntProperty.class
    })
    public List<DashProperty> property;

    @Serialize(order = 1)
    public boolean negate;

    public DashSimplePredicate(@Deserialize("property") List<DashProperty> property,
                               @Deserialize("negate") boolean negate) {
        this.property = property;
        this.negate = negate;
    }


    public DashSimplePredicate(SimpleMultipartModelSelector simpleMultipartModelSelector, StateManager<Block, BlockState> stateManager) {
        SimpleMultipartModelSelectorAccessor access = ((SimpleMultipartModelSelectorAccessor) simpleMultipartModelSelector);
        Property<?> stateManagerProperty = stateManager.getProperty(access.getKey());
        if (stateManagerProperty == null) {
            System.out.println("fuck");
        } else {
            String string = access.getValueString();
            negate = !string.isEmpty() && string.charAt(0) == '!';
            if (negate) {
                string = string.substring(1);
            }
            List<String> list = VALUE_SPLITTER.splitToList(string);
            if (list.size() == 1) {
                property = new ArrayList<>();
                property.add(createPredicateInfo(stateManager, stateManagerProperty, string));
            } else {
                property = list.stream().map((stringx) -> createPredicateInfo(stateManager, stateManagerProperty, stringx)).collect(Collectors.toList());
            }
        }
    }


    private DashProperty createPredicateInfo(StateManager<Block, BlockState> stateFactory, Property<?> property, String valueString) {
        Optional<?> optional = property.parse(valueString);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' '%s'", valueString, stateFactory.getOwner().toString()));
        } else {
            return PredicateHelper.getProperty(property, (Comparable<?>) optional.get());
        }
    }

    @Override
    public Predicate<BlockState> toUndash() {
        List<Map.Entry<? extends Property<?>, ? extends Comparable<?>>> out = new ArrayList<>();
        property.forEach(dashProperty -> out.add(dashProperty.toUndash()));
        Predicate<BlockState> outPredicate;
        if (out.size() == 1) {
            outPredicate = createPredicate(out.get(0).getKey(), out.get(0).getValue());
        } else {
            List<Predicate<BlockState>> list2 = out.stream().map((entry) -> createPredicate(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            outPredicate = (blockState) -> list2.stream().anyMatch((predicate) -> predicate.test(blockState));

        }
        return negate ? outPredicate.negate() : outPredicate;
    }


    private Predicate<BlockState> createPredicate(Property<?> property, Comparable<?> value) {
        return (blockState) -> blockState.get(property).equals(value);
    }

}
