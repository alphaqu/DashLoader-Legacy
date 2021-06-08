package net.quantumfusion.dashloader.mixin;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(State.class)
public abstract class StateMixin<S> {


    @Shadow
    private Table<Property<?>, Comparable<?>, S> withTable;

    @Shadow
    @Final
    private ImmutableMap<Property<?>, Comparable<?>> entries;


    @Inject(method = "createWithTable", at = @At(value = "HEAD"), cancellable = true)
    private void fastCreateWithTable(Map<Map<Property<?>, Comparable<?>>, S> states, CallbackInfo ci) {
        if (this.withTable != null) throw new IllegalStateException();
        final Table<Property<?>, Comparable<?>, S> table = HashBasedTable.create();
        final Map<Property<?>, Comparable<?>> tempMap = new HashMap<>(this.entries);
        entries.forEach((property, value) -> {
            final Comparable<?> temp = tempMap.get(property);
            property.getValues().forEach(possibleValue -> {
                if (possibleValue != value) {
                    tempMap.replace(property, possibleValue);
                    table.put(property, possibleValue, states.get(tempMap));
                }
            });
            tempMap.replace(property, temp);
        });
        this.withTable = table;
        ci.cancel();
    }


}
