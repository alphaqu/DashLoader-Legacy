package net.quantumfusion.dashloader.api;

import org.jetbrains.annotations.Nullable;

public class ExtraVariables {
    @Nullable
    private Object extraVariable1;
    @Nullable
    private Object extraVariable2;
    @Nullable
    private Object extraVariable3;

    public ExtraVariables(@Nullable Object extraVariable1, @Nullable Object extraVariable2, @Nullable Object extraVariable3) {
        this.extraVariable1 = extraVariable1;
        this.extraVariable2 = extraVariable2;
        this.extraVariable3 = extraVariable3;
    }

    public ExtraVariables(@Nullable Object extraVariable1, @Nullable Object extraVariable2) {
        this.extraVariable1 = extraVariable1;
        this.extraVariable2 = extraVariable2;
    }

    public ExtraVariables(@Nullable Object extraVariable1) {
        this.extraVariable1 = extraVariable1;
    }


    public Object getExtraVariable1() {
        return extraVariable1;
    }

    public Object getExtraVariable2() {
        return extraVariable2;
    }

    public Object getExtraVariable3() {
        return extraVariable3;
    }
}
