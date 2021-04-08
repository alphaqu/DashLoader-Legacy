package net.quantumfusion.dash.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.AbstractBlockState.class)
public interface AbstactBlockStateAccessor {


    @Accessor()
    AbstractBlock.ContextPredicate getSolidBlockPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate getSuffocationPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate getBlockVisionPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate getPostProcessPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate getEmissiveLightingPredicate();
}
