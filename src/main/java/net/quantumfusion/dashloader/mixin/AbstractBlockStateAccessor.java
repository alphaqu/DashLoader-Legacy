package net.quantumfusion.dashloader.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.AbstractBlockState.class)
public interface AbstractBlockStateAccessor {


    @Accessor
    int getLuminance();

    @Accessor
    boolean getHasSidedTransparency();

    @Accessor
    boolean getIsAir();

    @Accessor
    Material getMaterial();

    @Accessor
    MaterialColor getMaterialColor();

    @Accessor
    float getHardness();

    @Accessor
    boolean getToolRequired();

    @Accessor
    boolean getOpaque();
}
