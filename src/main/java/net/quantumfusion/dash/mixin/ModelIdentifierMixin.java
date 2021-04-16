package net.quantumfusion.dash.mixin;

import net.minecraft.client.util.ModelIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelIdentifier.class)
public abstract class ModelIdentifierMixin {

    @Shadow @Final private String variant;

    @Shadow public abstract boolean equals(Object object);

    @Inject(method = "equals(Ljava/lang/Object;)Z",
            at = @At(value = "HEAD"), cancellable = true)
    private void equalsFast(Object object, CallbackInfoReturnable<Boolean> cir) {
        if (this == object) cir.setReturnValue(true);
        if (object == null || getClass() != object.getClass()) cir.setReturnValue(false);
        if (object instanceof ModelIdentifier && super.equals(object)) {
            cir.setReturnValue(this.variant.equals(((ModelIdentifier) object).getVariant()));
        } else {
            cir.setReturnValue(false);
        }
    }
}
