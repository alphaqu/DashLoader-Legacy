package net.quantumfusion.dashloader.model.predicates;

import com.google.common.base.Splitter;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.data.serialization.Pointer2PointerMap;
import net.quantumfusion.dashloader.mixin.accessor.SimpleMultipartModelSelectorAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@DashObject(SimpleMultipartModelSelector.class)
public class DashSimplePredicate implements DashPredicate {
    private static final Splitter VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();

    @Serialize(order = 0)
    public final Pointer2PointerMap properties;

    @Serialize(order = 1)
    public final boolean negate;

    public DashSimplePredicate(@Deserialize("properties") Pointer2PointerMap properties,
                               @Deserialize("negate") boolean negate) {
        this.properties = properties;
        this.negate = negate;
    }


    public DashSimplePredicate(SimpleMultipartModelSelector simpleMultipartModelSelector, DashRegistry registry, StateManager<Block, BlockState> stateManager) {
        SimpleMultipartModelSelectorAccessor access = ((SimpleMultipartModelSelectorAccessor) simpleMultipartModelSelector);
        Property<?> stateManagerProperty = stateManager.getProperty(access.getKey());
        properties = new Pointer2PointerMap();
        String string = access.getValueString();
        negate = !string.isEmpty() && string.charAt(0) == '!';
        if (negate) {
            string = string.substring(1);
        }
        List<String> list = VALUE_SPLITTER.splitToList(string);
        if (list.size() == 1) {
            Pair<Integer, Integer> predicateProperty = createPredicateInfo(stateManager, stateManagerProperty, string, registry);
            properties.put(predicateProperty.getLeft(), predicateProperty.getRight());
        } else {
            List<Pair<Integer, Integer>> predicateProperties = list.stream().map((stringx) -> createPredicateInfo(stateManager, stateManagerProperty, stringx, registry)).collect(Collectors.toList());
            predicateProperties.forEach(pair -> properties.put(pair.getLeft(), pair.getRight()));
        }

    }


    private Pair<Integer, Integer> createPredicateInfo(StateManager<Block, BlockState> stateFactory, Property<?> property, String valueString, DashRegistry registry) {
        Optional<?> optional = property.parse(valueString);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' '%s'", valueString, stateFactory.getOwner().toString()));
        } else {
            return registry.createPropertyPointer(property, (Comparable<?>) optional.get());
        }
    }

    @Override
    public Predicate<BlockState> toUndash(DashRegistry registry) {
        List<Map.Entry<? extends Property<?>, ? extends Comparable<?>>> out = new ArrayList<>();
        properties.forEach((entry) -> out.add(registry.getProperty(entry.key, entry.value)));
        Predicate<BlockState> outPredicate;
        if (out.size() == 1) {
            final Map.Entry<? extends Property<?>, ? extends Comparable<?>> entry = out.get(0);
            outPredicate = createPredicate(entry);
        } else {
            List<Predicate<BlockState>> list2 = out.stream().map(this::createPredicate).collect(Collectors.toList());
            outPredicate = (blockState) -> list2.stream().anyMatch((predicate) -> predicate.test(blockState));

        }
        return negate ? outPredicate.negate() : outPredicate;
    }


    private Predicate<BlockState> createPredicate(Map.Entry<? extends Property<?>, ? extends Comparable<?>> entry) {
        return (blockState) -> blockState.get(entry.getKey()).equals(entry.getValue());
    }

}
