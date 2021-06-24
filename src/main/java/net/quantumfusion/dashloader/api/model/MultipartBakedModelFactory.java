package net.quantumfusion.dashloader.api.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.DashMultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MultipartBakedModelFactory implements ModelFactory {
    @Override
    public DashModel toDash(BakedModel model, DashRegistry registry, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> selectorsAndStates) {
        return new DashMultipartBakedModel((MultipartBakedModel) model, registry, selectorsAndStates);
    }

    @Override
    public Class<? extends BakedModel> getType() {
        return MultipartBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashType() {
        return DashMultipartBakedModel.class;
    }

}
