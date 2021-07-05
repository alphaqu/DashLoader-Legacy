package net.oskarstrom.dashloader.mixin.feature.misc;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.search.SuffixArray;
import net.oskarstrom.dashloader.util.duck.MixinValues;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SuffixArray.class)
public abstract class SuffixArrayMixin {

    @Shadow
    @Final
    private IntList characters;

    @Shadow
    private int maxTextLength;

    @Shadow
    private IntList suffixIndexToObjectIndex;

    @Shadow
    private IntList offsetInText;

    @Shadow
    @Final
    private static boolean PRINT_ARRAY;

    @Shadow
    protected abstract void printArray();

    /**
     * @author notequalalpha
     */
    @Overwrite
    public void build() {
        int characters = this.characters.size();
        int[] characterIds = new int[characters];
        final int[] js = new int[characters];
        final int[] ks = new int[characters];
        int[] ls = new int[characters];
        //done some shit
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

        for (int i = 0; i < characters; ++i) {
            characterIds[i] = this.characters.getInt(i);
        }

        int k = 1;

        for (int l = Math.min(characters, this.maxTextLength); k * 2 < l; k *= 2) {
            int n;
            for (n = 0; n < characters; ls[n] = n++) {
                js[n] = characterIds[n];
                ks[n] = n + k < characters ? characterIds[n + k] : -2;
            }

            Arrays.parallelQuickSort(0, characters, MixinValues.func.apply(ks, js), swapper);

            for (n = 0; n < characters; ++n) {
                if (n > 0 && js[n] == js[n - 1] && ks[n] == ks[n - 1]) {
                    characterIds[ls[n]] = characterIds[ls[n - 1]];
                } else {
                    characterIds[ls[n]] = n;
                }
            }
        }

        IntList intList = this.suffixIndexToObjectIndex;
        IntList intList2 = this.offsetInText;
        this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
        this.offsetInText = new IntArrayList(intList2.size());

        for (int o = 0; o < characters; ++o) {
            int p = ls[o];
            this.suffixIndexToObjectIndex.add(intList.getInt(p));
            this.offsetInText.add(intList2.getInt(p));
        }

        if (PRINT_ARRAY) {
            this.printArray();
        }
    }
}
