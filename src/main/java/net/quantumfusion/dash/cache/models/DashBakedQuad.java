package net.quantumfusion.dash.cache.models;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dash.cache.DashDirection;
import net.quantumfusion.dash.cache.atlas.DashSprite;

public class DashBakedQuad {
    int[] vertexData;
    int colorIndex;
    DashDirection face;
    boolean shade;

    //post


    public DashBakedQuad(int[] vertexData, int colorIndex, DashDirection face, boolean shade) {
        this.vertexData = vertexData;
        this.colorIndex = colorIndex;
        this.face = face;
        this.shade = shade;
    }

    public DashBakedQuad(BakedQuad bakedQuad) {
        vertexData = bakedQuad.getVertexData();
        colorIndex = bakedQuad.getColorIndex();
        face = new DashDirection(bakedQuad.getFace());
        shade = bakedQuad.hasShade();
    }

    public BakedQuad toUndash(Sprite sprite) {
        return new BakedQuad(vertexData,colorIndex,face.toUndash(),sprite,shade);
    }
}
