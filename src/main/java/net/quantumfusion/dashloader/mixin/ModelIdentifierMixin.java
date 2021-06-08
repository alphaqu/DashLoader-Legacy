package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.util.ModelIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelIdentifier.class)
public abstract class ModelIdentifierMixin {

    @Shadow
    @Final
    private String variant;

    /**
     * @author alphaqu, leocth
     * @reason DashLoader needs to replace the resource loading process completely. Thus, an overwrite is needed.
     */
    @Overwrite
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        //noinspection ConstantConditions
        if (super.equals(that) && that instanceof ModelIdentifier thatId) {
            return this.variant.equals(thatId.getVariant());
        } else {
            return false;
        }
    }
}
