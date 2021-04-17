package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelOverride.class)
public interface ModelOverrideAccessor {

    @Accessor("predicateToThresholds")
    Map<Identifier, Float> getPredicateToThresholdsD();
}
