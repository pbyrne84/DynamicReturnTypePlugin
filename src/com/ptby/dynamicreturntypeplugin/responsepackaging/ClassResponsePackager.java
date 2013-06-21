package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.index.ClassAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator;

public class ClassResponsePackager {
    private final ParameterTypeCalculator parameterTypeCalculator;


    public ClassResponsePackager() {
        parameterTypeCalculator = new ParameterTypeCalculator( new ClassConstantAnalyzer() );
    }

    public String packageClassReference( MethodReferenceImpl methodReference, int parameterIndex ) {
        ClassReference classReference = ( ClassReference ) methodReference.getClassReference();
        String returnType = parameterTypeCalculator.calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
        if( returnType == null){
            return null;
        }

        returnType = cleanReturnTypeOfPreviousCalls( returnType );
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

    private String cleanReturnTypeOfPreviousCalls( String functionReturnType ) {
        String[] functionReturnTypeParts = functionReturnType.split( ":" );
        return functionReturnTypeParts[ functionReturnTypeParts.length - 1 ];
    }
}
