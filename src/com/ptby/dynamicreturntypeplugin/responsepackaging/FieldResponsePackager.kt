package com.ptby.dynamicreturntypeplugin.responsepackaging

import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType

public class FieldResponsePackager {


    public fun packageFieldReference(methodReference: MethodReference, parameterType: ParameterType): GetTypeResponse {

        val intellijReference: String
        if (methodReference.getSignature().matches("#M#C(.*)")) {
            intellijReference = createLocalScopedFieldReference(methodReference)
        } else {
            val fieldReference = methodReference.getClassReference() as FieldReferenceImpl
            intellijReference = fieldReference.getSignature()
        }

        val packagedFieldReference = FieldReferenceAnalyzer.packageForGetTypeResponse(
                intellijReference,
                methodReference.getName(),
                parameterType.toString()
        )

        return GetTypeResponse(packagedFieldReference)
    }

    private fun createLocalScopedFieldReference(methodReference: MethodReference): String {
        val fieldReference = methodReference.getClassReference() as FieldReference
        return fieldReference.getSignature()
    }
}


