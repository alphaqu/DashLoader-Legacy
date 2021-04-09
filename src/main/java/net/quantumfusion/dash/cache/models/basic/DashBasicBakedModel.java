package net.quantumfusion.dash.cache.models.basic;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.DashDirection;
import net.quantumfusion.dash.cache.models.DashBakedModel;
import net.quantumfusion.dash.cache.models.DashBakedQuad;
import net.quantumfusion.dash.cache.models.DashModelOverrideList;
import net.quantumfusion.dash.cache.models.DashModelTransformation;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.BasicBakedModelAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;
import net.quantumfusion.dash.mixin.SpriteAtlasTextureAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBasicBakedModel implements DashBakedModel {
    List<DashBakedQuad> quads;
    Map<DashDirection, List<DashBakedQuad>> faceQuads;
    boolean usesAo;
    boolean hasDepth;
    boolean isSideLit;
    DashModelTransformation transformation;
    DashModelOverrideList itemPropertyOverrides;

    DashIdentifier spritePointer;


    public DashBasicBakedModel(BasicBakedModel basicBakedModel) {
        BasicBakedModelAccessor access = ((BasicBakedModelAccessor) basicBakedModel);
        quads = new ArrayList<>();
        faceQuads = new HashMap<>();
        access.getQuads().forEach(bakedQuad -> quads.add(new DashBakedQuad(bakedQuad)));
        access.getFaceQuads().forEach((direction, bakedQuads) -> {
            List<DashBakedQuad> out = new ArrayList<>();
            bakedQuads.forEach(bakedQuad -> out.add(new DashBakedQuad(bakedQuad)));
            faceQuads.put(new DashDirection(direction), out);
        });

        itemPropertyOverrides = new DashModelOverrideList(access.getItemPropertyOverrides());
        usesAo = access.getUsesAo();
        hasDepth = access.getHasDepth();
        isSideLit = access.getIsSideLit();
        transformation = new DashModelTransformation(access.getTransformation());
        spritePointer = new DashIdentifier(access.getSprite().getId());
    }


    @Override
    public BakedModel toUndash(SpriteAtlasManager manager) {
        Identifier id = spritePointer.toUndash();
        Sprite sprite = null;
        for (Map.Entry<Identifier, SpriteAtlasTexture> entry : ((SpriteAtlasManagerAccessor) manager).getAtlases().entrySet()) {
            SpriteAtlasTexture spriteAtlasTexture = entry.getValue();
            if (((SpriteAtlasTextureAccessor) spriteAtlasTexture).getSprites().containsKey(id)) {
                sprite = spriteAtlasTexture.getSprite(id);
            }
        }
        if(sprite == null){
            throw new DashException("Sprite not found in deserialized sprite cache: " + id);
        }

        List<BakedQuad> quadsOut = new ArrayList<>();
        Map<Direction, List<BakedQuad>> faceQuadsOut = new HashMap<>();
        for (DashBakedQuad dashBakedQuad : quads) {
            quadsOut.add(dashBakedQuad.toUndash(sprite));
        }
        for (Map.Entry<DashDirection, List<DashBakedQuad>> entry : faceQuads.entrySet()) {
            DashDirection dashDirection = entry.getKey();
            List<DashBakedQuad> dashBakedQuads = entry.getValue();
            List<BakedQuad> out = new ArrayList<>();
            for (DashBakedQuad dashBakedQuad : dashBakedQuads) {
                out.add(dashBakedQuad.toUndash(sprite));
            }
            faceQuadsOut.put(dashDirection.toUndash(), out);
        }

        return new BasicBakedModel(quadsOut, faceQuadsOut, usesAo, isSideLit, hasDepth, sprite, transformation.toUndash(), itemPropertyOverrides.toUndash());
    }
}
