package net.quantumfusion.dash.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import org.jetbrains.annotations.Nullable;

public class DashModelTransformation {

    @Serialize(order = 0)
    @SerializeNullable
    @Nullable
    public final DashTransformation thirdPersonLeftHand;
    @Serialize(order = 1)
    @SerializeNullable
    @Nullable
    public final DashTransformation thirdPersonRightHand;
    @Serialize(order = 2)
    @SerializeNullable
    @Nullable
    public final DashTransformation firstPersonLeftHand;
    @Serialize(order = 3)
    @SerializeNullable
    @Nullable
    public final DashTransformation firstPersonRightHand;
    @Serialize(order = 4)
    @SerializeNullable
    @Nullable
    public final DashTransformation head;
    @Serialize(order = 5)
    @SerializeNullable
    @Nullable
    public final DashTransformation gui;
    @Serialize(order = 6)
    @SerializeNullable
    @Nullable
    public final DashTransformation ground;
    @Serialize(order = 7)
    @SerializeNullable
    @Nullable
    public final DashTransformation fixed;


    public DashModelTransformation(@Deserialize("thirdPersonLeftHand") @Nullable DashTransformation thirdPersonLeftHand,
                                   @Deserialize("thirdPersonRightHand") @Nullable DashTransformation thirdPersonRightHand,
                                   @Deserialize("firstPersonLeftHand") @Nullable DashTransformation firstPersonLeftHand,
                                   @Deserialize("firstPersonRightHand") @Nullable DashTransformation firstPersonRightHand,
                                   @Deserialize("head") @Nullable DashTransformation head,
                                   @Deserialize("gui") @Nullable DashTransformation gui,
                                   @Deserialize("ground") @Nullable DashTransformation ground,
                                   @Deserialize("fixed") @Nullable DashTransformation fixed
    ) {
        this.thirdPersonLeftHand = thirdPersonLeftHand;
        this.thirdPersonRightHand = thirdPersonRightHand;
        this.firstPersonLeftHand = firstPersonLeftHand;
        this.firstPersonRightHand = firstPersonRightHand;
        this.head = head;
        this.gui = gui;
        this.ground = ground;
        this.fixed = fixed;
    }

    public DashModelTransformation(ModelTransformation other) {
        this.thirdPersonLeftHand = other.thirdPersonLeftHand == Transformation.IDENTITY ? null : new DashTransformation(other.thirdPersonLeftHand);
        this.thirdPersonRightHand = other.thirdPersonRightHand == Transformation.IDENTITY ? null : new DashTransformation(other.thirdPersonRightHand);
        this.firstPersonLeftHand = other.firstPersonLeftHand == Transformation.IDENTITY ? null : new DashTransformation(other.firstPersonLeftHand);
        this.firstPersonRightHand = other.firstPersonRightHand == Transformation.IDENTITY ? null : new DashTransformation(other.firstPersonRightHand);
        this.head = other.head == Transformation.IDENTITY ? null : new DashTransformation(other.head);
        this.gui = other.gui == Transformation.IDENTITY ? null : new DashTransformation(other.gui);
        this.ground = other.ground == Transformation.IDENTITY ? null : new DashTransformation(other.ground);
        this.fixed = other.fixed == Transformation.IDENTITY ? null : new DashTransformation(other.fixed);
    }

    public ModelTransformation toUndash() {
        return new ModelTransformation(
                thirdPersonLeftHand == null ? Transformation.IDENTITY : thirdPersonLeftHand.toUndash(),
                thirdPersonRightHand == null ? Transformation.IDENTITY : thirdPersonRightHand.toUndash(),
                firstPersonLeftHand == null ? Transformation.IDENTITY : firstPersonLeftHand.toUndash(),
                firstPersonRightHand == null ? Transformation.IDENTITY : firstPersonRightHand.toUndash(),
                head == null ? Transformation.IDENTITY : head.toUndash(),
                gui == null ? Transformation.IDENTITY : gui.toUndash(),
                ground == null ? Transformation.IDENTITY : ground.toUndash(),
                fixed == null ? Transformation.IDENTITY : fixed.toUndash()
        );
    }
}
