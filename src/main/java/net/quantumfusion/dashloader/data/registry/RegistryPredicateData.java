package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

public class RegistryPredicateData {
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0}, extraSubclassesId = "predicates")
    public Pointer2ObjectMap<DashPredicate> predicates;

    public RegistryPredicateData(@Deserialize("predicates") Pointer2ObjectMap<DashPredicate> predicates) {
        this.predicates = predicates;
    }


    public Int2ObjectMap<DashPredicate> toUndash() {
        return predicates.convert();
    }

}
