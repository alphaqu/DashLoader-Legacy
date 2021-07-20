package net.oskarstrom.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.BakedQuad;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;
import net.oskarstrom.dashloader.data.DashDirection;

public class DashBakedQuad implements Dashable<BakedQuad> {
	@Serialize(order = 0)
	public final int[] vertexData;
	@Serialize(order = 1)
	public final int colorIndex;
	@Serialize(order = 2)
	public final DashDirection face;
	@Serialize(order = 3)
	public final boolean shade;
	@Serialize(order = 4)
	public final int sprite;

	public DashBakedQuad(@Deserialize("vertexData") int[] vertexData,
						 @Deserialize("colorIndex") int colorIndex,
						 @Deserialize("face") DashDirection face,
						 @Deserialize("shade") boolean shade,
						 @Deserialize("sprite") int sprite) {
		this.vertexData = vertexData;
		this.colorIndex = colorIndex;
		this.face = face;
		this.shade = shade;
		this.sprite = sprite;
	}

	public DashBakedQuad(BakedQuad bakedQuad, DashRegistry registry) {
		vertexData = bakedQuad.getVertexData();
		colorIndex = bakedQuad.getColorIndex();
		face = new DashDirection(bakedQuad.getFace());
		shade = bakedQuad.hasShade();
		sprite = registry.sprites.register(bakedQuad.getSprite());
	}

	public BakedQuad toUndash(DashRegistry registry) {
		return new BakedQuad(vertexData, colorIndex, face.toUndash(registry), registry.sprites.getObject(sprite), shade);
	}
}
