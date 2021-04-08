package net.quantumfusion.dash.util;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class StringHelper {


	public static String idToFile(String s) {
		Map<Character, Character> replacements = new HashMap<>();
		replacements.put(':', '-');
		replacements.put('/', '_');
		StringBuilder output = new StringBuilder();
		for (Character c : s.toCharArray()) {
			output.append(replacements.getOrDefault(c, c));
		}
		return output.toString();
	}

	public static String idToFile(Identifier string) {
		return idToFile(string.toString());
	}
}
