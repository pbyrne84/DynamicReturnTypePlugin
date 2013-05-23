package com.ptby.dynamicreturntypeplugin;

public class ClassMethodConfig {

    private final String fqnClassName;
    private final String methodName;
    private final int parameterIndex;


    public ClassMethodConfig( String fqnClassName, String methodName, int parameterIndex ) {
        this.fqnClassName = fqnClassName;
        this.methodName = methodName;
        this.parameterIndex = parameterIndex;
    }


    public String getFqnClassName() {
        return fqnClassName;
    }


    public String getMethodName() {
        return methodName;
    }


    public int getParameterIndex() {
        return parameterIndex;
    }
}
