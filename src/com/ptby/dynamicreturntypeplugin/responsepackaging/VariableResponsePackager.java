package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;

public class VariableResponsePackager {

    public VariableResponsePackager() {
    }



    public  String packageVariableReference( MethodReferenceImpl methodReference,  ParameterType parameterType ) {
        String name = methodReference.getName();
        String[] methodCallParts = methodReference.getSignature().split( "\\." );
        String packagedVariableReference = VariableAnalyser.packageForGetTypeResponse(
                methodCallParts[ 0 ], name,  parameterType.toString()
        );

        return packagedVariableReference;
    }

}


