package com.ptby.dynamicreturntypeplugin.typecalculation;

public class ParameterType {
    private final String parameterType;


    public ParameterType( String parameterType ) {

        this.parameterType = parameterType;
    }


    public String toString() {
        return cleanReturnTypeOfPreviousCalls();
    }


    private String cleanReturnTypeOfPreviousCalls() {
        if ( parameterType == null ) {
            return null;
        }

        String[] returnTypeParts = parameterType.split( ":" );
        return returnTypeParts[ returnTypeParts.length - 1 ];
    }
}
