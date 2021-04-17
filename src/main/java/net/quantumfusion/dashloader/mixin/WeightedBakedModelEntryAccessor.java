package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBakedModel.Entry.class)
public interface WeightedBakedModelEntryAccessor {

    @Accessor
    BakedModel getModel();
}
