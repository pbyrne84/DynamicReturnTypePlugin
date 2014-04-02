package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy;
import org.apache.commons.lang.StringUtils;

public class FunctionCallConfig {

    private final String functionName;
    private final int parameterIndex;
    private final ValueReplacementStrategy valueReplacementStrategy;


    public FunctionCallConfig( String functionName,
                               int parameterIndex,
                               ValueReplacementStrategy valueReplacementStrategy ) {
        this.valueReplacementStrategy = valueReplacementStrategy;
        this.functionName = functionName.toLowerCase();
        this.parameterIndex = parameterIndex;
    }


    public boolean isValid() {
        return !functionName.equals( "" ) && parameterIndex != -1;
    }


    private String getFunctionName() {
        return functionName;
    }


    public int getParameterIndex() {
        return parameterIndex;
    }


    public String formatBeforeLookup( String passedType ) {
        return valueReplacementStrategy.replaceCalculatedValue( passedType );
    }


    @Override
    public String toString() {
        return "FunctionCallConfig{" +
                "\nfunctionName='" + functionName + '\'' +
                "\n, parameterIndex=" + parameterIndex +
                "\n, valueReplacementStrategy=" + valueReplacementStrategy +
                '}';
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof FunctionCallConfig ) ) {
            return false;
        }

        FunctionCallConfig that = ( FunctionCallConfig ) o;

        if ( parameterIndex != that.parameterIndex ) {
            return false;
        }
        if ( functionName != null ? !functionName.equals( that.functionName ) : that.functionName != null ) {
            return false;
        }
        if ( valueReplacementStrategy != null ? !valueReplacementStrategy
                .equals( that.valueReplacementStrategy ) : that.valueReplacementStrategy != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = functionName != null ? functionName.hashCode() : 0;
        result = 31 * result + parameterIndex;
        result = 31 * result + ( valueReplacementStrategy != null ? valueReplacementStrategy.hashCode() : 0 );
        return result;
    }


    public boolean equalsFunctionReference( FunctionReference functionReference ) {
        String lowerCaseFullFunctionName
                = ( functionReference.getNamespaceName() + functionReference.getName() ).toLowerCase();

        return getFunctionName().equals( lowerCaseFullFunctionName ) ||
                validateAgainstPossibleGlobalFunction( functionReference );
    }


    private boolean validateAgainstPossibleGlobalFunction( FunctionReference functionReference ) {
        String functionReferenceText = functionReference.getText();
        return functionReferenceText.trim().indexOf( "\\" ) != 0 &&
                ( "\\" + functionReference.getName() ).toLowerCase().equals( getFunctionName() );
    }


}
