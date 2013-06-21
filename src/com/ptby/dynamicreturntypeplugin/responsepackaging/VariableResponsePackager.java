package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator;

public class VariableResponsePackager {

    private final ParameterTypeCalculator parameterTypeCalculator;


    public VariableResponsePackager() {
        parameterTypeCalculator = new ParameterTypeCalculator( new ClassConstantAnalyzer() );
    }



    public  String packageVariableReference( MethodReferenceImpl methodReference, int parameterIndex ) {
        String returnType = parameterTypeCalculator.calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
        String name = methodReference.getName();
        String[] methodCallParts = methodReference.getSignature().split( "\\." );
        String packagedVariableReference = VariableAnalyser.packageForGetTypeResponse(
                methodCallParts[ 0 ], name, cleanReturnTypeOfPreviousCalls( returnType )
        );

        return packagedVariableReference;
    }


    private String cleanReturnTypeOfPreviousCalls( String functionReturnType ) {
        if( functionReturnType == null ){
            return null;
        }

        String[] functionReturnTypeParts = functionReturnType.split( ":" );
        return functionReturnTypeParts[ functionReturnTypeParts.length - 1 ];
    }

}


