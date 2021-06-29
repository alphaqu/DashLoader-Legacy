package net.quantumfusion.dashloader.mixin;

import com.google.common.collect.Table;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.util.duck.StateMultithreading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(State.class)
public class StateMixin<S> {

    @Shadow
    private Table<Property<?>, Comparable<?>, S> withTable;

    @Inject(method = "with", at = @At(value = "HEAD"), cancellable = true)
    private <T extends Comparable<T>, V extends T> void withInject(Property<T> property, V value, CallbackInfoReturnable<S> cir) {
        if (withTable == null) {
            DashLoader.THREAD_POOL.invokeAll(StateMultithreading.tasks);
            StateMultithreading.tasks.clear();
        }
    }

}