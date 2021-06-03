package net.quantumfusion.dashloader.mixin.accessor;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(FontStorage.class)
public interface FontStorageAccessor {


    @Accessor
    List<Font> getFonts();
}
