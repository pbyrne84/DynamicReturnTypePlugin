package com.ptby.dynamicreturntypeplugin.gettype

import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator

open class FunctionReferenceGetTypeResponse protected constructor(private val isNull: Boolean,
                                                                  private val originalReference: FunctionReference?) : GetTypeResponse {

    companion object {
        fun createNull(): FunctionReferenceGetTypeResponse {
            return FunctionReferenceGetTypeResponse(true, null)
        }

        fun newMethod(originalReference: FunctionReference): FunctionReferenceGetTypeResponse {
            return FunctionReferenceGetTypeResponse(false, originalReference)
        }

        fun newFunction(originalReference: FunctionReference): FunctionReferenceGetTypeResponse {
            return FunctionGetTypeResponse(false, originalReference)
        }

    }


    override public fun isNull(): Boolean {
        return isNull
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
        var reference = originalReference?.signature as String
        return filterExcessReturnTypes(reference) + parameters
    }

    private fun filterExcessReturnTypes(originalReference: String): String {
        val multiAliasedMethodCalls = originalReference.split("\\|".toRegex())
        if ( multiAliasedMethodCalls.size == 1 ) {
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
        val parameterTypeCalculator = ParameterTypeCalculator()

        var parameters = DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR
        if ( originalReference.parameters.size == 0 ) {
            return parameters
        }


        var index = 0
        for ( parameter in originalReference.parameters) {
            if ( parameter is PhpTypedElement ) {
                parameters += parameterTypeCalculator.calculateTypeFromParameter(
                        index,
                        originalReference.parameters).toNullableString() + DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR
            }

            index++;
        }

        return parameters.removeSuffix(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR) + DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR;
    }
}


class FunctionGetTypeResponse(private val responseIsNull: Boolean,
                              private val originalReference: FunctionReference) :
        FunctionReferenceGetTypeResponse(responseIsNull, originalReference) {


    override fun toString(): String {
        val functionSig = super.toString()
        return functionSig;
    }
}

