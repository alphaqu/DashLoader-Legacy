package net.quantumfusion.dash.mixin;

import com.google.common.collect.Table;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(State.class)
public interface StateAccessor<O, S> {

	@Accessor("withTable")
	void setWithTable(Table<Property<?>, Comparable<?>, S> table);
}
