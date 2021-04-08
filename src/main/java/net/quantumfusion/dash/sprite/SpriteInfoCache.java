package net.quantumfusion.dash.sprite;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.sprite.info.DashSpriteInfo;

import java.util.ArrayList;
import java.util.List;

public class SpriteInfoCache {
    @Serialize(order = 0)
    public List<DashSpriteInfo> info;
    @Serialize(order = 1)
    public DashIdentifier id;

    public SpriteInfoCache(@Deserialize("info") List<DashSpriteInfo> info,
                           @Deserialize("id") DashIdentifier id) {
        this.info = info;
        this.id = id;
    }

    public static SpriteInfoCache create(ArrayList<Sprite.Info> infos, Identifier id) {
        ArrayList<DashSpriteInfo> info = new ArrayList<>();
        infos.forEach(info1 -> info.add(new DashSpriteInfo(info1)));
        return new SpriteInfoCache(info,new DashIdentifier(id));
    }

    public ArrayList<Sprite.Info> toUndash() {
        ArrayList<Sprite.Info> out = new ArrayList<>();
        info.forEach(dashInfo -> out.add(dashInfo.toUndash()));
        return out;
    }
}
