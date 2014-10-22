package com.ptby.dynamicreturntypeplugin.responsepackaging

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterType

public class VariableResponsePackager {


    public fun packageVariableReference(methodReference: MethodReference,
                                        parameterType: ParameterType): GetTypeResponse {
        val name = methodReference.getName()
        val methodCallParts = methodReference.getSignature().split("\\.")
        val intellijReference = StringBuilder()
        for (i in 0..methodCallParts.size - 1 - 1) {
            if (i > 0) {
                intellijReference.append(".")
            }
            intellijReference.append(methodCallParts[i])
        }

        val packagedVariableReference = VariableAnalyser.packageForGetTypeResponse(
                intellijReference.toString(),
                name,
                parameterType.toNullableString()
        )

        return GetTypeResponse(packagedVariableReference)
    }

}


