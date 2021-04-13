package net.quantumfusion.dash.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.DashException;
import net.quantumfusion.dash.cache.atlas.DashSprite;
import net.quantumfusion.dash.cache.blockstates.DashBlockState;

import java.util.HashMap;
import java.util.Map;

public class DashRegistry {


    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, DashBlockState> blockstates;

    public Map<Integer, BlockState> blockstatesOut;

    @Serialize(order = 1)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, DashSprite> sprites;

    public Map<Integer, Sprite> spritesOut;

    public DashRegistry(@Deserialize("blockstates") Map<Integer, DashBlockState> blockstates,
                        @Deserialize("sprites") Map<Integer, DashSprite> sprites) {
        this.blockstates = blockstates;
        this.sprites = sprites;
    }

    public DashRegistry() {
        blockstates = new HashMap<>();
        sprites = new HashMap<>();
    }


    public int createBlockStatePointer(BlockState blockState) {
        final int hash = blockState.hashCode();
        if (blockstates.get(hash) == null) {
            blockstates.put(hash, new DashBlockState(blockState));
        }
        return hash;
    }

    public int createSpritePointer(Sprite sprite) {
        final int hash = sprite.hashCode();
        if (sprites.get(hash) == null) {
            sprites.put(hash, new DashSprite(sprite));
        }
        return hash;
    }

    public BlockState getBlockstate(Integer pointer) {
        if (blockstatesOut == null) {
            throw new DashException("Registry not deserialized");
        }
        BlockState blockstate = blockstatesOut.get(pointer);
        if (blockstate == null) {
            Dash.LOGGER.error("Blockstate not found in data. PINTR: " + pointer);
        }
        return blockstate;
    }

    public Sprite getSprite(Integer pointer) {
        if (spritesOut == null) {
            throw new DashException("Registry not deserialized");
        }
        Sprite sprite = spritesOut.get(pointer);
        if (sprite == null) {
            Dash.LOGGER.error("Sprite not found in data. PINTR: " + pointer);
        }
        return sprite;
    }

    public void toUndash() {
        spritesOut = new HashMap<>();
        blockstatesOut = new HashMap<>();
        blockstates.forEach((integer, dashBlockState) -> blockstatesOut.put(integer, dashBlockState.toUndash()));
        sprites.forEach((integer, sprite) -> spritesOut.put(integer, sprite.toUndash()));
    }

}
