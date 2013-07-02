package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.FieldReference;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator;

public class FieldResponsePackager {

    public FieldResponsePackager() {
    }


    public String packageFieldReference( MethodReferenceImpl methodReference, ParameterType parameterType) {
        FieldReferenceImpl fieldReference = ( FieldReferenceImpl ) methodReference.getClassReference();

        String intellijReference;
        if( methodReference.getSignature().matches( "#M#C(.*)" )){
            intellijReference = createLocalScopedFieldReference( methodReference );
        }else{
            intellijReference = fieldReference.getSignature();
        }

        String packagedFieldReference = FieldReferenceAnalyzer.packageForGetTypeResponse(
                intellijReference, methodReference.getName(), parameterType.toString()
        );

        return packagedFieldReference;
    }

    private String createLocalScopedFieldReference( MethodReferenceImpl methodReference ) {
        FieldReference fieldReference = ( FieldReference ) methodReference.getClassReference();
        return fieldReference.getSignature();
    }
}


