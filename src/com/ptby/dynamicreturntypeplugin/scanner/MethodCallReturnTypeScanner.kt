package com.ptby.dynamicreturntypeplugin.scanner

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator

public class MethodCallReturnTypeScanner(private val callReturnTypeCalculator: CallReturnTypeCalculator) {


    public fun getTypeFromMethodCall(classMethodConfigList: List<ClassMethodConfigKt>,
                                     methodReference: MethodReference): GetTypeResponse {

        for (classMethodConfig in classMethodConfigList) {
            if (classMethodConfig.equalsMethodReferenceName(methodReference)) {
                val getTypeResponse = callReturnTypeCalculator.calculateTypeFromMethodParameter(
                        methodReference,
                        classMethodConfig.parameterIndex
                )

                if (!getTypeResponse.isNull()) {
                    return getTypeResponse
                }
            }
        }

        return GetTypeResponse(null)
    }


}
