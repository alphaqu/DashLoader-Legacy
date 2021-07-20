package net.oskarstrom.dashloader;

public interface Dashable<K> {
	K toUndash(DashRegistry registry);
}
