package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;

public class ClassResponsePackager {

    public GetTypeResponse packageClassReference( MethodReference methodReference, ParameterType parameterType ) {
        PhpReference classReference = ( PhpReference ) methodReference.getClassReference();
        String returnType = parameterType.toString();
        if ( returnType == null || classReference == null) {
            return new GetTypeResponse( null );
        }

        String signature = "#M" + classReference.getSignature();
        String response = signature + ":" + methodReference.getName() + ":" + returnType;
        return new GetTypeResponse( response );
    }
}
