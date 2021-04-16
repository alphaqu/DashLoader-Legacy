package net.quantumfusion.dash.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.mixin.ModelOverrideAccessor;

import java.util.HashMap;
import java.util.Map;

public class DashModelOverride {

    @Serialize(order = 0)
    public final Integer modelId;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public final Map<Integer, Float> predicateToThresholds;

    public DashModelOverride(@Deserialize("modelId") Integer modelId,
                             @Deserialize("predicateToThresholds") Map<Integer, Float> predicateToThresholds
    ) {
        this.modelId = modelId;
        this.predicateToThresholds = predicateToThresholds;
    }

    public DashModelOverride(ModelOverride modelOverride, DashCache loader) {
        modelId = loader.registry.createIdentifierPointer(modelOverride.getModelId());
        predicateToThresholds = new HashMap<>();
        ((ModelOverrideAccessor) modelOverride).getPredicateToThresholdsD().forEach((identifier, aFloat) -> predicateToThresholds.put(loader.registry.createIdentifierPointer(identifier), aFloat));
    }

    public ModelOverride toUndash(DashCache loader) {
        Map<Identifier, Float> out = new HashMap<>();
        predicateToThresholds.forEach((s, aFloat) -> out.put(loader.registry.getIdentifier(s), aFloat));
        return new ModelOverride(loader.registry.getIdentifier(modelId), out);
    }
}
