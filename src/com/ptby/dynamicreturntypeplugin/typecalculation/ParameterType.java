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
        String returnTypePart = returnTypeParts[ returnTypeParts.length - 1 ];
        if ( returnTypePart.length() > 2 && returnTypePart.substring( 0, 2 ).equals( "#C" ) ) {
            return returnTypePart.substring( 2 );
        }

        return returnTypePart;
    }
}
