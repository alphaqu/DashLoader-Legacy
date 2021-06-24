package net.quantumfusion.dashloader.api.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.model.DashModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ModelFactory extends Factory<BakedModel, DashModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> {

    default FactoryType getFactoryType() {
        return FactoryType.MODEL;
    }
}
