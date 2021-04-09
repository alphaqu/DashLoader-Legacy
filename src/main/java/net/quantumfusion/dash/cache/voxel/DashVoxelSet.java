package net.quantumfusion.dash.cache.voxel;

import net.minecraft.util.shape.VoxelSet;

public class DashVoxelSet {
    public final int xSize;
    public final int ySize;
    public final int zSize;

    public DashVoxelSet(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public DashVoxelSet(VoxelSet voxelSet) {
        this.xSize = voxelSet.getXSize();
        this.ySize = voxelSet.getYSize();
        this.zSize = voxelSet.getZSize();
    }


}
