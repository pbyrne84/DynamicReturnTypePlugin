package com.ptby.dynamicreturntypeplugin.signatureconversion


public data class CustomMethodCallSignature private(public val className: String,
                                                    public val method: String,
                                                    public val parameter: String,
                                                    public val rawStringSignature: String) {


    class object {

        fun new(className: String, method: String, parameter: String): CustomMethodCallSignature {
            return CustomMethodCallSignature(className, method, parameter, className + ":" + method + ":" + parameter)
        }


        public fun createFromString(signature: String?): CustomMethodCallSignature? {
            if (signature == null) {
                return null
            }

            val signatureParts = signature.split(":")
            if (signatureParts.size < 2) {
                return null
            }

            var parameter = ""
            if (signatureParts.size > 2) {
                parameter = signatureParts[signatureParts.size - 1]
            }

            return CustomMethodCallSignature(signatureParts[0], signatureParts[1], parameter, signature)
        }
    }
}
