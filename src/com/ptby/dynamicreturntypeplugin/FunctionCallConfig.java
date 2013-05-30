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


    @Override
    public String toString() {
        return "FunctionCallConfig{" +
                "\nfunctionName='" + functionName + '\'' +
                "\n, parameterIndex=" + parameterIndex +
                '}';
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        FunctionCallConfig that = ( FunctionCallConfig ) o;

        if ( parameterIndex != that.parameterIndex ) {
            return false;
        }
        if ( !functionName.equals( that.functionName ) ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = functionName.hashCode();
        result = 31 * result + parameterIndex;
        return result;
    }
}
