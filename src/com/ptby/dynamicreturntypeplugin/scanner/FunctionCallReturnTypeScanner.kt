package com.ptby.dynamicreturntypeplugin.scanner

import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator

public class FunctionCallReturnTypeScanner(private val callReturnTypeCalculator: CallReturnTypeCalculator) {


    public fun getTypeFromFunctionCall(functionCallConfigs: List<FunctionCallConfigKt>,
                                       functionReference: FunctionReferenceImpl): GetTypeResponse {
        for (functionCallConfig in functionCallConfigs) {
            if (functionCallConfig.equalsFunctionReference(functionReference)) {
                val getTypeResponse = callReturnTypeCalculator.calculateTypeFromFunctionParameter(
                        functionReference,
                        functionCallConfig.parameterIndex
                )

                if (!getTypeResponse.isNull()) {
                    val maskReplacedType = functionCallConfig.formatBeforeLookup(getTypeResponse.toString())
                    return GetTypeResponse(maskReplacedType, functionReference )
                }

                return getTypeResponse
            }
        }

        return GetTypeResponse(null, null )
    }
}
