package net.quantumfusion.dash.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.ParticleTextureDataAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DashParticleTextureData {

	@Nullable
	@Serialize(order = 0)
	@SerializeNullable
	public List<DashIdentifier> textureList;

	@Serialize(order = 1)
	public DashIdentifier id;

	public DashParticleTextureData(@Deserialize("textureList") @Nullable List<DashIdentifier> textureList,
								   @Deserialize("id") DashIdentifier id) {
		this.textureList = textureList;
		this.id = id;
	}

	public DashParticleTextureData(ParticleTextureData particleTextureData, Identifier id) {
		textureList = new ArrayList<>();
		this.id = new DashIdentifier(id);
		List<Identifier> particleTextureDataTextureList = particleTextureData.getTextureList();
		if (particleTextureDataTextureList == null) {
			textureList = null;
		} else {
			particleTextureDataTextureList.forEach(identifier -> textureList.add(new DashIdentifier(identifier)));
		}

	}

	public ParticleTextureData toUndash() {
		List<Identifier> textureListOut;
		if (textureList == null) {
			textureListOut = null;
		} else {
			textureListOut = new ArrayList<>();
			textureList.forEach(dashIdentifier -> textureListOut.add(dashIdentifier.toUndash()));
		}
		return ParticleTextureDataAccessor.newParticleTextureData(textureListOut);
	}
}
