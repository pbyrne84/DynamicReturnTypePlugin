package com.ptby.dynamicreturntypeplugin.scripting;

public class ScriptSignatureParser {


    public ParsedSignature parseSignature( String currentParameterSignature ) {
        if ( currentParameterSignature.equals( "" ) ) {
            return null;
        }

        int prefixEndIndex = calculatePrefixEnd( currentParameterSignature );
        String prefix = "";
        String namespace = "";
        String returnClassName = currentParameterSignature;
        if ( prefixEndIndex != -1 ) {
            if ( prefixEndIndex != 0 ) {
                prefix = currentParameterSignature.substring( 0, prefixEndIndex );
            }
            int namesSpaceEndIndex = currentParameterSignature.lastIndexOf( "\\" );

            if ( namesSpaceEndIndex != -1 && prefixEndIndex < namesSpaceEndIndex ) {
                namespace = currentParameterSignature.substring( prefixEndIndex, namesSpaceEndIndex );
                returnClassName = currentParameterSignature.substring( namesSpaceEndIndex + 1 );
            }else {
                returnClassName = currentParameterSignature.substring( prefixEndIndex );
            }
        }

        return new ParsedSignature( prefix, namespace, returnClassName );

    }

    private int calculatePrefixEnd( String currentValue ) {
        if ( !currentValue.contains( "#" ) && currentValue.contains( "\\" ) ) {
            return 0;
        }

        return currentValue.indexOf( "\\" ) + 1;
    }
}
