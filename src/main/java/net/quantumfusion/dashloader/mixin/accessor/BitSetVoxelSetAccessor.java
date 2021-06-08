package net.quantumfusion.dashloader.mixin.accessor;


import net.minecraft.util.shape.BitSetVoxelSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BitSetVoxelSet.class)
public interface BitSetVoxelSetAccessor {


    @Invoker
    void invokeMethod_31942(int i, int j, int k, int l);

    @Invoker
    boolean invokeMethod_31938(int i, int j, int k, int l, int m);

    @Invoker
    boolean invokeIsColumnFull(int i, int j, int k, int l);
}
