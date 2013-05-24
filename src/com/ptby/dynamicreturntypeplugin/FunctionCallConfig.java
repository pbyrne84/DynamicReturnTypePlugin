package com.ptby.dynamicreturntypeplugin;

public class FunctionCallConfig {

    private final String functionName;
    private final int parameterIndex;


    public FunctionCallConfig( String functionName, int parameterIndex ) {
        this.functionName = functionName;
        this.parameterIndex = parameterIndex;
    }


    public String getFunctionName() {
        return functionName;
    }


    public int getParameterIndex() {
        return parameterIndex;
    }
}
