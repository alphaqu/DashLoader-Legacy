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
import net.oskarstrom.dashloader.api.DashDataClass;
import net.oskarstrom.dashloader.api.enums.DashDataType;
import net.oskarstrom.dashloader.blockstate.DashBlockState;
import net.oskarstrom.dashloader.blockstate.property.DashProperty;
import net.oskarstrom.dashloader.blockstate.property.value.DashPropertyValue;
import net.oskarstrom.dashloader.data.DashID;
import net.oskarstrom.dashloader.data.DashIdentifier;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class DashRegistry {


	private static int totalTasks = 6;
	private static int tasksDone = 0;
	public final SimpleRegistryStorage<Identifier, DashID> identifiers = new SimpleRegistryStorage<>(this, "Identifier", DashIdentifier::createIdentifier);
	public final SimpleRegistryStorage<NativeImage, DashImage> images = new SimpleRegistryStorage<>(this, "NativeImage", DashImage::new);
	public final AdvancedRegistryStorage<BlockState, DashBlockState> blockstates = new AdvancedRegistryStorage<>(this, "BlockState", DashBlockState::new);
	public final AdvancedRegistryStorage<Sprite, DashSprite> sprites = new AdvancedRegistryStorage<>(this, "Sprite", DashSprite::new);
	public final AdvancedRegistryStorage<BakedQuad, DashBakedQuad> bakedQuads = new AdvancedRegistryStorage<>(this, "BakedQuad", DashBakedQuad::new);
	public final FactoryRegistryStorage<Font, DashFont> fonts = new FactoryRegistryStorage<>(this, DashDataType.FONT);
	public final FactoryRegistryStorage<Property<?>, DashProperty> properties = new FactoryRegistryStorage<>(this, DashDataType.PROPERTY);
	public final FactoryRegistryStorage<Comparable<?>, DashPropertyValue> propertyValues = new PropertyValueFactoryRegistryStorage(this, DashDataType.PROPERTY_VALUE);
	public final PredicateFactoryRegistryStorage predicates = new PredicateFactoryRegistryStorage(this, DashDataType.PREDICATE);
	public final ModelFactoryRegistryStorage models = new ModelFactoryRegistryStorage(this, DashDataType.MODEL);
	public final List<AbstractRegistryStorage<?, ?>> registries = new ArrayList<>();
	public final List<DashDataClass> dataClasses = new ArrayList<>();
	public Map<Class<?>, DashDataType> apiFailed = new ConcurrentHashMap<>();


	public DashRegistry() {
		registries.add(identifiers);
		registries.add(images);
		registries.add(properties);
		registries.add(propertyValues);
		registries.add(blockstates);
		registries.add(predicates);
		registries.add(sprites);
		registries.add(bakedQuads);
		registries.add(fonts);
		registries.add(models);
	}

	/*
		Undashing everything (converting DashObjects to Minecraft Objects)
		 */
	public void toUndash() {
		Logger logger = LogManager.getLogger();
		try {
			tasksDone = 0;
			totalTasks = registries.size() + 1;
			dataClasses.forEach(dataClass -> dataClass.reload(this));
			registries.forEach(abstractRegistryStorage -> undashTask(abstractRegistryStorage, logger));
			dataClasses.forEach(dataClass -> dataClass.apply(this));
			log(logger, "Applying Model Overrides");
			models.getModelsToDeserialize().forEach(modelcategory -> ThreadHelper.applyExec(modelcategory, dashModel -> dashModel.apply(this)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <D extends Dashable<O>, O> void undashTask(AbstractRegistryStorage<O, D> storage, Logger logger) {
		log(logger, "Loading {} {}", storage.getSize(), storage.toString());
		storage.toUndash(logger);
	}

	public void log(Logger logger, String s, Object... params) {
		tasksDone++;
		logger.info("[" + tasksDone + "/" + totalTasks + "] " + s, params);
	}

	public void apiReport(Logger logger) {
		if (apiFailed.size() != 0) {
			logger.error("Found incompatible objects that were not able to be serialized.");
			AtomicInteger stage = new AtomicInteger();
			apiFailed.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getName())).forEach(entry -> {
				stage.incrementAndGet();
				logger.error("[" + entry.getValue().name() + "] Object: " + entry.getKey().getName());
			});
			logger.error("In total there are " + stage.get() + " incompatible objects. Please contact the mod developers to add support.");
		}
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
	public final Integer createModelPointer(final BakedModel bakedModel) {
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
	@Deprecated(since = "2.1", forRemoval = true)
	public final Pair<Integer, Integer> createPropertyPointer(final Property<?> property, final Comparable<?> value) {
		final int prop = properties.register(property);
		final int val = propertyValues.register(value);
		return Pair.of(prop, val);
	}
}
