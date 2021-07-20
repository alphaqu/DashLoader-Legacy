package net.oskarstrom.dashloader.image.shader;

import com.google.common.collect.ImmutableMap;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.VertexFormat;
import net.oskarstrom.dashloader.mixin.accessor.VertexFormatAccessor;
import net.oskarstrom.dashloader.util.DashHelper;

import java.util.Map;

public class DashVertexFormat {
	@Serialize(order = 0)
	public Map<String, DashVertexFormatElement> elementMap;

	public DashVertexFormat(@Deserialize("elementMap") Map<String, DashVertexFormatElement> elementMap) {
		this.elementMap = elementMap;
	}

	public DashVertexFormat(VertexFormat vertexFormat) {
		this.elementMap = DashHelper.convertMapValues(((VertexFormatAccessor) vertexFormat).getElementMap(), DashVertexFormatElement::new);
	}

	public VertexFormat toUndash() {
		return new VertexFormat(ImmutableMap.copyOf(DashHelper.convertMapValues(elementMap, DashVertexFormatElement::toUndash)));
	}
}
