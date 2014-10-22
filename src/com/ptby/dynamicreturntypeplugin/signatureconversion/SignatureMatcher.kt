package com.ptby.dynamicreturntypeplugin.signatureconversion

public class SignatureMatcher {


    public fun verifySignatureIsClassConstantFunctionCall(signature: String): Boolean {
        return signature.matches(CLASS_CONSTANT_CALL_PATTERN)
    }


    public fun verifySignatureIsClassConstantFunctionCall(signature: CustomMethodCallSignature): Boolean {
        return verifySignatureIsClassConstantFunctionCall(signature.rawStringSignature)
    }


    public fun verifySignatureIsFieldCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(FIELD_CALL_PATTERN)
    }


    public fun verifySignatureIsMethodCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(METHOD_CALL_PATTERN)
    }


    /**
     * deferred in the sense that there is no \, this causes signatures to change and have to be further processed
     */
    public fun verifySignatureIsDeferredGlobalFunctionCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN)
    }

    /**
     * It seems the signature #M#M#C is used when there a variable is declared from a return and a method is then
     * called on it
     */
    public fun verifySignatureIsFromReturnInitialiasedLocalObject(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(RETURN_INITIALISED_LOCAL_AND_STATIC_METHOD_CALL_PATTERN)

    }

    class object {
        private val CLASS_CONSTANT_CALL_PATTERN = "(#*)K#C(.*)\\.(.*)"
        private val FIELD_CALL_PATTERN = "(#P#C.*):(.*):(.*)"
        private val RETURN_INITIALISED_LOCAL_AND_STATIC_METHOD_CALL_PATTERN = "(((#M)+)#M#C.*):(.*):(.*)"
        private val METHOD_CALL_PATTERN = "(#M#C.*):(.*):(.*)"
        private val DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN = "(#M#F.*):(.*):(.*)"
    }
}
