package net.oskarstrom.dashloader.mixin;

import net.minecraft.block.Blocks;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.util.duck.StateMultithreading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Blocks.class)
public class BlocksMixin {

    @Inject(method = "<clinit>()V", at = @At(value = "TAIL"))
    private static void clinitInject(CallbackInfo info) {
        if (StateMultithreading.tasks.size() != 0) {
            System.out.println(StateMultithreading.tasks.size());
            DashLoader.THREAD_POOL.invokeAll(StateMultithreading.tasks);
            StateMultithreading.tasks = null;
        }
    }

}
