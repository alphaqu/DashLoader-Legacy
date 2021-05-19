package net.quantumfusion.dashloader.api.models;

import net.minecraft.client.render.model.BakedModel;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.models.DashModel;

public interface ModelFactory extends Factory<BakedModel, DashModel> {

    default FactoryType getFactoryType() {
        return FactoryType.MODEL;
    }
}
