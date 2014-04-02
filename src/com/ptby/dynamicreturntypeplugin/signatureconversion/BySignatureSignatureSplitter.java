package com.ptby.dynamicreturntypeplugin.signatureconversion;

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BySignatureSignatureSplitter {

    public List<String> createChainedSignatureList( String signature ) {
        List<String> chainedSignatureList = new StringList();
        if ( signature.equals( "" ) ) {
            return chainedSignatureList;
        }

        int chainedSignatureCount = StringUtils
                .countMatches( signature, DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING ) + 1;

        if ( chainedSignatureCount < 2 ) {
            chainedSignatureList.add( signature );
            return chainedSignatureList;
        }

        int beginIndex = signature.lastIndexOf( DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING );
        String cleanedSignature = signature.substring( beginIndex + 1 );

        int currentStringPos = 0;
        int currentOrdinalIncrement = 3;
        for ( int i = 0; i < chainedSignatureCount; i++ ) {
            int nextSignatureStart = StringUtils.ordinalIndexOf( cleanedSignature, ":", currentOrdinalIncrement );
            String subSignature = getSubSignature( cleanedSignature, currentStringPos, nextSignatureStart );
            chainedSignatureList.add( subSignature );
            if ( nextSignatureStart == -1 ) {
                return chainedSignatureList;
            }

            currentStringPos = nextSignatureStart;
            currentOrdinalIncrement = currentOrdinalIncrement + 2;
        }

        return chainedSignatureList;
    }


    private String getSubSignature( String cleanedSignature, int currentStringPos, int nextSignatureStart ) {
        if ( nextSignatureStart != -1 ) {
            return cleanedSignature.substring( currentStringPos, nextSignatureStart );
        }

        return cleanedSignature.substring( currentStringPos );
    }

}
