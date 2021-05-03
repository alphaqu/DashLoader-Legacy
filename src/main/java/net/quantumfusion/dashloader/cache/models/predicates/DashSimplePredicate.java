package net.quantumfusion.dashloader.cache.models.predicates;

import com.google.common.base.Splitter;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.SimpleMultipartModelSelectorAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashSimplePredicate implements DashPredicate {
    private static final Splitter VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();

    @Serialize(order = 0)
    @SerializeNullable()
    public Map<Long, Long> property;

    @Serialize(order = 1)
    public boolean negate;

    public DashSimplePredicate(@Deserialize("property") Map<Long, Long> property,
                               @Deserialize("negate") boolean negate) {
        this.property = property;
        this.negate = negate;
    }


    public DashSimplePredicate(SimpleMultipartModelSelector simpleMultipartModelSelector, StateManager<Block, BlockState> stateManager, DashRegistry registry) {
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
            property = new HashMap<>();
            if (list.size() == 1) {
                Pair<Long, Long> predic = createPredicateInfo(stateManager, stateManagerProperty, string, registry);
                property.put(predic.getLeft(), predic.getRight());
            } else {
                List<Pair<Long, Long>> predic = list.stream().map((stringx) -> createPredicateInfo(stateManager, stateManagerProperty, stringx, registry)).collect(Collectors.toList());
                predic.forEach(integerIntegerPair -> property.put(integerIntegerPair.getLeft(), integerIntegerPair.getRight()));
            }
        }
    }


    private Pair<Long, Long> createPredicateInfo(StateManager<Block, BlockState> stateFactory, Property<?> property, String valueString, DashRegistry registry) {
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
        property.forEach((propertyPointer, valuePointer) -> out.add(registry.getProperty(propertyPointer, valuePointer)));
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
