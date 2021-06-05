package net.quantumfusion.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.json.ModelOverride;
import net.quantumfusion.dashloader.DashRegistry;

public class DashModelOverrideCondition {
    @Serialize(order = 0)
    public final Integer type; // identifier
    @Serialize(order = 1)
    public final float threshold;

    public DashModelOverrideCondition(@Deserialize("type") Integer type, @Deserialize("threshold") float threshold) {
        this.type = type;
        this.threshold = threshold;
    }

    public DashModelOverrideCondition(ModelOverride.Condition condition, DashRegistry registry) {
        type = registry.createIdentifierPointer(condition.getType());
        threshold = condition.getThreshold();
    }

    public ModelOverride.Condition toUndash(DashRegistry registry) {
        return new ModelOverride.Condition(registry.getIdentifier(type), threshold);
    }
}
