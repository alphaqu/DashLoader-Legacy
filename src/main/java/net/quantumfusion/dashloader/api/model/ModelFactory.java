package net.quantumfusion.dashloader.api.model;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.model.DashModel;

public interface ModelFactory extends Factory<BakedModel, DashModel> {

    default FactoryType getFactoryType() {
        return FactoryType.MODEL;
    }
}
