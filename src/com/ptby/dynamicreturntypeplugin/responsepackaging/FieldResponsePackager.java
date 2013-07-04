package com.ptby.dynamicreturntypeplugin.responsepackaging;

import com.jetbrains.php.lang.psi.elements.FieldReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType;

public class FieldResponsePackager {

    public FieldResponsePackager() {
    }


    public GetTypeResponse packageFieldReference( MethodReference methodReference, ParameterType parameterType) {
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

        return new GetTypeResponse( packagedFieldReference );
    }

    private String createLocalScopedFieldReference( MethodReference methodReference ) {
        FieldReference fieldReference = ( FieldReference ) methodReference.getClassReference();
        return fieldReference.getSignature();
    }
}


