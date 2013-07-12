package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.FunctionReference;
import org.apache.commons.lang.StringUtils;

public class FunctionCallConfig implements StringClassMaskConfig {

    private final String functionName;
    private final int parameterIndex;
    private final String stringClassNameMask;
    private final boolean hasValidClassNameMask;


    public FunctionCallConfig( String functionName, int parameterIndex, String stringClassNameMask ) {
        this.functionName = functionName.toLowerCase();
        this.parameterIndex = parameterIndex;
        this.stringClassNameMask = stringClassNameMask;
        this.hasValidClassNameMask
                = !stringClassNameMask.equals( "" )
                && StringUtils.countMatches( stringClassNameMask, "%" ) == 1;
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


    @Override
    public String getStringClassNameMask() {
        return stringClassNameMask;
    }


    @Override
    public boolean hasValidStringClassNameMask() {
        return hasValidClassNameMask;
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


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        FunctionCallConfig that = ( FunctionCallConfig ) o;

        if ( hasValidClassNameMask != that.hasValidClassNameMask ) {
            return false;
        }
        if ( parameterIndex != that.parameterIndex ) {
            return false;
        }
        if ( !functionName.equals( that.functionName ) ) {
            return false;
        }
        if ( !stringClassNameMask.equals( that.stringClassNameMask ) ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = functionName.hashCode();
        result = 31 * result + parameterIndex;
        result = 31 * result + stringClassNameMask.hashCode();
        result = 31 * result + ( hasValidClassNameMask ? 1 : 0 );
        return result;
    }


    @Override
    public String toString() {
        return "FunctionCallConfig{" +
                "\nfunctionName='" + functionName + '\'' +
                "\n, parameterIndex=" + parameterIndex +
                "\n, stringClassNameMask='" + stringClassNameMask + '\'' +
                "\n, hasValidStringClassNameMask=" + hasValidClassNameMask +
                '}';
    }


}
