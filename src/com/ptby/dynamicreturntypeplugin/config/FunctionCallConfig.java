package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.FunctionReference;

public class FunctionCallConfig {

    private final String functionName;
    private final int parameterIndex;


    public FunctionCallConfig( String functionName, int parameterIndex ) {
        this.functionName = functionName.toLowerCase();
        this.parameterIndex = parameterIndex;
    }


    private String getFunctionName() {
        return functionName;
    }


    public int getParameterIndex() {
        return parameterIndex;
    }


    public boolean equalsFunctionReference( FunctionReference functionReference ) {
        String lowerCaseFullFunctionName
                = ( functionReference.getNamespaceName() + functionReference.getName() ).toLowerCase();

        boolean isValid = getFunctionName().equals( lowerCaseFullFunctionName ) ||
                validateAgainstPossibleGlobalFunction( functionReference );
        return isValid;
    }


    private boolean validateAgainstPossibleGlobalFunction( FunctionReference functionReference ) {
        String functionReferenceText = functionReference.getText();
        return functionReferenceText.trim().indexOf( "\\" ) != 0 &&
                ( "\\" + functionReference.getName() ).toLowerCase().equals( getFunctionName() );
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
