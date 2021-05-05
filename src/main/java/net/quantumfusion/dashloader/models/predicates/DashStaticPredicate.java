package net.quantumfusion.dashloader.models.predicates;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.function.Predicate;

public class DashStaticPredicate implements DashPredicate {

    @Serialize(order = 0)
    public boolean value;

    public DashStaticPredicate(@Deserialize("value") boolean value) {
        this.value = value;
    }


    public DashStaticPredicate(MultipartModelSelector multipartModelSelector) {
        value = multipartModelSelector == MultipartModelSelector.TRUE;
    }

    @Override
    public Predicate<BlockState> toUndash(DashRegistry registry) {
        return (blockState) -> value;
    }

}
