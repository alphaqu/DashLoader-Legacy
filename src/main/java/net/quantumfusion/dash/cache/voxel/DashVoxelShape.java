package net.quantumfusion.dash.cache.voxel;

import net.minecraft.util.shape.VoxelShape;
import net.quantumfusion.dash.mixin.VoxelShapeAccessor;

public class DashVoxelShape {
    public final DashVoxelSet voxels;

    public DashVoxelShape(DashVoxelSet voxels) {
        this.voxels = voxels;
    }

    public DashVoxelShape(VoxelShape voxelShape) {
        voxels = new DashVoxelSet(((VoxelShapeAccessor)voxelShape).getVoxels());
    }


}
