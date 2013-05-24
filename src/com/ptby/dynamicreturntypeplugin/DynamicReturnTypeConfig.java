package com.ptby.dynamicreturntypeplugin;

import java.util.List;

public class DynamicReturnTypeConfig {
    private final List<ClassMethodConfig> classMethodConfigs;
    private final List<FunctionCallConfig> functionCallConfigs;


    public DynamicReturnTypeConfig( List<ClassMethodConfig> classMethodConfigs, List<FunctionCallConfig> functionCallConfigs ) {
        this.classMethodConfigs = classMethodConfigs;
        this.functionCallConfigs = functionCallConfigs;
    }


    public List<ClassMethodConfig> getClassMethodConfigs() {
        return classMethodConfigs;
    }


    public List<FunctionCallConfig> getFunctionCallConfigs() {
        return functionCallConfigs;
    }
}
