package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.FunctionReference;

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


    public boolean equalsFunctionReference( FunctionReference functionReference ) {
        String fullFunctionName = ( functionReference.getNamespaceName() + functionReference.getName() );
        return getFunctionName().equals( fullFunctionName ) ||
                validateAgainstPossibleGlobalFunction( functionReference );
    }


    private boolean validateAgainstPossibleGlobalFunction( FunctionReference functionReference ) {
        String text = functionReference.getText();
        return !text.contains( "\\" ) &&
                getFunctionName().lastIndexOf( "\\" ) != -1 &&
                ( "\\" + functionReference.getName() ).equals( getFunctionName() );
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
