package net.quantumfusion.dash.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.List;

@Mixin(targets = "net.minecraft.client.render.model.ModelLoader$ModelDefinition")
public interface ModelLoaderAccessor {

    @Accessor("components")
    public List<UnbakedModel> getComponents();

    @Accessor("values")
    public List<Object> getValues();


//    @Invoker("<init>")
//    static ModelLoader.ModelDefinition newModelDefinition(List<UnbakedModel> components, List<Object> values) {
//        throw new AssertionError();
//    }
//
//    @Invoker("create")
//    static ModelLoader.ModelDefinition create(BlockState state, MultipartUnbakedModel rawModel, Collection<Property<?>> properties) {
//        throw new AssertionError();
//    }
//
//    @Invoker("create")
//    static ModelLoader.ModelDefinition create(BlockState state, UnbakedModel rawModel, Collection<Property<?>> properties) {
//        throw new AssertionError();
//    }

}
