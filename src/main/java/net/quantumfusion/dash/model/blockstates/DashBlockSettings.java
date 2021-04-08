package net.quantumfusion.dash.model.blockstates;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.AbstractBlockSettingsAccessor;
import net.quantumfusion.dash.registry.DashPropertyRegistry;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class DashBlockSettings {
    //Material id
    private String material;
    //MaterialCOloridtofactory
    private boolean collidable;
    //blocksoundgroup
    private String  soundGroup;
    private float resistance;
    private float hardness;
    private boolean toolRequired;
    private boolean randomTicks;
    private float slipperiness;
    private float velocityMultiplier;
    private float jumpVelocityMultiplier;
    private DashIdentifier lootTableId;
    private boolean opaque;
    private boolean isAir;
    private String allowsSpawningPredicate;
    private boolean dynamicBounds;

    public DashBlockSettings(String material, boolean collidable, String soundGroup,  float resistance, float hardness, boolean toolRequired, boolean randomTicks, float slipperiness, float velocityMultiplier, float jumpVelocityMultiplier, DashIdentifier lootTableId, boolean opaque, boolean isAir, String allowsSpawningPredicate,  boolean dynamicBounds) {
        this.material = material;
        this.collidable = collidable;
        this.soundGroup = soundGroup;
        this.resistance = resistance;
        this.hardness = hardness;
        this.toolRequired = toolRequired;
        this.randomTicks = randomTicks;
        this.slipperiness = slipperiness;
        this.velocityMultiplier = velocityMultiplier;
        this.jumpVelocityMultiplier = jumpVelocityMultiplier;
        this.lootTableId = lootTableId;
        this.opaque = opaque;
        this.isAir = isAir;
        this.allowsSpawningPredicate = allowsSpawningPredicate;
        this.dynamicBounds = dynamicBounds;
    }

    public DashBlockSettings(AbstractBlock.Settings settings) {
        AbstractBlockSettingsAccessor settingsAccess  = ((AbstractBlockSettingsAccessor)settings);
        this.material = DashPropertyRegistry.materialToId(settingsAccess.getMaterial());
        this.collidable = settingsAccess.collidable();
        this.soundGroup = DashPropertyRegistry.blockSoundGroupToId(settingsAccess.soundGroup());
        this.resistance = settingsAccess.resistance();
        this.hardness = settingsAccess.hardness();
        this.toolRequired = settingsAccess.toolRequired();
        this.randomTicks = settingsAccess.randomTicks();
        this.slipperiness = settingsAccess.slipperiness();
        this.velocityMultiplier = settingsAccess.velocityMultiplier();
        this.jumpVelocityMultiplier = settingsAccess.jumpVelocityMultiplier();
        this.lootTableId = new DashIdentifier(settingsAccess.lootTableId());
        this.opaque = settingsAccess.opaque();
        isAir = settingsAccess.getIsAir();
        this.allowsSpawningPredicate = settingsAccess.allowsSpawningPredicate().toString();
        this.dynamicBounds = settingsAccess.dynamic();
    }
}
