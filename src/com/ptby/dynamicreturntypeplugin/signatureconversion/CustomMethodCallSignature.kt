package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_processingv2.GetBySignature


public data class CustomMethodCallSignature private(public val className: String,
                                                    public val method: String,
                                                    public val parameter: Array<String>,
                                                    public val rawStringSignature: String) {


    class object {

        fun new(className: String, method: String, parameters: Array<String>): CustomMethodCallSignature {
            return CustomMethodCallSignature(className,
                                             method,
                                             parameters,
                                             className + ":" + method + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + parameters)
        }
    }
}
