package net.oskarstrom.dashloader.data.serialization;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Pointer2PointerMap {
	@Serialize(order = 0)
	public final List<Entry> data;

	public Pointer2PointerMap(@Deserialize("data") List<Entry> data) {
		this.data = data;
	}

	public Pointer2PointerMap(int size) {
		data = new ArrayList<>(size);
	}

	public Pointer2PointerMap(Map<Integer, Integer> map) {
		data = new ArrayList<>(map.size());
		map.forEach((integer, o) -> data.add(Entry.of(integer, o)));
	}

	public Pointer2PointerMap() {
		data = new ArrayList<>();
	}

	public void put(int key, int value) {
		data.add(Entry.of(key, value));
	}

	public void forEach(Consumer<Entry> action) {
		data.forEach(action);
	}


	public int size() {
		return data.size();
	}

	public Map<Integer, Integer> convert() {
		Map<Integer, Integer> map = new HashMap<>((int) (data.size() / 0.75));
		data.forEach(entry -> map.put(entry.key, entry.value));
		return map;
	}

	public static class Entry {
		@Serialize(order = 0)
		public final int key;
		@Serialize(order = 1)
		public final int value;

		public Entry(@Deserialize("key") int key,
					 @Deserialize("value") int value) {
			this.key = key;
			this.value = value;
		}

		public static Entry of(int key, int value) {
			return new Entry(key, value);
		}

	}
}
