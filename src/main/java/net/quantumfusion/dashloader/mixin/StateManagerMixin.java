package net.quantumfusion.dashloader.mixin;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.util.duck.StateMultithreading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.concurrent.Executors;

@Mixin(StateManager.class)
public class StateManagerMixin<O, S extends State<O, S>> {

    @Redirect(method = "<init>(Ljava/util/function/Function;Ljava/lang/Object;Lnet/minecraft/state/StateManager$Factory;Ljava/util/Map;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/state/State;createWithTable(Ljava/util/Map;)V"))
    private void multiThreadWithTableCreation(State<O, S> state, Map<Map<Property<?>, Comparable<?>>, S> states) {
        if (StateMultithreading.tasks != null) {
            StateMultithreading.tasks.add(Executors.callable(() -> state.createWithTable(states)));
        }
    }

//    @Shadow
//    @Final
//    private O owner;
//    @Shadow
//    @Final

//    private Map<String, Property<?>> namedProperties;

//    private static <S extends State<?, S>, T extends Comparable<T>> MapCodec<S> method_30040(MapCodec<S> mapCodec, Supplier<S> supplier, String string, Property<T> property) {
//        return Codec.mapPair(mapCodec, property.getValueCodec().fieldOf(string).setPartial(() -> {
//            return property.createValue(supplier.get());
//        })).xmap((pair) -> {
//            return (pair.getFirst()).with(property, (pair.getSecond()).getValue());
//        }, (state) -> {
//            return com.mojang.datafixers.util.Pair.of(state, property.createValue(state));
//        });
//    }
//
//    private static Stream<List<Pair<Property<?>, Comparable<?>>>> generateCombinations(Collection<Property<?>> properties) {
//        Stream<List<Pair<Property<?>, Comparable<?>>>> stream = Stream.of(Collections.emptyList());
//        for (Property<?> property : properties) {
//            stream = stream.flatMap((prevCombination) -> property.getValues().stream().map((value) -> {
//                List<Pair<Property<?>, Comparable<?>>> newCombinations = new ArrayList<>(prevCombination);
//                newCombinations.add(Pair.of(property, value));
//                return newCombinations;
//            }));
//        }
//        return stream;
//    }
//
//    private MapCodec<S> createCodec(Collection<Map.Entry<String, Property<?>>> entries, Supplier<S> supplier) {
//        MapCodec<S> mapCodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));
//        for (Map.Entry<String, Property<?>> entry : entries) {
//            mapCodec = method_30040(mapCodec, supplier, entry.getKey(), entry.getValue());
//        }
//        return mapCodec;
//    }
//
//    @Inject(method = "build", at = @At(value = "HEAD"), cancellable = true)
//    private void buildInject(Function<O, S> ownerToStateFunction, StateManager.Factory<O, S> factory, CallbackInfoReturnable<StateManager<O, S>> cir) {
//        cir.setReturnValue(multiBuild(ownerToStateFunction, owner, factory, namedProperties));
//    }
//
//    private StateManager<O, S> multiBuild(Function<O, S> function, O object, StateManager.Factory<O, S> factory, Map<String, Property<?>> propertiesMap) {
//        //noinspection unchecked
//        final StateManager<O, S> stateManager = Unsafe.allocateInstance(StateManager.class);
//        //stfu ----------- ^
//        final StateManagerAccessor<O, S> access = (StateManagerAccessor<O, S>) stateManager;
//
//        access.setOwner(object);
//        final ImmutableSortedMap<String, Property<?>> properties = ImmutableSortedMap.copyOf(propertiesMap);
//        access.setProperties(properties);
//
//
//        Map<Map<Property<?>, Comparable<?>>, S> states = new ConcurrentHashMap<>();
//        final MapCodec<S> finalMapCodec = createCodec(properties.entrySet(), () -> function.apply(object));
//        generateCombinations(properties.values()).forEach((combinationList) -> {
//            ImmutableMap<Property<?>, Comparable<?>> immutableMap = ImmutableMap.copyOf(combinationList);
//            S state = factory.create(object, immutableMap, finalMapCodec);
//            states.put(immutableMap, state);
//        });
//
//        access.setStates(ImmutableList.copyOf(states.values()));
//        return stateManager;
//    }
//
//    private StateManager<O, S> fastBuild(Function<O, S> function, O object, StateManager.Factory<O, S> factory, Map<String, Property<?>> propertiesMap) {
//        //noinspection unchecked
//        final StateManager<O, S> stateManager = Unsafe.allocateInstance(StateManager.class);
//        //stfu ----------- ^
//        final StateManagerAccessor<O, S> access = (StateManagerAccessor<O, S>) stateManager;
//
//        access.setOwner(object);
//        final ImmutableSortedMap<String, Property<?>> properties = ImmutableSortedMap.copyOf(propertiesMap);
//        final int size = properties.size();
//        access.setProperties(properties);
//
//
//        Map<Map<Property<?>, Comparable<?>>, S> states = new LinkedHashMap<>();
//
//        Map<Comparable<?>[], Pair<SortedMap<Property<?>, Comparable<?>>, S>> myStates = new LinkedHashMap<>();
//
//        final MapCodec<S> finalMapCodec = createCodec(properties.entrySet(), () -> function.apply(object));
//        generateCombinations(properties.values()).forEach((combinationList) -> {
//
//            Comparable<?>[] values = new Comparable[size];
//            for (int i = 0, combinationListSize = combinationList.size(); i < combinationListSize; i++) {
//                values[i] = combinationList.get(i);
//            }
//            ImmutableSortedMap<Property<?>, Comparable<?>> immutableMap = ImmutableSortedMap.copyOf(combinationList);
//            S state = factory.create(object, immutableMap, finalMapCodec);
//            states.put(immutableMap, state);
//            myStates.put(values, Pair.of(immutableMap, state));
//        });
//
//
//        List<Pair<Pair<Table<Property<?>, Comparable<?>, S>, Table<Property<?>, Comparable<?>, S>>, S>> answers = new ArrayList<>();
//        states.forEach((propertyComparableMap, s) -> s.createWithTable(states));
//        myStates.forEach((values, pair) -> {
//            final HashBasedTable<Property<?>, Comparable<?>, S> withTable = HashBasedTable.create();
//            final SortedMap<Property<?>, Comparable<?>> entries = pair.getLeft();
//            final S s = pair.getRight();
//
//
//            int pos = 0;
//            for (Map.Entry<Property<?>, Comparable<?>> entry : entries.entrySet()) {
//                final Comparable<?> stateValue = entry.getValue();
//                final Property<?> stateProperty = entry.getKey();
//                final Comparable<?>[] clone = values.clone();
//                for (Comparable<?> value : stateProperty.getValues()) {
//                    if (value != stateValue) {
//                        clone[pos] = value;
//                        withTable.put(stateProperty, value, myStates.get(clone).getValue());
//                    }
//                }
//                pos++;
//            }
//
//
//            answers.add(Pair.of(Pair.of((withTable.isEmpty() ? withTable : ArrayTable.create(withTable)), ((StateAccessor) s).getWithTable()), s));
//        });
//
//        System.out.println(answers.size());
//
//        if (properties.size() > 3) {
//            System.out.println(answers.size());
//        }
//        access.setStates(ImmutableList.copyOf(states.values()));
//        return stateManager;

//    }


}
