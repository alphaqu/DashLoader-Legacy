package net.quantumfusion.dashloader.cache.voxel.subclasses;

import net.minecraft.util.shape.BitSetVoxelSet;
import net.quantumfusion.dashloader.cache.voxel.DashVoxelSet;

public class DashBitSetVoxelSet {
    DashVoxelSet parent;
    int xMin;
    int yMin;
    int zMin;
    int xMax;
    int yMax;
    int zMax;

    public DashBitSetVoxelSet(DashVoxelSet parent, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        this.parent = parent;
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }

    public DashBitSetVoxelSet(BitSetVoxelSet bitSetVoxelSet) {

        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }


}
