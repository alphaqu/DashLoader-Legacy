package net.quantumfusion.dash.registry;

import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Map;

public class DashPropertyRegistry {
    static Map<String, BlockSoundGroup> blockSoundGroupMappings;
    static Map<String, Material> materialMappings;

    public static String materialToId(Material material) {
        Material out;
        for (Map.Entry<String, Material> value : materialMappings.entrySet()) {
            if (value.getValue() == material) {
                return value.getKey();

            }
        }
        System.err.println(material.getClass().getCanonicalName() + "  not registered in Material Dash Registries, using default value which may break compatibility.");
        return "AIR";
    }

    public static String blockSoundGroupToId(BlockSoundGroup material) {
        Material out;
        for (Map.Entry<String, BlockSoundGroup> value : blockSoundGroupMappings.entrySet()) {
            if (value.getValue() == material) {
                return value.getKey();

            }
        }
        System.err.println(material.getClass().getCanonicalName() + "  not registered in BlockSoundGroup Dash Registries, using default value which may break compatibility.");
        return "WOOD";
    }


}
