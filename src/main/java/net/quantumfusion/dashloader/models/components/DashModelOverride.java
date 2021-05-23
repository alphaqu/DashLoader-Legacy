package net.quantumfusion.dashloader.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.ModelOverrideAccessor;
import net.quantumfusion.dashloader.util.PairMap;

import java.util.HashMap;
import java.util.Map;

public class DashModelOverride {

    @Serialize(order = 0)
    public final Long modelId;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public final PairMap<Long, Float> predicateToThresholds;

    public DashModelOverride(@Deserialize("modelId") Long modelId,
                             @Deserialize("predicateToThresholds") PairMap<Long, Float> predicateToThresholds
    ) {
        this.modelId = modelId;
        this.predicateToThresholds = predicateToThresholds;
    }

    public DashModelOverride(ModelOverride modelOverride, DashRegistry registry) {
        modelId = registry.createIdentifierPointer(modelOverride.getModelId());
        predicateToThresholds = new PairMap<>();
        ((ModelOverrideAccessor) modelOverride).getPredicateToThresholdsD().forEach((identifier, aFloat) -> predicateToThresholds.put(registry.createIdentifierPointer(identifier), aFloat));
    }

    public ModelOverride toUndash(DashRegistry registry) {
        Map<Identifier, Float> out = new HashMap<>();
        predicateToThresholds.forEach((s, aFloat) -> out.put(registry.getIdentifier(s), aFloat));
        return new ModelOverride(registry.getIdentifier(modelId), out);
    }
}
