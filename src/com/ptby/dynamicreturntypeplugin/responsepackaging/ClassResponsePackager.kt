package com.ptby.dynamicreturntypeplugin.responsepackaging

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType

public class ClassResponsePackager {

    public fun packageClassReference(methodReference: MethodReference, parameterType: ParameterType): GetTypeResponse {
        val classReference = methodReference.getClassReference() as PhpReference?
        val returnType = parameterType.toNullableString()
        if (returnType == null || classReference == null) {
            return GetTypeResponse(null)
        }

        val signature = "#M" + classReference.getSignature()
        val response = signature + ":" + methodReference.getName() + ":" + returnType
        return GetTypeResponse(response)
    }
}
