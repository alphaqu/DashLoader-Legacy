package net.oskarstrom.dashloader.data;

import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;

public interface DashID extends Dashable<Identifier> {

	Identifier toUndash(DashRegistry registry);
}
