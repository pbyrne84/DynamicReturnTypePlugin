package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider


data class CustomMethodCallSignature private constructor(val className: String,
                                                                val method: String,
                                                                val desiredParameter: String,
                                                                val rawStringSignature: String) {


    companion object {

        fun new(className: String, method: String, desiredParameter: String): CustomMethodCallSignature {
            return CustomMethodCallSignature(className,
                                             method,
                                             desiredParameter,
                                             className + ":" + method + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + desiredParameter)
        }
    }
}
