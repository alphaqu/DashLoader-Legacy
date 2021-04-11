package net.quantumfusion.dash.mixin;

import io.activej.serializer.stream.StreamOutput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.misc.DashSplashTextData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

    @Shadow
    @Final
    private static Identifier RESOURCE_ID;

    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void fastSplash(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> cir) {
        try {
            if (Dash.splashText != null) {
                System.out.println("loaded splash fast");
                cir.setReturnValue(Dash.splashText);
            } else {
                Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(RESOURCE_ID);
                List<String> var7;
                try {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                        var7 = bufferedReader.lines().map(String::trim).filter((string) -> string.hashCode() != 125780783).collect(Collectors.toList());
                    }
                } finally {
                    if (resource != null) {
                        resource.close();
                    }

                }
                StreamOutput output = StreamOutput.create(Files.newOutputStream(Dash.config.resolve("dash/" + "splash" + ".activej"), StandardOpenOption.CREATE, StandardOpenOption.WRITE));
                output.serialize(Dash.splashTextSerializer, new DashSplashTextData(var7));
                output.flush();
                cir.setReturnValue(var7);
            }
        } catch (IOException var36) {
            cir.setReturnValue(Collections.emptyList());
        }
    }
}
