package net.quantumfusion.dashloader.mixin;


import net.minecraft.Bootstrap;
import net.quantumfusion.dashloader.util.TimeHelper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(Bootstrap.class)
public class BootStrapMixin {


    @Shadow
    @Final
    private static Logger LOGGER;

    private static Instant start;

    @Inject(method = "initialize", at = @At(value = "HEAD"), cancellable = true)
    private static void logInitialize(CallbackInfo ci) {
        LOGGER.info("Starting Bootstrap initialization");
        start = Instant.now();
    }

    @Inject(method = "initialize", at = @At(value = "TAIL"), cancellable = true)
    private static void logInitializeEnd(CallbackInfo ci) {
        LOGGER.info("Bootstrap initialization completeted in {}s", TimeHelper.getS1Decimal(start));
    }


}
