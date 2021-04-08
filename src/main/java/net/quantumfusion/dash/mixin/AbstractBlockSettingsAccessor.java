package net.quantumfusion.dash.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@Mixin(AbstractBlock.Settings.class)
public interface AbstractBlockSettingsAccessor {
    @Accessor()
    Material getMaterial();

    @Accessor()
    Function<BlockState, MaterialColor> materialColorFactory();

    @Accessor()
    boolean collidable();

    @Accessor()
    BlockSoundGroup soundGroup();

    @Accessor()
    ToIntFunction<BlockState> luminance();

    @Accessor()
    float resistance();

    @Accessor()
    float hardness();

    @Accessor()
    boolean toolRequired();

    @Accessor()
    boolean randomTicks();

    @Accessor()
    float slipperiness();

    @Accessor()
    float velocityMultiplier();

    @Accessor()
    float jumpVelocityMultiplier();

    @Accessor()
    Identifier lootTableId();

    @Accessor()
    boolean opaque();

    @Accessor()
    AbstractBlock.TypedContextPredicate<EntityType<?>> allowsSpawningPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate solidBlockPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate suffocationPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate blockVisionPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate postProcessPredicate();

    @Accessor()
    AbstractBlock.ContextPredicate emissiveLightingPredicate();

    @Accessor()
    boolean getIsAir();
    @Accessor()
    boolean dynamic();

}
