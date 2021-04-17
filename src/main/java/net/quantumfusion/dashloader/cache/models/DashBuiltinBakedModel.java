package net.quantumfusion.dashloader.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.components.DashModelOverrideList;
import net.quantumfusion.dashloader.cache.models.components.DashModelTransformation;
import net.quantumfusion.dashloader.mixin.BuiltinBakedModelAccessor;

public class DashBuiltinBakedModel implements DashModel,DashBakedModel {
    @Serialize(order = 0)
    public DashModelTransformation transformation;
    @Serialize(order = 1)
    public DashModelOverrideList itemPropertyOverrides;
    @Serialize(order = 2)
    public Integer spritePointer;
    @Serialize(order = 3)
    public boolean sideLit;

    public DashBuiltinBakedModel(@Deserialize("transformation") DashModelTransformation transformation,
                                 @Deserialize("itemPropertyOverrides") DashModelOverrideList itemPropertyOverrides,
                                 @Deserialize("spritePointer") Integer spritePointer,
                                 @Deserialize("sideLit") boolean sideLit) {
        this.transformation = transformation;
        this.itemPropertyOverrides = itemPropertyOverrides;
        this.spritePointer = spritePointer;
        this.sideLit = sideLit;
    }

    public DashBuiltinBakedModel() {
    }

    public DashBuiltinBakedModel(BuiltinBakedModel builtinBakedModel, DashRegistry registry) {
        BuiltinBakedModelAccessor access = ((BuiltinBakedModelAccessor) builtinBakedModel);
        transformation = new DashModelTransformation(access.getTransformation());
        itemPropertyOverrides = new DashModelOverrideList(access.getItemPropertyOverrides(), registry);
        spritePointer = registry.createSpritePointer(access.getSprite());
        sideLit = access.getSideLit();
    }


    @Override
    public BakedModel toUndash(DashRegistry registry) {
        Sprite sprite = registry.getSprite(spritePointer);
        return new BuiltinBakedModel(transformation.toUndash(), itemPropertyOverrides.toUndash(registry), sprite, sideLit);
    }

    @Override
    public void apply(DashRegistry registry) {
        itemPropertyOverrides.applyOverrides(registry);
    }

    @Override
    public DashModel toDash(BakedModel model, DashRegistry registry) {
        return new DashBuiltinBakedModel((BuiltinBakedModel) model,registry);
    }

    @Override
    public ModelStage getStage() {
        return ModelStage.SIMPLE;
    }
}
