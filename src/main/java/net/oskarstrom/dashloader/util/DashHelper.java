package net.oskarstrom.dashloader.util;

import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.data.serialization.PairMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DashHelper {

    public static <O, I> O nullable(I input, Function<I, O> func) {
        return input == null ? null : func.apply(input);
    }


    public static <O, I> O nullable(I input) {
        return (O) nullable(input, i -> i);
    }

    public static <O, I> O nullable(I input, DashRegistry registry, BiFunction<I, DashRegistry, O> func) {
        return input == null ? null : func.apply(input, registry);
    }

    /**
     * @param in   The Original Hashmap
     * @param func The Conversion Code
     * @param <K>  Key
     * @param <V>  Value
     * @param <OK> Out Key
     * @param <OV> Out Value
     * @return The Converted Hashmap
     */
    public static <K, V, OK, OV> Map<OK, OV> convertMap(Map<K, V> in, BiFunction<K, V, Map.Entry<OK, OV>> func) {
        final Map<OK, OV> out = new HashMap<>((int) (in.size() / 0.75));
        in.forEach((one, two) -> {
            final Map.Entry<OK, OV> apply = func.apply(one, two);
            out.put(apply.getKey(), apply.getValue());
        });
        return out;
    }

    public static <K, V, OK> Map<OK, V> convertMapKeys(Map<K, V> in, Function<K, OK> func) {
        return convertMap(in, (k, v) -> Pair.of(func.apply(k), v));
    }

    public static <K, V, OV> Map<K, OV> convertMapValues(Map<K, V> in, Function<V, OV> func) {
        return convertMap(in, (k, v) -> Pair.of(k, func.apply(v)));
    }

    public static <V, OV> void convertArrays(V[] in, OV[] out, Function<V, OV> func) {
        for (int i = 0; i < in.length; i++) {
            out[i] = func.apply(in[i]);
        }
    }


    public static <K, V> PairMap<K, V> listPairToPM(List<Pair<K, V>> in) {
        final PairMap<K, V> pairMap = new PairMap<>();
        in.forEach(kvPair -> pairMap.put(kvPair.getKey(), kvPair.getValue()));
        return pairMap;
    }

    public static <V, OV> List<OV> convertList(Iterable<V> in, Function<V, OV> func) {
        final List<OV> out = new ArrayList<>();
        in.forEach(v -> out.add(func.apply(v)));
        return out;
    }

    public static <K, V, OK, OV> PairMap<OK, OV> convertMapToPM(Map<K, V> in, BiFunction<K, V, Map.Entry<OK, OV>> func) {
        final PairMap<OK, OV> out = new PairMap<>(in.size());
        in.forEach((one, two) -> {
            final Map.Entry<OK, OV> apply = func.apply(one, two);
            out.put(apply.getKey(), apply.getValue());
        });
        return out;
    }


    public static <K, V, OK, OV> PairMap<OK, OV> convertPMtoPM(PairMap<K, V> in, BiFunction<K, V, PairMap.Entry<OK, OV>> func) {
        final PairMap<OK, OV> out = new PairMap<>(in.size());
        in.forEach((one, two) -> {
            final PairMap.Entry<OK, OV> apply = func.apply(one, two);
            out.put(apply.key, apply.value);
        });
        return out;
    }


    public static <K, V, OK, OV> Map<OK, OV> convertPairMapToMap(PairMap<K, V> in, BiFunction<K, V, Map.Entry<OK, OV>> func) {
        final HashMap<OK, OV> out = new HashMap<>(in.size());
        in.forEach((one, two) -> {
            final Map.Entry<OK, OV> apply = func.apply(one, two);
            out.put(apply.getKey(), apply.getValue());
        });
        return out;
    }


    public static <T> void registryForEach(Iterable<T> iterable, DashRegistry registry, BiConsumer<T, DashRegistry> action) {
        iterable.forEach(t -> action.accept(t, registry));
    }

}
