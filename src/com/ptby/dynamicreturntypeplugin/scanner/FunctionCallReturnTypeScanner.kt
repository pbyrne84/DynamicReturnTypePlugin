package com.ptby.dynamicreturntypeplugin.scanner

import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer

public class FunctionCallReturnTypeScanner() {
    private val parameterTypeCalculator: ParameterTypeCalculator

    {
        parameterTypeCalculator = ParameterTypeCalculator(ClassConstantAnalyzer())
    }

    public fun getTypeFromFunctionCall(functionCallConfigs: List<FunctionCallConfigKt>,
                                       functionReference: FunctionReferenceImpl): GetTypeResponse {
        for (functionCallConfig in functionCallConfigs) {
            if (functionCallConfig.equalsFunctionReference(functionReference)) {
                val getTypeResponse = calculateTypeFromFunctionParameter(
                        functionReference,
                        functionCallConfig.parameterIndex
                )

                if (!getTypeResponse.isNull()) {
                    val maskReplacedType = functionCallConfig.formatBeforeLookup(getTypeResponse.toString())
                    return GetTypeResponse.function( maskReplacedType, functionReference )
                }

                return getTypeResponse
            }
        }

        return GetTypeResponse.createNull()
    }

    private fun calculateTypeFromFunctionParameter(functionReference: FunctionReference, parameterIndex: Int): GetTypeResponse {
        val functionReturnType = parameterTypeCalculator.calculateTypeFromParameter(functionReference, parameterIndex, functionReference.getParameters())

        return GetTypeResponse.function(functionReturnType.toNullableString(),functionReference )
    }

}
