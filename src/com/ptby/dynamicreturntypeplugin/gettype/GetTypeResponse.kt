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

public class GetTypeResponse(private val response: String?, private val originalReference: FunctionReference?) {
    {
        if (response != null && response == "null") {
            throw RuntimeException("cannot be string null")

        }
    }


    public fun isNull(): Boolean {
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

        var parameters = ""
        var index = 0
        for ( parameter in originalReference.getParameters() ) {
            if ( parameter is PhpTypedElement ) {
                parameters += DynamicReturnTypeProvider.PARAMETER_SEPARATOR + parameterTypeCalculator.calculateTypeFromParameter(
                        originalReference,
                        index,
                        originalReference.getParameters()).toNullableString()
            }

            index++;
        }

        return parameters;
    }
}
