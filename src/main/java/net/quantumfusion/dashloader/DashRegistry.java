package net.quantumfusion.dashloader;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.api.font.FontFactory;
import net.quantumfusion.dashloader.api.model.ModelFactory;
import net.quantumfusion.dashloader.api.predicate.PredicateFactory;
import net.quantumfusion.dashloader.api.property.PropertyFactory;
import net.quantumfusion.dashloader.blockstate.DashBlockState;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;
import net.quantumfusion.dashloader.data.DashID;
import net.quantumfusion.dashloader.data.DashIdentifier;
import net.quantumfusion.dashloader.data.DashRegistryData;
import net.quantumfusion.dashloader.data.registry.*;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.image.DashImage;
import net.quantumfusion.dashloader.image.DashSprite;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.DashModelIdentifier;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;
import net.quantumfusion.dashloader.model.predicates.DashStaticPredicate;
import net.quantumfusion.dashloader.util.ThreadHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DashRegistry {


    private static int totalTasks = 6;
    private static int tasksDone = 0;
    private final Map<Integer, DashModel> models;
    public Map<Class, FactoryType> apiFailed = new ConcurrentHashMap<>();
    public Int2ObjectMap<BlockState> blockstatesOut;
    public Int2ObjectMap<Predicate<BlockState>> predicateOut;
    public Int2ObjectMap<Identifier> identifiersOut;
    public Int2ObjectMap<BakedModel> modelsOut;
    public Int2ObjectMap<Sprite> spritesOut;
    public Int2ObjectMap<Font> fontsOut;
    public Int2ObjectMap<NativeImage> imagesOut;
    public Int2ObjectMap<Property<?>> propertiesOut;
    public Int2ObjectMap<Comparable<?>> propertyValuesOut;
    DashLoader loader;
    private Int2ObjectMap<DashBlockState> blockstates;
    private Int2ObjectMap<DashSprite> sprites;
    private Int2ObjectMap<DashID> identifiers;
    private Int2ObjectMap<DashFont> fonts;
    private Int2ObjectMap<DashImage> images;
    private Int2ObjectMap<DashPredicate> predicates;
    private Int2ObjectMap<DashProperty> properties;
    private Int2ObjectMap<DashPropertyValue> propertyValues;

    private List<Int2ObjectMap<DashModel>> modelsToDeserialize;


    public DashRegistry(Int2ObjectMap<DashBlockState> blockstates,
                        Int2ObjectMap<DashSprite> sprites,
                        Int2ObjectMap<DashID> identifiers,
                        Int2ObjectMap<DashModel> models,
                        Int2ObjectMap<DashFont> fonts,
                        Int2ObjectMap<DashImage> images,
                        Int2ObjectMap<DashPredicate> predicates,
                        Int2ObjectMap<DashProperty> properties,
                        Int2ObjectMap<DashPropertyValue> propertyValues) {
        this.blockstates = blockstates;
        this.sprites = sprites;
        this.identifiers = identifiers;
        this.models = models;
        this.fonts = fonts;
        this.images = images;
        this.predicates = predicates;
        this.properties = properties;
        this.propertyValues = propertyValues;
    }

    public DashRegistry(DashLoader loader) {
        blockstates = new Int2ObjectOpenHashMap<>();
        sprites = new Int2ObjectOpenHashMap<>();
        identifiers = new Int2ObjectOpenHashMap<>();
        models = new Int2ObjectOpenHashMap<>();
        fonts = new Int2ObjectOpenHashMap<>();
        predicates = new Int2ObjectOpenHashMap<>();
        images = new Int2ObjectOpenHashMap<>();
        properties = new Int2ObjectOpenHashMap<>();
        propertyValues = new Int2ObjectOpenHashMap<>();
        modelsToDeserialize = new ArrayList<>();
        this.loader = loader;
    }

    public Triple<DashRegistryData, RegistryImageData, RegistryModelData> createData() {
        return Triple.of(
                new DashRegistryData(
                        new RegistryBlockStateData(new Pointer2ObjectMap<>(blockstates)),
                        new RegistryFontData(new Pointer2ObjectMap<>(fonts)),
                        new RegistryIdentifierData(new Pointer2ObjectMap<>(identifiers)),
                        new RegistryPropertyData(new Pointer2ObjectMap<>(properties)),
                        new RegistryPropertyValueData(new Pointer2ObjectMap<>(propertyValues)),
                        new RegistrySpriteData(new Pointer2ObjectMap<>(sprites)),
                        new RegistryPredicateData(new Pointer2ObjectMap<>(predicates))
                ),
                new RegistryImageData(new Pointer2ObjectMap<>(images)),
                getModels());
    }

    public void loadData(DashRegistryData registryData) {
        blockstates = registryData.blockStateRegistryData.toUndash();
        sprites = registryData.spriteRegistryData.toUndash();
        fonts = registryData.fontRegistryData.toUndash();
        predicates = registryData.predicateRegistryData.toUndash();
        properties = registryData.propertyRegistryData.toUndash();
        propertyValues = registryData.propertyValueRegistryData.toUndash();
        identifiers = registryData.identifierRegistryData.toUndash();
    }

    public void loadImageData(RegistryImageData dashImageData) {
        images = dashImageData.toUndash();
    }


    public void loadModelData(RegistryModelData modelData) {
        this.modelsToDeserialize = modelData.toUndash();
    }


    public RegistryModelData getModels() {
        Map<Integer, Pointer2ObjectMap<DashModel>> modelsToAdd = new HashMap<>();
        for (Map.Entry<Integer, DashModel> entry : models.entrySet()) {
            final DashModel value = entry.getValue();
            modelsToAdd.computeIfAbsent(value.getStage(), Pointer2ObjectMap::new).put(entry.getKey(), value);
        }
        return new RegistryModelData(new Pointer2ObjectMap<>(modelsToAdd));
    }


    public Integer createBlockStatePointer(BlockState blockState) {
        final int hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState, this));
        }
        return hash;
    }

    public final Integer createModelPointer(final BakedModel bakedModel) {
        if (bakedModel == null) {
            return null;
        }
        final Integer hash = bakedModel.hashCode();
        if (models.get(hash) == null) {
            ModelFactory model = loader.getApi().modelMappings.get(bakedModel.getClass());
            if (model != null) {
                models.put(hash, model.toDash(bakedModel, this, DashLoader.getVanillaData().getModelData(bakedModel)));
            } else {
                apiFailed.putIfAbsent(bakedModel.getClass(), FactoryType.MODEL);
            }
        }
        return hash;
    }

    public final Integer createSpritePointer(final Sprite sprite) {
        final int hash = sprite.hashCode();
        if (sprites.get(hash) == null) {
            sprites.put(hash, new DashSprite(sprite, this));
        }
        return hash;
    }

    public final Integer createIdentifierPointer(final Identifier identifier) {
        final int hash = identifier.hashCode();
        if (identifiers.get(hash) == null) {
            if (identifier instanceof ModelIdentifier) {
                identifiers.put(hash, new DashModelIdentifier((ModelIdentifier) identifier));
            } else {
                identifiers.put(hash, new DashIdentifier(identifier));
            }
        }
        return hash;
    }

    public final Integer createImagePointer(final NativeImage image) {
        final int hash = image.hashCode();
        if (images.get(hash) == null) {
            images.put(hash, new DashImage(image));
        }
        return hash;
    }

    public final Integer createPredicatePointer(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final int hash = selector.hashCode();
        if (predicates.get(hash) == null) {
            predicates.put(hash, obtainPredicate(selector, stateManager));
        }
        return hash;
    }

    public final DashPredicate obtainPredicate(final MultipartModelSelector selector, final StateManager<Block, BlockState> stateManager) {
        final boolean isTrue = selector == MultipartModelSelector.TRUE;
        if (selector == MultipartModelSelector.FALSE || isTrue) {
            return new DashStaticPredicate(isTrue);
        } else {
            PredicateFactory predicateFactory = loader.getApi().predicateMappings.get(selector.getClass());
            if (predicateFactory != null) {
                return predicateFactory.toDash(selector, this, stateManager);
            } else {
                apiFailed.putIfAbsent(selector.getClass(), FactoryType.PREDICATE);
            }
        }
        return null;
    }


    public final Integer createFontPointer(final Font font) {
        final int hash = font.hashCode();
        if (fonts.get(hash) == null) {
            FontFactory fontFactory = loader.getApi().fontMappings.get(font.getClass());
            if (fontFactory != null) {
                fonts.put(hash, fontFactory.toDash(font, this, null));
            } else {
                apiFailed.putIfAbsent(font.getClass(), FactoryType.FONT);
            }
        }
        return hash;
    }

    public final Pair<Integer, Integer> createPropertyPointer(final Property<?> property, final Comparable<?> value) {
        final int hashV = value.hashCode();
        final int hashP = property.hashCode();
        final boolean propVal = !propertyValues.containsKey(hashV);
        final boolean prop = !properties.containsKey(hashP);
        if (propVal || prop) {
            PropertyFactory propertyFactory = loader.getApi().propertyMappings.get(property.getClass());
            if (propertyFactory != null) {
                if (propVal) {
                    propertyValues.put(hashV, propertyFactory.toDash(value, this, hashP));
                }
                if (prop) {
                    properties.put(hashP, propertyFactory.toDash(property, this, hashV));
                }
            } else {
                apiFailed.put(property.getClass(), FactoryType.PROPERTY);
            }
        }
        return Pair.of(hashP, hashV);
    }

    public final BlockState getBlockstate(final int pointer) {
        return logIfNullThenReturn(blockstatesOut, pointer, "BlockState");
    }

    public final Sprite getSprite(final int pointer) {
        return logIfNullThenReturn(spritesOut, pointer, "Sprite");
    }

    public final Identifier getIdentifier(final int pointer) {
        return logIfNullThenReturn(identifiersOut, pointer, "Identifier");
    }

    public final BakedModel getModel(final int pointer) {
        return logIfNullThenReturn(modelsOut, pointer, "BakedModel");
    }

    public final Font getFont(final int pointer) {
        return logIfNullThenReturn(fontsOut, pointer, "Font");
    }

    public final NativeImage getImage(final int pointer) {
        return logIfNullThenReturn(imagesOut, pointer, "NativeImage");
    }

    public final Predicate<BlockState> getPredicate(final int pointer) {
        return logIfNullThenReturn(predicateOut, pointer, "Predicate");
    }

    public final Pair<Property<?>, Comparable<?>> getProperty(final int propertyPointer, final int valuePointer) {
        final Property<?> property = propertiesOut.get(propertyPointer);
        final Comparable<?> value = propertyValuesOut.get(valuePointer);
        if (property == null || value == null) {
            DashLoader.LOGGER.error("Property not found in data. PINTR: " + propertyPointer + "/" + valuePointer);
        }
        return Pair.of(property, value);
    }


    private <T> T logIfNullThenReturn(final Int2ObjectMap<T> map, final int ptr, final String typeStr) {
        final T t = map.get(ptr);
        if (t == null) {
            //reified type parameters when?  - leocth
            //DashLoader.LOGGER.error(T.class.getSimpleName() + " not found in data. PINTR: " + ptr);
            DashLoader.LOGGER.error(typeStr + " not found in data. PINTR: " + ptr);
        }
        return t;
    }

    public void toUndash() {
        Logger logger = LogManager.getLogger();
        try {
            tasksDone = 0;
            totalTasks = 4 + modelsToDeserialize.size();
            log(logger, "Loading Simple Objects");
            identifiersOut = ThreadHelper.execParallel(identifiers, this);
            imagesOut = ThreadHelper.execParallel(images, this);
            identifiers = null;
            images = null;

            log(logger, "Loading Properties");
            propertiesOut = ThreadHelper.execParallel(properties, this);
            propertyValuesOut = ThreadHelper.execParallel(propertyValues, this);
            properties = null;
            propertyValues = null;

            log(logger, "Loading Advanced Objects");
            blockstatesOut = ThreadHelper.execParallel(blockstates, this);
            predicateOut = ThreadHelper.execParallel(predicates, this);
            spritesOut = ThreadHelper.execParallel(sprites, this);
            fontsOut = ThreadHelper.execParallel(fonts, this);
            blockstates = null;
            predicates = null;
            sprites = null;
            fonts = null;

            modelsOut = new Int2ObjectOpenHashMap<>((int) Math.ceil(modelsToDeserialize.size() / 0.75));
            final short[] currentStage = {0};
            modelsToDeserialize.forEach(modelCategory -> {
                log(logger, "Loading " + modelCategory.size() + " Models: " + "[" + currentStage[0] + "]");
                modelsOut.putAll(ThreadHelper.execParallel(modelCategory, this));
                currentStage[0]++;
            });
            log(logger, "Applying Model Overrides");
            modelsToDeserialize.forEach(modelcategory -> DashLoader.THREAD_POOL.invoke(new ThreadHelper.UndashTask.ApplyTask(new ArrayList<>(modelcategory.values()), 100, this)));
            modelsToDeserialize = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(Logger logger, String s) {
        tasksDone++;
        logger.info("[" + tasksDone + "/" + totalTasks + "] " + s);
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
