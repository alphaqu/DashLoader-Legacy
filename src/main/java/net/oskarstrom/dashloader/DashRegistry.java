package net.oskarstrom.dashloader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.api.DataClass;
import net.oskarstrom.dashloader.api.enums.DashDataType;
import net.oskarstrom.dashloader.blockstate.DashBlockState;
import net.oskarstrom.dashloader.blockstate.property.DashProperty;
import net.oskarstrom.dashloader.data.DashID;
import net.oskarstrom.dashloader.data.DashIdentifier;
import net.oskarstrom.dashloader.data.DashRegistryData;
import net.oskarstrom.dashloader.data.registry.*;
import net.oskarstrom.dashloader.data.registry.storage.AbstractRegistryStorage;
import net.oskarstrom.dashloader.data.registry.storage.AdvancedRegistryStorage;
import net.oskarstrom.dashloader.data.registry.storage.FactoryRegistryStorage;
import net.oskarstrom.dashloader.data.registry.storage.SimpleRegistryStorage;
import net.oskarstrom.dashloader.data.registry.storage.impl.ModelFactoryRegistryStorage;
import net.oskarstrom.dashloader.data.registry.storage.impl.PredicateFactoryRegistryStorage;
import net.oskarstrom.dashloader.data.registry.storage.impl.PropertyValueFactoryRegistryStorage;
import net.oskarstrom.dashloader.font.DashFont;
import net.oskarstrom.dashloader.image.DashImage;
import net.oskarstrom.dashloader.image.DashSprite;
import net.oskarstrom.dashloader.model.components.DashBakedQuad;
import net.oskarstrom.dashloader.util.ThreadHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DashRegistry {


    private static int totalTasks = 6;
    private static int tasksDone = 0;
    public final SimpleRegistryStorage<Identifier, DashID> identifiers;
    public final SimpleRegistryStorage<NativeImage, DashImage> images;
    public final AdvancedRegistryStorage<BlockState, DashBlockState> blockstates;
    public final AdvancedRegistryStorage<Sprite, DashSprite> sprites;
    public final AdvancedRegistryStorage<BakedQuad, DashBakedQuad> bakedQuads;
    public final FactoryRegistryStorage<Font, DashFont> fonts;
    public final FactoryRegistryStorage<Property<?>, DashProperty> properties;
    public final ModelFactoryRegistryStorage models;
    public final PropertyValueFactoryRegistryStorage propertyValues;
    public final PredicateFactoryRegistryStorage predicates;
    public final List<DataClass> dataClasses;
    public Map<Class<?>, DashDataType> apiFailed = new ConcurrentHashMap<>();
    DashLoader loader;


    public DashRegistry(DashLoader loader) {
        identifiers = new SimpleRegistryStorage<>(Identifier.class, this, DashIdentifier::createIdentifier);
        images = new SimpleRegistryStorage<>(NativeImage.class, this, DashImage::new);
        blockstates = new AdvancedRegistryStorage<>(BlockState.class, this, DashBlockState::new);
        sprites = new AdvancedRegistryStorage<>(Sprite.class, this, DashSprite::new);
        bakedQuads = new AdvancedRegistryStorage<>(BakedQuad.class, this, DashBakedQuad::new);
        fonts = new FactoryRegistryStorage<>(Font.class, this, DashDataType.FONT);
        predicates = new PredicateFactoryRegistryStorage(Predicate.class, this, DashDataType.PREDICATE);
        properties = new FactoryRegistryStorage<>(Property.class, this, DashDataType.PROPERTY);
        propertyValues = new PropertyValueFactoryRegistryStorage(Comparable.class, this, DashDataType.PROPERTY_VALUE);
        models = new ModelFactoryRegistryStorage(BakedModel.class, this, DashDataType.MODEL);
        dataClasses = new ArrayList<>();
        this.loader = loader;
    }

    public Triple<DashRegistryData, RegistryImageData, RegistryModelData> createData() {
        return Triple.of(
                new DashRegistryData(
                        new RegistryBlockStateData(blockstates),
                        new RegistryFontData(fonts),
                        new RegistryIdentifierData(identifiers),
                        new RegistryPropertyData(properties),
                        new RegistryPropertyValueData(propertyValues),
                        new RegistrySpriteData(sprites),
                        new RegistryPredicateData(predicates),
                        new RegistryBakedQuadData(bakedQuads),
                        loader.getApi().dataClasses
                ),
                new RegistryImageData(images),
                new RegistryModelData(models));
    }

    public void loadData(DashRegistryData registryData) {
        blockstates.populate(registryData.blockStateRegistryData.blockstates);
        sprites.populate(registryData.spriteRegistryData.sprites);
        fonts.populate(registryData.fontRegistryData.fonts);
        predicates.populate(registryData.predicateRegistryData.predicates);
        properties.populate(registryData.propertyRegistryData.property);
        propertyValues.populate(registryData.propertyValueRegistryData.propertyValues);
        identifiers.populate(registryData.identifierRegistryData.identifiers);
        bakedQuads.populate(registryData.registryBakedQuadData.quads);
        dataClasses.addAll(registryData.dataClassList);
    }

    public void loadImageData(RegistryImageData dashImageData) {
        images.populate(dashImageData.images);
    }

    public void loadModelData(RegistryModelData registryModelData) {
        models.populateModels(registryModelData.toUndash());
    }


    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final BlockState getBlockstate(final int pointer) {
        return blockstates.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final Sprite getSprite(final int pointer) {
        return sprites.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final Identifier getIdentifier(final int pointer) {
        return identifiers.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final BakedModel getModel(final int pointer) {
        return models.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final Font getFont(final int pointer) {
        return fonts.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final NativeImage getImage(final int pointer) {
        return images.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final Predicate<BlockState> getPredicate(final int pointer) {
        return predicates.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final BakedQuad getBakedQuad(final int pointer) {
        return bakedQuads.getObject(pointer);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#getObject(int)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final Pair<Property<?>, Comparable<?>> getProperty(final int propertyPointer, final int valuePointer) {
        final Property<?> property = properties.getObject(propertyPointer);
        final Comparable<?> value = propertyValues.getObject(valuePointer);
        return Pair.of(property, value);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createBlockStatePointer(BlockState blockState) {
        return blockstates.register(blockState);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createSpritePointer(final Sprite sprite) {
        return sprites.register(sprite);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createImagePointer(final NativeImage image) {
        return images.register(image);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createBakedQuadPointer(final BakedQuad quad) {
        return bakedQuads.register(quad);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createIdentifierPointer(final Identifier identifier) {
        return identifiers.register(identifier);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link AbstractRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createFontPointer(final Font font) {
        return fonts.register(font);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2, Use the new method {@link FactoryRegistryStorage#register(Object)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createModelPointer(final BakedModel bakedModel) {
        return models.register(bakedModel);
    }

    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2,
     * Use the new method {@link PredicateFactoryRegistryStorage#register(MultipartModelSelector, StateManager)} instead.
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public final int createPredicatePointer(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        return predicates.register(selector, stateManager);
    }


    /**
     * @apiNote Get the storages by accessing the registry field
     * @deprecated This has been Deprecated and will be removed in 2.2,
     * Properties and Comparables are now split.
     * Use the new method {@link FactoryRegistryStorage#register(Object)} instead.
     */
    public final Pair<Integer, Integer> createPropertyPointer(final Property<?> property, final Comparable<?> value) {
        final int prop = properties.register(property);
        final int val = propertyValues.register(value);
        return Pair.of(prop, val);
    }

    public void toUndash() {
        Logger logger = LogManager.getLogger();
        try {
            tasksDone = 0;
            totalTasks = 9;
            for (DataClass dataClass : dataClasses) {
                dataClass.reload(this);
            }
            undashTask(identifiers, logger, "Identifiers");
            undashTask(images, logger, "Images");
            undashTask(properties, logger, "Properties");
            undashTask(propertyValues, logger, "Property Values");
            undashTask(blockstates, logger, "Blockstates");
            undashTask(predicates, logger, "Predicates");
            undashTask(sprites, logger, "Sprites");
            undashTask(bakedQuads, logger, "Quads");
            undashTask(fonts, logger, "Fonts");
            undashTask(models, logger, "Models");
            for (DataClass dataClass : dataClasses) {
                dataClass.apply(this);
            }
            log(logger, "Applying Model Overrides");
            models.getModelsToDeserialize().forEach(modelcategory -> DashLoader.THREAD_POOL.invoke(new ThreadHelper.UndashTask.ApplyTask(new ArrayList<>(modelcategory.values()), 100, this)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <D extends Dashable<O>, O> void undashTask(AbstractRegistryStorage<O, D> storage, Logger logger, String name) {
        log(logger, "Loading {} {}", storage.getSize(), name);
        storage.toUndash(logger);
    }

    public void log(Logger logger, String s, Object... params) {
        tasksDone++;
        logger.info("[" + tasksDone + "/" + totalTasks + "] " + s, params);
    }

    public void apiReport(Logger logger) {
        if (apiFailed.size() != 0) {
            logger.error("Found incompatible objects that were not able to be serialized.");
            int[] ints = new int[1];
            apiFailed.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().name)).forEach(entry -> {
                ints[0]++;
                logger.error("[" + entry.getValue().name() + "] Object: " + entry.getKey().getName());
            });
            logger.error("In total there are " + ints[0] + " incompatible objects. Please contact the mod developers to add support.");
        }
    }
}
