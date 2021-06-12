package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.Map;

public class RegistryPredicateData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "predicates")
    public Pointer2ObjectMap<DashPredicate> predicates;

    public RegistryPredicateData(@Deserialize("predicates") Pointer2ObjectMap<DashPredicate> predicates) {
        this.predicates = predicates;
    }


    public Map<Integer, DashPredicate> toUndash() {
        return predicates.convert();
    }

}
