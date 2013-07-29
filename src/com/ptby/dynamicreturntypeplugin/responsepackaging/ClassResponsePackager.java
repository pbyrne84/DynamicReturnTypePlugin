package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;

public class ClassResponsePackager {

    public GetTypeResponse packageClassReference( MethodReference methodReference, ParameterType parameterType ) {
        ClassReference classReference = ( ClassReference ) methodReference.getClassReference();
        String returnType = parameterType.toString();
        if ( returnType == null ) {
            return new GetTypeResponse( null );
        }

        String signature = "#M" + classReference.getSignature();
        String response = signature + ":" + methodReference.getName() + ":" + returnType;
        return new GetTypeResponse( response );
    }
}
