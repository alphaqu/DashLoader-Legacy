package net.oskarstrom.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.data.DashDirection;

public class DashBakedQuad {
    @Serialize(order = 0)
    public final int[] vertexData;
    @Serialize(order = 1)
    public final int colorIndex;
    @Serialize(order = 2)
    public final DashDirection face;
    @Serialize(order = 3)
    public final boolean shade;

    public DashBakedQuad(@Deserialize("vertexData") int[] vertexData,
                         @Deserialize("colorIndex") int colorIndex,
                         @Deserialize("face") DashDirection face,
                         @Deserialize("shade") boolean shade) {
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

    public BakedQuad toUndash(Sprite sprite, DashRegistry registry) {
        return new BakedQuad(vertexData, colorIndex, face.toUndash(registry), sprite, shade);
    }
}
