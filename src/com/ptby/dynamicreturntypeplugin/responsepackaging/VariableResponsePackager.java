package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;

public class VariableResponsePackager {

    public VariableResponsePackager() {
    }


    public GetTypeResponse packageVariableReference( MethodReference methodReference, ParameterType parameterType ) {
        String name = methodReference.getName();
        String[] methodCallParts = methodReference.getSignature().split( "\\." );
        StringBuilder intellijReference = new StringBuilder();
        for ( int i = 0; i < methodCallParts.length - 1; i++ ) {
            if ( i > 0 ) {
                intellijReference.append( "." );
            }
            intellijReference.append( methodCallParts[ i ] );
        }

        String packagedVariableReference = VariableAnalyser.packageForGetTypeResponse(
                intellijReference.toString(), name, parameterType.toString()
        );

        return new GetTypeResponse( packagedVariableReference );
    }

}


