package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.index.ClassAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator;

public class ClassResponsePackager {


    public String packageClassReference( MethodReferenceImpl methodReference,  ParameterType parameterType ) {
        ClassReference classReference = ( ClassReference ) methodReference.getClassReference();
        String returnType = parameterType.toString();
        if( returnType == null){
            return null;
        }

        if( returnType.indexOf( "#" ) == -1 ){
            if( returnType.indexOf( "\\" ) == -1 ){
                returnType = "\\" + returnType;
            }
            returnType = "#C" + returnType;
        }

        String response = ClassAnalyzer
                .packageForGetTypeResponse( classReference.getSignature(), methodReference.getName(), returnType );

        return response;

    }

}
