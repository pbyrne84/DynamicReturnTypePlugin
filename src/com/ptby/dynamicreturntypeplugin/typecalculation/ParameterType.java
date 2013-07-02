package com.ptby.dynamicreturntypeplugin.typecalculation;

public class ParameterType {
    private final String parameterType;


    public ParameterType( String parameterType ) {
        this.parameterType = parameterType;
    }

    public String getClassReferenceString(){
        if( this.parameterType == null ){
            return null;

        }

        String returnType = parameterType;
        if( returnType.indexOf( "#" ) == -1 ){
            if( returnType.indexOf( "\\" ) == -1 ){
                returnType = "\\" + returnType;
            }
            returnType = "#C" + returnType;
        }

        return returnType;
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
