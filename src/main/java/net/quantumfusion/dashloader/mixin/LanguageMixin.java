package net.quantumfusion.dashloader.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@Mixin(Language.class)
public class LanguageMixin {
    @Shadow
    @Final
    private static Pattern TOKEN_PATTERN;

    @Shadow
    @Final
    private static Gson GSON;

    /**
     * @author alphaqu, leocth
     * @reason DashLoader needs to replace the resource loading process completely. Thus, an overwrite is needed.
     */
    @Overwrite
    public static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
        GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class).entrySet().forEach(stringJsonElementEntry -> {
            final String key = stringJsonElementEntry.getKey();
            entryConsumer.accept(key,
                    TOKEN_PATTERN.matcher(JsonHelper.asString(stringJsonElementEntry.getValue(), key)).replaceAll("%$1s"));
        });
    }
}
