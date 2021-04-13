package net.quantumfusion.dash.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashID;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.cache.models.DashModelIdentifier;
import net.quantumfusion.dash.mixin.ModelOverrideAccessor;

import java.util.HashMap;
import java.util.Map;

public class DashModelOverride {

    @Serialize(order = 0)
    public final String modelId;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeSubclasses(path = {0}, value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    @SerializeNullable(path = {1})

    public final Map<DashID, Float> predicateToThresholds;

    public DashModelOverride(@Deserialize("modelId") String modelId,
                             @Deserialize("predicateToThresholds") Map<DashID, Float> predicateToThresholds
    ) {
        this.modelId = modelId;
        this.predicateToThresholds = predicateToThresholds;
    }

    public DashModelOverride(ModelOverride modelOverride) {
        modelId = modelOverride.getModelId().toString();
        predicateToThresholds = new HashMap<>();
        ((ModelOverrideAccessor) modelOverride).getPredicateToThresholdsD().forEach((identifier, aFloat) -> {
            DashID id = null;
            if (identifier instanceof ModelIdentifier) {
                id = new DashModelIdentifier((ModelIdentifier) identifier);
            } else if (identifier != null) {
                id = new DashIdentifier(identifier);
            } else {
                System.err.println(modelId + " identifier format is not supported by Dash, ask the developer to add support.");
            }
            predicateToThresholds.put(id, aFloat);
        });
    }

    public ModelOverride toUndash() {
        Map<Identifier, Float> out = new HashMap<>();
        predicateToThresholds.forEach((s, aFloat) -> out.put(s.toUndash(), aFloat));
        return new ModelOverride(Identifier.tryParse(modelId), out);
    }
}
