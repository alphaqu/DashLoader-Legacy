package net.quantumfusion.dash.model.object;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.mixin.ModelOverrideAccessor;

import java.util.HashMap;
import java.util.Map;

public class DashModelOverride {

	@Serialize(order = 0)
	public final String modelId;

	@Serialize(order = 1)
	@SerializeNullable()
	@SerializeNullable(path = {1})
	@SerializeNullable(path = {0})
	public final Map<String, Float> predicateToThresholds;

	public DashModelOverride(@Deserialize("modelId") String modelId,
							 @Deserialize("predicateToThresholds") Map<String, Float> predicateToThresholds
	) {
		this.modelId = modelId;
		this.predicateToThresholds = predicateToThresholds;
	}

	public DashModelOverride(ModelOverride modelOverride) {
		modelId = modelOverride.getModelId().toString();
		Map<String, Float> out = new HashMap<>();
		((ModelOverrideAccessor) modelOverride).getPredicateToThresholdsD().forEach((identifier, aFloat) -> out.put(identifier.toString(), aFloat));
		predicateToThresholds = out;
	}

	public ModelOverride toUndash() {
		Map<Identifier, Float> out = new HashMap<>();
		predicateToThresholds.forEach((s, aFloat) -> out.put(Identifier.tryParse(s), aFloat));
		return new ModelOverride(Identifier.tryParse(modelId), out);
	}
}
