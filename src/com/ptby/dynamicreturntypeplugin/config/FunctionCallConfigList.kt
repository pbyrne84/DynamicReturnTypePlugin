package com.ptby.dynamicreturntypeplugin.config

import java.util.ArrayList

public class FunctionCallConfigList(vararg functionCallConfigs: FunctionCallConfigKt) : ArrayList<FunctionCallConfigKt>() {
    {
        for (functionCallConfig in functionCallConfigs) {
            add(functionCallConfig)
        }
    }
}
