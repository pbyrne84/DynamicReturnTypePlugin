package com.ptby.dynamicreturntypeplugin.gettype

import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterListOwner
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer

open class GetTypeResponse protected (private val response: String?,
                                      private val originalReference: FunctionReference?) {
    {
        if (response != null && response == "null") {
            throw RuntimeException("cannot be string null")

        }
    }


    class object {
        fun createNull(): GetTypeResponse {
            return GetTypeResponse(null, null)
        }

        fun newMethod(originalReference: FunctionReference): GetTypeResponse {
            return GetTypeResponse("not null", originalReference)
        }

        fun newFunction(originalReference: FunctionReference): GetTypeResponse {
            return FunctionGetTypeResponse("not null", originalReference)
        }
    }


    open public fun isNull(): Boolean {
        return response == null
    }


    override fun toString(): String {
        if ( isNull() ) {
            return ""
        }

        var parameters = ""
        if ( originalReference != null ) {
            parameters = convertParameters(originalReference)
        }


        return originalReference?.getSignature() as String + parameters
    }


    private fun convertParameters(originalReference: FunctionReference): String {
        val parameterTypeCalculator = ParameterTypeCalculator(ClassConstantAnalyzer())

        var parameters = DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR
        if ( originalReference.getParameters().size() == 0 ) {
            return parameters
        }


        var index = 0
        for ( parameter in originalReference.getParameters() ) {
            if ( parameter is PhpTypedElement ) {
                parameters += parameterTypeCalculator.calculateTypeFromParameter(
                        originalReference,
                        index,
                        originalReference.getParameters()).toNullableString() + DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR
            }

            index++;
        }

        return parameters.trimTrailing(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR) + DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR;
    }
}


class FunctionGetTypeResponse (private val returnType: String?,
                               private val originalReference: FunctionReference) : GetTypeResponse(returnType,
                                                                                                   originalReference) {
    override fun isNull(): Boolean {
        return returnType == null
    }

    override fun toString(): String {
        val functionSig = super.toString()
        println("functionSig " + functionSig)
        return functionSig;
    }
}

