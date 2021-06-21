package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.Shader;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.image.shader.DashShader;
import net.quantumfusion.dashloader.util.DashHelper;
import net.quantumfusion.dashloader.util.TaskHandler;

import java.io.IOException;
import java.util.Map;

public class DashShaderData {
    @Serialize(order = 0)
    public Map<String, DashShader> shaders;


    public DashShaderData(@Deserialize("shaders") Map<String, DashShader> shaders) {
        this.shaders = shaders;
    }

    public DashShaderData(VanillaData data, TaskHandler taskHandler) {
        taskHandler.setSubtasks(1);
        this.shaders = DashHelper.convertMapValues(data.getShaderData(), DashShader::new);
        taskHandler.completedSubTask();
    }

    public Map<String, Shader> toUndash() {
        return DashHelper.convertMapValues(shaders, shader -> {
            try {
                return shader.toUndash();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
