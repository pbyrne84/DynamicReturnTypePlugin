package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_processingv2.GetBySignature


public data class CustomMethodCallSignature private(public val className: String,
                                                    public val method: String,
                                                    public val desiredParameter: String,
                                                    public val rawStringSignature: String) {


    companion object {

        fun new(className: String, method: String, desiredParameter: String): CustomMethodCallSignature {
            return CustomMethodCallSignature(className,
                                             method,
                                             desiredParameter,
                                             className + ":" + method + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + desiredParameter)
        }
    }
}
