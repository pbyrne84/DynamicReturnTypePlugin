package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider


public data class CustomMethodCallSignature private(public val className: String,
                                                    public val method: String,
                                                    public val parameter: String,
                                                    public val rawStringSignature: String) {


    class object {

        fun new(className: String, method: String, parameter: String): CustomMethodCallSignature {
            return CustomMethodCallSignature(className,
                                             method,
                                             parameter,
                                             className + ":" + method + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + parameter)
        }


        public fun createFromString(signature: String?): CustomMethodCallSignature? {
            if (signature == null) {
                return null

            }
            var signatureWithParameterSeparated = signature as String
            var parameter = ""

            if( signatureWithParameterSeparated.contains( DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) ) {
                val indexOfParameterSignature = signatureWithParameterSeparated.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
                parameter = signatureWithParameterSeparated.substring(
                        indexOfParameterSignature + 1
                )
                signatureWithParameterSeparated = signatureWithParameterSeparated.substring(
                        0, indexOfParameterSignature
                )
            }

            val signatureParts = signatureWithParameterSeparated.split(":")
            if (signatureParts.size() < 2) {
                return null
            }


            return CustomMethodCallSignature(signatureParts[0], signatureParts[1], parameter, signature)
        }
    }


}
