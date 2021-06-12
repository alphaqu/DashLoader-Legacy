package net.quantumfusion.dashloader;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.data.DashVanillaData;
import net.quantumfusion.dashloader.data.mappings.*;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTextureData;
import net.quantumfusion.dashloader.mixin.accessor.AbstractTextureAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAccessor;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAtlasTextureAccessor;
import net.quantumfusion.dashloader.util.TaskHandler;
import net.quantumfusion.dashloader.util.VanillaData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashMappings {
    private List<Pair<SpriteAtlasTexture, DashSpriteAtlasTextureData>> atlasesToRegister;
    public VanillaData vanillaData;
    public DashModelData modelData;
    public DashSpriteAtlasData spriteAtlasData;
    public DashBlockStateData blockStateData;
    public DashParticleData particleData;
    public DashFontManagerData fontManagerData;
    public DashSplashTextData splashTextData;

    public DashMappings() {
    }

    public void loadCacheData(DashVanillaData data) {
        this.modelData = data.modelMappings;
        this.spriteAtlasData = data.spriteAtlasMappings;
        this.blockStateData = data.blockStateMappings;
        this.particleData = data.particleMappings;
        this.fontManagerData = data.fontMappings;
        this.splashTextData = data.splashTextMappings;
    }

    public void loadVanillaData(VanillaData data, DashRegistry registry, TaskHandler taskHandler) {
        taskHandler.logAndTask("Mapping Blockstates");
        blockStateData = (new DashBlockStateData(data, registry));

        taskHandler.logAndTask("Mapping Models");
        modelData = (new DashModelData(data, registry));

        taskHandler.logAndTask("Mapping Particles");
        particleData = (new DashParticleData(data, registry));

        taskHandler.logAndTask("Mapping Fonts");
        fontManagerData = (new DashFontManagerData(data, registry));

        taskHandler.logAndTask("Mapping Splash Text");
        splashTextData = (new DashSplashTextData(data));

        taskHandler.logAndTask("Mapping Atlas");
        spriteAtlasData = (new DashSpriteAtlasData(data, registry));
    }

    public DashVanillaData createData() {
        return new DashVanillaData(blockStateData, fontManagerData, modelData, particleData, splashTextData, spriteAtlasData);
    }

    public void toUndash(DashRegistry registry, VanillaData data) {
        final Pair<SpriteAtlasManager, List<SpriteAtlasTexture>> spriteData = spriteAtlasData.toUndash(registry);


        data.loadCacheData(
                spriteData.getKey(),
                blockStateData.toUndash(registry),
                modelData.toUndash(registry),
                particleData.toUndash(registry),
                fontManagerData.toUndash(registry),
                splashTextData.toUndash());
        atlasesToRegister = new ArrayList<>();
        spriteData.getValue().forEach(atlasTexture -> atlasesToRegister.add(Pair.of(atlasTexture, vanillaData.getAtlasData(atlasTexture))));

        modelData = null;
        spriteAtlasData = null;
        blockStateData = null;
        particleData = null;
        fontManagerData = null;
        splashTextData = null;
    }

    public void registerAtlases(TextureManager textureManager) {
        atlasesToRegister.forEach((atlas) -> {
            //atlas registration
            final SpriteAtlasTexture atlasTexture = atlas.getLeft();
            final DashSpriteAtlasTextureData data = atlas.getRight();
            final Identifier id = atlasTexture.getId();
            final int glId = TextureUtil.generateTextureId();
            final int width = data.width;
            final int maxLevel = data.maxLevel;
            final int height = data.height;
            ((AbstractTextureAccessor) atlasTexture).setGlId(glId);
            //ding dong lwjgl here are their styles

            TextureUtil.prepareImage(glId, maxLevel, width, height);
            ((SpriteAtlasTextureAccessor) atlasTexture).getSprites().forEach((identifier, sprite) -> {
                final SpriteAccessor access = (SpriteAccessor) sprite;
                access.setAtlas(atlasTexture);
                access.setId(identifier);
                sprite.upload();
            });

            //helu textures here are the atlases
            textureManager.registerTexture(id, atlasTexture);
            atlasTexture.setFilter(false, maxLevel > 0);
            DashLoader.LOGGER.info("Allocated: {}x{}x{} {}-atlas", width, height, maxLevel, id);
        });
    }

    @NotNull
    public List<SpriteAtlasTexture> getAtlases() {
        final ArrayList<SpriteAtlasTexture> list = new ArrayList<>();
        atlasesToRegister.forEach(entry -> list.add(entry.getLeft()));
        return list;
    }
}
