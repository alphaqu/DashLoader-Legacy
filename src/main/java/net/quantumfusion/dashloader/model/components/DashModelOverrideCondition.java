package net.quantumfusion.dashloader.model.components;

import net.minecraft.client.render.model.json.ModelOverride;
import net.quantumfusion.dashloader.DashRegistry;

public class DashModelOverrideCondition {
    public final Long type; // identifier
    public final float threshold;

    public DashModelOverrideCondition(Long type, float threshold) {
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
