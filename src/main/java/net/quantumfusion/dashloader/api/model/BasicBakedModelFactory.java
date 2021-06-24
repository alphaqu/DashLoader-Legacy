package net.quantumfusion.dashloader.api.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.model.DashBasicBakedModel;
import net.quantumfusion.dashloader.model.DashModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class BasicBakedModelFactory implements ModelFactory {


    /**
     * Creates the model to be stored.
     *
     * @param model
     * @param registry The registry
     * @param var1     An extra variable
     * @param <K>      An extra variable
     * @return A serializable model.
     */
    @Override
    public DashModel toDash(BakedModel model, DashRegistry registry, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> selectorsAndStates) {
        return new DashBasicBakedModel((BasicBakedModel) model, registry);
    }

    @Override
    public Class<? extends BakedModel> getType() {
        return BasicBakedModel.class;
    }

    @Override
    public Class<? extends DashModel> getDashType() {
        return DashBasicBakedModel.class;
    }

}
