package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;

public class VariableResponsePackager {

    public VariableResponsePackager() {
    }



    public GetTypeResponse packageVariableReference( MethodReference methodReference,  ParameterType parameterType ) {
        String name = methodReference.getName();
        String[] methodCallParts = methodReference.getSignature().split( "\\." );
        String packagedVariableReference = VariableAnalyser.packageForGetTypeResponse(
                methodCallParts[ 0 ], name,  parameterType.toString()
        );

        return new GetTypeResponse( packagedVariableReference );
    }

}


