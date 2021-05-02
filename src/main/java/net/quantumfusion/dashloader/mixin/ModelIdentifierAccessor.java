package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.util.ModelIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelIdentifier.class)
public interface ModelIdentifierAccessor {

    @Accessor
    void setVariant(String variant);

}
