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

open class FunctionReferenceGetTypeResponse protected constructor(private val response: String?,
                                                                  private val originalReference: FunctionReference?) : GetTypeResponse {
    init {
        if (response != null && response == "null") {
            throw RuntimeException("cannot be string null")

        }
    }


    companion object {
        fun createNull(): FunctionReferenceGetTypeResponse {
            return FunctionReferenceGetTypeResponse(null, null)
        }

        fun newMethod(originalReference: FunctionReference): FunctionReferenceGetTypeResponse {
            return FunctionReferenceGetTypeResponse("not null", originalReference)
        }

        fun newFunction(originalReference: FunctionReference): FunctionReferenceGetTypeResponse {
            return FunctionGetTypeResponse("not null", originalReference)
        }

    }


    override public fun isNull(): Boolean {
        return response == null
    }

    override fun toString(): String {
        return getSignature()
    }

    override fun getSignature(): String {
        if ( isNull() ) {
            return ""
        }

        var parameters = ""
        if ( originalReference != null ) {
            parameters = convertParameters(originalReference)
        }

        //
        //#M#C\DynamicReturnTypePluginTestEnvironment\OverriddenReturnType\PhockitoChild.mock|#M#C\object.mock�Bouh?
        //signature #M#C\DynamicReturnTypePluginTestEnvironment\OverriddenReturnType\PhockitoChild.mock�Bouh?
        var reference = originalReference?.getSignature() as String
        return filterExcessReturnTypes(reference) + parameters
    }

    private fun filterExcessReturnTypes(originalReference: String): String {
        val multiAliasedMethodCalls = originalReference.split("\\|".toRegex())
        if ( multiAliasedMethodCalls.size() == 1 ) {
            return originalReference;
        }

        for ( multiAliasedMethodCall in  multiAliasedMethodCalls ) {
            if ( multiAliasedMethodCall.indexOf("#M#C\\object") !== 0 ) {
                return multiAliasedMethodCall;
            }
        }

        return originalReference
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
                        index,
                        originalReference.getParameters()).toNullableString() + DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR
            }

            index++;
        }

        return parameters.removeSuffix(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR) + DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR;
    }
}


class FunctionGetTypeResponse(private val returnType: String?,
                              private val originalReference: FunctionReference) : FunctionReferenceGetTypeResponse(
        returnType,
        originalReference) {

    override fun isNull(): Boolean {
        return returnType == null
    }

    override fun toString(): String {
        val functionSig = super.toString()
        return functionSig;
    }
}

