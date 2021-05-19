package net.quantumfusion.dashloader.api.predicates;

import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.models.predicates.DashPredicate;
import net.quantumfusion.dashloader.models.predicates.DashStaticPredicate;

public class StaticPredicate implements PredicateFactory {


    @Override
    public <K> DashPredicate toDash(MultipartModelSelector modelSelector, DashRegistry registry, K var1) {
        return new DashStaticPredicate(modelSelector);
    }

    @Override
    public Class<? extends MultipartModelSelector> getType() {
        return MultipartModelSelector.class;
    }

    @Override
    public Class<? extends DashPredicate> getDashType() {
        return DashStaticPredicate.class;
    }
}
