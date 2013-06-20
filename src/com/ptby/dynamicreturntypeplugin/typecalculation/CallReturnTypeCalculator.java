package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.jetbrains.php.lang.psi.elements.FieldReference;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;

public class CallReturnTypeCalculator {

    private final ParameterTypeCalculator parameterTypeCalculator;


    public CallReturnTypeCalculator() {
        parameterTypeCalculator = new ParameterTypeCalculator( new ClassConstantAnalyzer() );

    }


    public String calculateTypeFromMethodParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof FieldReferenceImpl ) {
            return packageFieldReference( methodReference, parameterIndex );
        }

        if ( classReference instanceof VariableImpl ) {
            return packageVariableReference( methodReference, parameterIndex );

        }

        return parameterTypeCalculator.calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
    }


    private String packageFieldReference( MethodReferenceImpl methodReference, int parameterIndex ) {
        FieldReferenceImpl fieldReference = ( FieldReferenceImpl ) methodReference.getClassReference();

        String returnType = parameterTypeCalculator.calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );

        String intellijReference;
        if( methodReference.getSignature().matches( "#M#C(.*)" )){
            intellijReference = createLocalScopedFieldReference( methodReference );
        }else{
            intellijReference = fieldReference.getSignature();
        }

        String packagedFieldReference = FieldReferenceAnalyzer.packageForGetTypeResponse(
                intellijReference, methodReference.getName(), cleanReturnTypeOfPreviousCalls( returnType )
        );

        return packagedFieldReference;
    }


    private String createLocalScopedFieldReference( MethodReferenceImpl methodReference ) {
        FieldReference fieldReference = ( FieldReference ) methodReference.getClassReference();
        return fieldReference.getSignature();
    }



    private String packageVariableReference( MethodReferenceImpl methodReference, int parameterIndex ) {
        String returnType = parameterTypeCalculator.calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
        String name = methodReference.getName();
        String[] methodCallParts = methodReference.getSignature().split( "\\." );
        String packagedVariableReference = VariableAnalyser.packageForGetTypeResponse(
                methodCallParts[ 0 ], name, cleanReturnTypeOfPreviousCalls( returnType )
        );

        return packagedVariableReference;
    }


    public String calculateTypeFromFunctionParameter( FunctionReferenceImpl functionReference, int parameterIndex ) {
        String functionReturnType = parameterTypeCalculator.calculateTypeFromParameter(
                parameterIndex, functionReference.getParameters()
        );

        return cleanReturnTypeOfPreviousCalls( functionReturnType );
    }


    private String cleanReturnTypeOfPreviousCalls( String functionReturnType ) {
        if( functionReturnType == null ){
            return null;
        }
        String[] functionReturnTypeParts = functionReturnType.split( ":" );

        return functionReturnTypeParts[ functionReturnTypeParts.length - 1 ];
    }


}