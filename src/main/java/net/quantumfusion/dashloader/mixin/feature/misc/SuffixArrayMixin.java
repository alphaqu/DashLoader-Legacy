package net.quantumfusion.dashloader.mixin.feature.misc;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.minecraft.client.search.SuffixArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SuffixArray.class)
public abstract class SuffixArrayMixin {

    @Redirect(method = "build()V",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/Arrays;quickSort(IILit/unimi/dsi/fastutil/ints/IntComparator;Lit/unimi/dsi/fastutil/Swapper;)V"))
    private void parallelQuickSort(int from, int to, IntComparator comp, Swapper swapper) {
        Arrays.parallelQuickSort(from, to, comp, swapper);
    }
}
