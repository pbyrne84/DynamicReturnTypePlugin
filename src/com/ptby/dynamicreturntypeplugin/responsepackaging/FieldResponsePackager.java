package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.FieldReference;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator;

public class FieldResponsePackager {


    private final ParameterTypeCalculator parameterTypeCalculator;


    public FieldResponsePackager() {
        parameterTypeCalculator = new ParameterTypeCalculator( new ClassConstantAnalyzer() );
    }


    public String packageFieldReference( MethodReferenceImpl methodReference, int parameterIndex ) {
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

    private String cleanReturnTypeOfPreviousCalls( String functionReturnType ) {
        if( functionReturnType == null ){
            return null;
        }

        String[] functionReturnTypeParts = functionReturnType.split( ":" );
        return functionReturnTypeParts[ functionReturnTypeParts.length - 1 ];
    }


}


