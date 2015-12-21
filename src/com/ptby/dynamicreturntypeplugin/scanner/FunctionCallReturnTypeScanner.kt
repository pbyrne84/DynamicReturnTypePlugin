package com.ptby.dynamicreturntypeplugin.scanner

import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt
import com.ptby.dynamicreturntypeplugin.gettype.FunctionReferenceGetTypeResponse
import com.ptby.dynamicreturntypeplugin.typecalculation.ParameterTypeCalculator

public class FunctionCallReturnTypeScanner() {
    private val parameterTypeCalculator: ParameterTypeCalculator

    init {
        parameterTypeCalculator = ParameterTypeCalculator()
    }

    public fun getTypeFromFunctionCall(functionCallConfigs: List<FunctionCallConfigKt>,
                                       functionReference: FunctionReference): FunctionReferenceGetTypeResponse {
        for (functionCallConfig in functionCallConfigs) {
            if (functionCallConfig.equalsFunctionReference(functionReference)) {
                return FunctionReferenceGetTypeResponse.newFunction(functionReference)
            }
        }

        return FunctionReferenceGetTypeResponse.createNull()
    }

}
