package net.quantumfusion.dashloader.api.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.DashWeightedBakedModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class WeightedBakedModelFactory implements ModelFactory {
    @Override
    public DashModel toDash(BakedModel model, DashRegistry registry, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> selectorsAndStates) {
        return new DashWeightedBakedModel((WeightedBakedModel) model, registry);
    }

    @Override
    public Class<? extends BakedModel> getType() {
        return WeightedBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashType() {
        return DashWeightedBakedModel.class;
    }

}
