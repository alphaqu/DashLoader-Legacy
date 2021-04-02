package net.quantumfusion.dash.sprite;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.sprite.info.DashInfo;

import java.util.ArrayList;
import java.util.List;

public class SpriteInfoCache {
    @Serialize(order = 0)
    public List<DashInfo> info;
    @Serialize(order = 1)
    public DashIdentifier id;

    public SpriteInfoCache(@Deserialize("info") List<DashInfo> info,
                           @Deserialize("id") DashIdentifier id) {
        this.info = info;
        this.id = id;
    }

    public static SpriteInfoCache create(ArrayList<Sprite.Info> infos, Identifier id) {
        ArrayList<DashInfo> info = new ArrayList<>();
        infos.forEach(info1 -> info.add(new DashInfo(info1)));
        return new SpriteInfoCache(info,new DashIdentifier(id));
    }

    public ArrayList<Sprite.Info> toUndash() {
        ArrayList<Sprite.Info> out = new ArrayList<>();
        info.forEach(dashInfo -> out.add(dashInfo.toUndash()));
        return out;
    }
}
