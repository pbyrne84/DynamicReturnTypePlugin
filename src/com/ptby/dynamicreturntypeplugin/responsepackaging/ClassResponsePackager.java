package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.index.ClassAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator;

public class ClassResponsePackager {


    public GetTypeResponse packageClassReference( MethodReferenceImpl methodReference,  ParameterType parameterType ) {
        ClassReference classReference = ( ClassReference ) methodReference.getClassReference();
        String returnType = parameterType.getClassReferenceString();
        if( returnType == null){
            return new GetTypeResponse( null );
        }

        String response = ClassAnalyzer
                .packageForGetTypeResponse( classReference.getSignature(), methodReference.getName(), returnType );

        return new GetTypeResponse( response );

    }

}
