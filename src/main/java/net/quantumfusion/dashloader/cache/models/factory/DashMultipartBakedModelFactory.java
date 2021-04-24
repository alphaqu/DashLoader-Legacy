package net.quantumfusion.dashloader.cache.models.factory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.cache.models.DashModel;
import net.quantumfusion.dashloader.cache.models.DashMultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class DashMultipartBakedModelFactory implements DashModelFactory{
    @Override
    public<K> DashModel toDash(BakedModel model, DashRegistry registry, K var1) {
        //noinspection unchecked
        return new DashMultipartBakedModel((MultipartBakedModel) model,registry, (Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>) var1);
    }

    @Override
    public Class<? extends BakedModel> getModelType() {
        return MultipartBakedModel.class;
    }
}
