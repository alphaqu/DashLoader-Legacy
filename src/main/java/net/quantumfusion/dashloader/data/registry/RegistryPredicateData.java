package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;

import java.util.Map;

public class RegistryPredicateData {
    @Serialize(order = 0)
    @SerializeNullable(path = {0})
    @SerializeSubclasses(path = {1}, extraSubclassesId = "predicates")
    public Map<Long, DashPredicate> predicates;

    public RegistryPredicateData(@Deserialize("predicates") Map<Long, DashPredicate> predicates) {
        this.predicates = predicates;
    }
}
