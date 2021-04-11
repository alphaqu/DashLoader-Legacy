package net.quantumfusion.dash.cache;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dash.cache.models.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class DashModelLoader {
    //output
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;

    Path modelPath = FileSystems.getDefault().getPath("C:\\Program Files (x86)\\inkscape\\MinecraftMods\\Dash\\run\\config\\dash\\model.activej");
    Path atlasPath = FileSystems.getDefault().getPath("C:\\Program Files (x86)\\inkscape\\MinecraftMods\\Dash\\run\\config\\dash\\atlas.activej");
    Path blockstatePath = FileSystems.getDefault().getPath("C:\\Program Files (x86)\\inkscape\\MinecraftMods\\Dash\\run\\config\\dash\\blockstate.activej");



    public DashModelLoader(SpriteAtlasManager atlasManager,
                           Object2IntMap<BlockState> stateLookup,
                           Map<Identifier, BakedModel> models) {
        modelsOut = new HashMap<>();


        System.out.println("Caching atlas");
        try {
            BinarySerializer<DashSpriteAtlasManager> serializer = SerializerBuilder.create().build(DashSpriteAtlasManager.class);
            StreamOutput output = StreamOutput.create(Files.newOutputStream(atlasPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            output.serialize(serializer, new DashSpriteAtlasManager(atlasManager));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Caching blockstates");
        try {
            BinarySerializer<DashBlockStateData> serializer = SerializerBuilder.create().build(DashBlockStateData.class);
            StreamOutput output = StreamOutput.create(Files.newOutputStream(blockstatePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            output.serialize(serializer, new DashBlockStateData(stateLookup));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Caching models");
        try {
            BinarySerializer<DashModelData> serializer = SerializerBuilder.create().build(DashModelData.class);
            StreamOutput output = StreamOutput.create(Files.newOutputStream(modelPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            output.serialize(serializer, new DashModelData(models, this));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Caching complete.");
    }

    public DashBakedModel convertSimpleModel(BakedModel model) {
        if (model instanceof BasicBakedModel) {
            return new DashBasicBakedModel((BasicBakedModel) model, this);
        } else if (model instanceof BuiltinBakedModel) {
            return new DashBuiltinBakedModel((BuiltinBakedModel) model, this);
        } else if (model instanceof MultipartBakedModel) {
            return new DashMultipartBakedModel((MultipartBakedModel) model, this);
        } else if (model instanceof WeightedBakedModel) {
            return new DashWeightedBakedModel((WeightedBakedModel) model, this);
        } else {
            if (model != null) {
                System.err.println(model.getClass().getCanonicalName() + " is not supported by Dash, ask the developer to add support.");
            }
        }
        return null;
    }


    public void load() {
        System.out.println("Deserializing atlas");
        try {
            BinarySerializer<DashSpriteAtlasManager> serializer = SerializerBuilder.create().build(DashSpriteAtlasManager.class);
            StreamInput input = StreamInput.create(Files.newInputStream(atlasPath));
            DashSpriteAtlasManager atlas = input.deserialize(serializer);
            System.out.println("Loading atlas");
            atlasManagerOut = atlas.toUndash();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Deserializing blockstates");
        try {
            BinarySerializer<DashBlockStateData> serializer = SerializerBuilder.create().build(DashBlockStateData.class);
            StreamInput input = StreamInput.create(Files.newInputStream(blockstatePath));
            DashBlockStateData blockStateData = input.deserialize(serializer);
            System.out.println("Loading blockstates");
            stateLookupOut = blockStateData.toUndash();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Deserializing models");
        Pair<Map<DashID, DashBakedModel>, Map<DashID, DashBakedModel>> modelsToUndash = null;
        try {
            BinarySerializer<DashModelData> serializer = SerializerBuilder.create().build(DashModelData.class);
            StreamInput input = StreamInput.create(Files.newInputStream(blockstatePath));
            DashModelData modelData = input.deserialize(serializer);
            System.out.println("Loading models");
            modelsToUndash = modelData.toUndash();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //load basicmodels
        System.out.println("Loading simple models");
        modelsToUndash.getLeft().forEach((dashIdentifier, dashBakedModel) -> modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(this)));
        System.out.println("Loading complex models");
        //load other models
        modelsToUndash.getRight().forEach((dashIdentifier, dashBakedModel) -> modelsOut.put(dashIdentifier.toUndash(), dashBakedModel.toUndash(this)));
        System.out.println("Loaded DashCache");
    }


}
