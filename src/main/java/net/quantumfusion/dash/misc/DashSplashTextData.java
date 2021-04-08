package net.quantumfusion.dash.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.List;

public class DashSplashTextData {
	@Serialize(order = 0)
	public final List<String> splashList;

	public DashSplashTextData(@Deserialize("splashList") List<String> splashList) {
		this.splashList = splashList;
	}
}
