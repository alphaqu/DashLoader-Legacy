package net.oskarstrom.dashloader.data.serializers;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import net.oskarstrom.dashloader.DashException;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.util.ClassLoaderWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.function.Function;

import static io.activej.codegen.ClassBuilder.CLASS_BUILDER_MARKER;

public class DashSerializer<O> {
    private final DashLoader loader;


    Function<SerializerBuilder, BinarySerializer<O>> createSerializerFunction;
    BinarySerializer<O> serializer;
    String identifier;
    @Nullable
    File serializerCache;


    public DashSerializer(DashLoader loader, String identifier, Function<SerializerBuilder, BinarySerializer<O>> createSerializerFunction) {
        this.createSerializerFunction = createSerializerFunction;
        this.identifier = identifier;
        this.loader = loader;
    }

    public boolean createSerializer(boolean forceRecache) {
        if (!forceRecache) {
            Path serializerPath = getSerializerPath(identifier);
            if (serializerPath != null) {
                try {
                    final File serializerFile = serializerPath.toFile();
                    if (serializerFile.exists()) {
                        serializerCache = serializerFile;
                        serializer = loadSerializerCache(DashLoader.getInstance().getAssignedClassLoader(), serializerFile);
                        return true;
                    }
                } catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                final BinarySerializer<O> build = createSerializerFunction.apply(SerializerBuilder.create().withGeneratedBytecodePath(loader.getModBoundDir()));
                renameRawSerializer(identifier, build);
                serializer = build;
            } catch (IOException e) {
                if (!(e instanceof FileSystemException)) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public void serializeObject(O clazz, Path path, String name) {
        final DashLoader.TaskHandler taskHandler = DashLoader.TASK_HANDLER;
        try {
            taskHandler.setCurrentTask("Serializing " + name);
            taskHandler.setSubtasks(3);
            DashLoader.LOGGER.info("  Starting " + name + " Serialization.");
            StreamOutput output = StreamOutput.create(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            taskHandler.completedSubTask();
            output.serialize(serializer, clazz);
            taskHandler.completedSubTask();
            output.close();
            taskHandler.completedSubTask();
            DashLoader.LOGGER.info("    Finished " + name + " Serialization.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        taskHandler.completedTask();
    }

    @NotNull
    public O deserializeObject(Path path, String name) {
        try {
            if (serializer == null) {
                throw new DashException(name + " Serializer not created.");
            }
            O out = StreamInput.create(Files.newInputStream(path), 1048576).deserialize(serializer);
            if (out == null) {
                throw new DashException(name + " Deserialization failed");
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new DashException(name + " File failed");
    }


    private <K> BinarySerializer<K> loadSerializerCache(ClassLoaderWrapper classLoader, File serializer) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final byte[] bytes = IOUtils.toByteArray(FileUtils.openInputStream(serializer));
        return createSerializerInstance(classLoader, "io.activej.codegen.io.activej.serializer.BinarySerializer_" + serializer.getName().split("-")[0], bytes);
    }

    @SuppressWarnings("unchecked")
    private <T> BinarySerializer<T> createSerializerInstance(ClassLoaderWrapper classLoader, String actualClassName, byte[] bytecode) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass = classLoader.defineCustomClass(actualClassName, bytecode);
        try {
            Field field = aClass.getField(CLASS_BUILDER_MARKER);
            //noinspection ResultOfMethodCallIgnored
            field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new AssertionError(e);
        }
        return (BinarySerializer<T>) aClass.getConstructor().newInstance();
    }

    private Path getSerializerPath(String name) {
        File[] files = loader.getModBoundDir().toFile().listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().endsWith(name + ".serializer")) {
                return Paths.get(file.toURI());
            }
        }
        return null;
    }

    public void markCacheAsNull() {
        serializerCache = null;
    }

    private <K> void renameRawSerializer(String name, BinarySerializer<K> serializer) throws IOException {
        final String className = serializer.getClass().getName().replaceFirst("io.activej.codegen.", "");
        final Path folder = loader.getModBoundDir();
        final Path rawSerializerPath = folder.resolve(className + ".class");
        final Path destination = folder.resolve(className.replaceFirst("io.activej.serializer.BinarySerializer_", "") + "-" + name + ".serializer");
        try {
            Files.move(rawSerializerPath, destination, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(rawSerializerPath);
        }
        DashLoader.LOGGER.info("Created Serializer {}", className);
    }
}
