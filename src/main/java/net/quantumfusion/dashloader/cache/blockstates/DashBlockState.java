package net.quantumfusion.dashloader.cache.blockstates;

import com.google.common.collect.ImmutableMap;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.*;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.blockstates.properties.*;
import net.quantumfusion.dashloader.mixin.StateAccessor;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DashBlockState {

    @Serialize(order = 0)
    public Integer owner;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeSubclasses(path = {0}, value = {
            DashBooleanProperty.class,
            DashDirectionProperty.class,
            DashEnumProperty.class,
            DashIntProperty.class
    })
    public List<DashProperty> entriesEncoded;


    public DashBlockState(@Deserialize("owner") Integer owner,
                          @Deserialize("entriesEncoded") List<DashProperty> entriesEncoded) {
        this.owner = owner;
        this.entriesEncoded = entriesEncoded;
    }

    public  <T extends Enum<T> & StringIdentifiable> DashBlockState(BlockState blockState, DashRegistry registry) {
        StateAccessor<Block, BlockState> accessState = ((StateAccessor<Block, BlockState>) blockState);
        entriesEncoded = new ArrayList<>();
        accessState.getEntries().forEach((property, comparable) -> {
            if (property instanceof BooleanProperty) {
                entriesEncoded.add(new DashBooleanProperty((BooleanProperty) property, (Boolean) comparable));
            } else if (property instanceof DirectionProperty) {
                entriesEncoded.add(new DashDirectionProperty((DirectionProperty) property, (Direction) comparable));
            } else if (property instanceof EnumProperty) {
                entriesEncoded.add(new DashEnumProperty((EnumProperty<T>) property, (Enum<T>) comparable));
            } else if (property instanceof IntProperty) {
                entriesEncoded.add(new DashIntProperty((IntProperty) property, comparable.toString()));
            }
        });
        owner = registry.createIdentifierPointer(Registry.BLOCK.getId(blockState.getBlock()));
    }

    public  <T extends Enum<T> & StringIdentifiable> BlockState toUndash(DashRegistry registry) {
        try {
            ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
            //TODO hardcoded, this is bad
            //above comment is correct, this is horrible
            entriesEncoded.forEach(property -> {
                if (property instanceof DashBooleanProperty) {
                    MutablePair<BooleanProperty, Boolean> out = ((DashBooleanProperty) property).toUndash();
                    builder.put(out.left, out.right);
                } else if (property instanceof DashDirectionProperty) {
                    MutablePair<DirectionProperty, Direction> out = ((DashDirectionProperty) property).toUndash();
                    builder.put(out.left, out.right);
                } else if (property instanceof DashEnumProperty) {
                    MutablePair<EnumProperty<T>, Enum<T>> out = ((DashEnumProperty) property).toUndash();
                    builder.put(out.left, out.right);
                } else if (property instanceof DashIntProperty) {
                    MutablePair<IntProperty, Integer> out = ((DashIntProperty) property).toUndash();
                    builder.put(out.left, out.right);
                }
            });

            return new BlockState(Registry.BLOCK.get(registry.getIdentifier(owner)), builder.build(), null);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.getLogger().error(Registry.BLOCK.get(registry.getIdentifier(owner)).getDefaultState().toString());
        }
        return null;
    }

}
