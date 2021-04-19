package net.quantumfusion.dashloader.mixin;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.client.util.math.MatrixStack;
import net.quantumfusion.dashloader.misc.SuffixCreator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SuffixArray.class)
public abstract class SuffixArrayMixin {

    @Shadow private IntList suffixIndexToObjectIndex;

    @Shadow private IntList offsetInText;

    @Shadow protected abstract void printArray();

    @Shadow @Final private static boolean PRINT_ARRAY;

    @Shadow private int maxTextLength;

    @Shadow @Final private IntList characters;

    @Inject(method = "build()V",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void build(CallbackInfo ci) {
        final int i = this.characters.size();
        final int[] is = new int[i];
        final int[] js = new int[i];
        final int[] ks = new int[i];
        final int[] ls = new int[i];
        IntComparator intComparator = SuffixCreator.get(js,ks);
        Swapper swapper = (ix, j) -> {
            if (ix != j) {
                int k = js[ix];
                js[ix] = js[j];
                js[j] = k;
                k = ks[ix];
                ks[ix] = ks[j];
                ks[j] = k;
                k = ls[ix];
                ls[ix] = ls[j];
                ls[j] = k;
            }

        };

        int k;
        for(k = 0; k < i; ++k) {
            is[k] = this.characters.getInt(k);
        }

        k = 1;

        for(int l = Math.min(i, this.maxTextLength); k * 2 < l; k *= 2) {
            int n;
            for(n = 0; n < i; ls[n] = n++) {
                js[n] = is[n];
                ks[n] = n + k < i ? is[n + k] : -2;
            }

            Arrays.parallelQuickSort(0, i, intComparator, swapper);

            for(n = 0; n < i; ++n) {
                if (n > 0 && js[n] == js[n - 1] && ks[n] == ks[n - 1]) {
                    is[ls[n]] = is[ls[n - 1]];
                } else {
                    is[ls[n]] = n;
                }
            }
        }
        IntList intList = this.suffixIndexToObjectIndex;
        IntList intList2 = this.offsetInText;
        this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
        this.offsetInText = new IntArrayList(intList2.size());

        for(int o = 0; o < i; ++o) {
            int p = ls[o];
            this.suffixIndexToObjectIndex.add(intList.getInt(p));
            this.offsetInText.add(intList2.getInt(p));
        }

        if (PRINT_ARRAY) {
            this.printArray();
        }
        ci.cancel();
    }
}
