package net.quantumfusion.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.json.ModelOverride;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.ModelOverrideAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashModelOverride {

    @Serialize(order = 0)
    public final Integer modelId;

    @Serialize(order = 1)
    public final List<DashModelOverrideCondition> conditions;

    public DashModelOverride(@Deserialize("modelId") Integer modelId,
                             @Deserialize("conditions") List<DashModelOverrideCondition> conditions
    ) {
        this.modelId = modelId;
        this.conditions = conditions;
    }

    public DashModelOverride(ModelOverride modelOverride, DashRegistry registry) {
        modelId = registry.createIdentifierPointer(modelOverride.getModelId());
        conditions = new ArrayList<>();
        ((ModelOverrideAccessor) modelOverride).getConditions().forEach(condition -> conditions.add(new DashModelOverrideCondition(condition, registry)));
    }

    public ModelOverride toUndash(DashRegistry registry) {
        List<ModelOverride.Condition> out = new ArrayList<>();
        conditions.forEach(dashModelOverrideCondition -> out.add(dashModelOverrideCondition.toUndash(registry)));
        return new ModelOverride(registry.getIdentifier(modelId), out);
    }
}
