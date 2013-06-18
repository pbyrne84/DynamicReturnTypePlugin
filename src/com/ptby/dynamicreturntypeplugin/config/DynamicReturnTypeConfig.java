package com.ptby.dynamicreturntypeplugin.config;


import java.util.ArrayList;
import java.util.List;

public class DynamicReturnTypeConfig {
    private final List<ClassMethodConfig> classMethodConfigs;
    private final List<FunctionCallConfig> functionCallConfigs;


    public DynamicReturnTypeConfig( List<ClassMethodConfig> classMethodConfigs, List<FunctionCallConfig> functionCallConfigs ) {
        this.classMethodConfigs = classMethodConfigs;
        this.functionCallConfigs = functionCallConfigs;
    }

    public DynamicReturnTypeConfig( ) {
        this( new ArrayList<ClassMethodConfig>(), new ArrayList<FunctionCallConfig>() );
    }




    public List<ClassMethodConfig> getClassMethodConfigs() {
        return classMethodConfigs;
    }


    public List<FunctionCallConfig> getFunctionCallConfigs() {
        return functionCallConfigs;
    }


    @Override
    public String toString() {
        return "DynamicReturnTypeConfig{" +
                "\nclassMethodConfigs=" + classMethodConfigs +
                "\n, functionCallConfigs=" + functionCallConfigs +
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

        DynamicReturnTypeConfig that = ( DynamicReturnTypeConfig ) o;

        if ( !classMethodConfigs.equals( that.classMethodConfigs ) ) {
            return false;
        }
        if ( !functionCallConfigs.equals( that.functionCallConfigs ) ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = classMethodConfigs.hashCode();
        result = 31 * result + functionCallConfigs.hashCode();
        return result;
    }
}
