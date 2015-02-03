package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider

public class SignatureMatcher {

    public fun verifySignatureIsProjectRootVariableCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(PROJECT_ROOT_VARIABLE_PATTERN)
    }

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
        return signature.rawStringSignature.matches(METHOD_CALL_PATTERN) ||
                verifySignatureIsProjectRootVariableCall(signature)
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
        private val PROJECT_ROOT_VARIABLE_PATTERN = "(#M#V.*):(.*)" + DynamicReturnTypeProvider.PARAMETER_SEPARATOR+ "(.*)"
        private val CLASS_CONSTANT_CALL_PATTERN = "(#*)K#C(.*)\\.(.*)"
        private val FIELD_CALL_PATTERN = "(#P#C.*):(.*)" +DynamicReturnTypeProvider.PARAMETER_SEPARATOR+"(.*)"
        private val RETURN_INITIALISED_LOCAL_AND_STATIC_METHOD_CALL_PATTERN = "(((#M)+)#M#C.*):(.*)" + DynamicReturnTypeProvider.PARAMETER_SEPARATOR+ "(.*)"
        private val METHOD_CALL_PATTERN = "(#M#C.*):(.*)"+ DynamicReturnTypeProvider.PARAMETER_SEPARATOR+"(.*)"
        private val DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN = "(#M#F.*):(.*)" + DynamicReturnTypeProvider.PARAMETER_SEPARATOR+ "(.*)"
    }
}
