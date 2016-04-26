package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider

 class SignatureMatcher {

    fun verifySignatureIsProjectRootVariableCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(PROJECT_ROOT_VARIABLE_PATTERN)
    }

    fun verifySignatureIsClassConstantFunctionCall(signature: String): Boolean {
        return signature.matches(CLASS_CONSTANT_CALL_PATTERN )
    }


    fun verifySignatureIsClassConstantFunctionCall(signature: CustomMethodCallSignature): Boolean {
        return verifySignatureIsClassConstantFunctionCall(signature.rawStringSignature)
    }


    fun verifySignatureIsFieldCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(FIELD_CALL_PATTERN)
    }


    fun verifySignatureIsMethodCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(METHOD_CALL_PATTERN) ||
                verifySignatureIsProjectRootVariableCall(signature)
    }


    /**
     * deferred in the sense that there is no \, this causes signatures to change and have to be further processed
     */
    fun verifySignatureIsDeferredGlobalFunctionCall(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN)
    }

    /**
     * It seems the signature #M#M#C is used when there a variable is declared from a return and a method is then
     * called on it
     */
    fun verifySignatureIsFromReturnInitialiasedLocalObject(signature: CustomMethodCallSignature): Boolean {
        return signature.rawStringSignature.matches(RETURN_INITIALISED_LOCAL_AND_STATIC_METHOD_CALL_PATTERN)

    }

    companion object {
        private val PROJECT_ROOT_VARIABLE_PATTERN: Regex =
                ("(#M#V.*):(.*)" + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + "(.*)").toRegex()

        val CLASS_CONSTANT_CALL_PATTERN: Regex = "(#*)K#C(.*)\\.(.*)".toRegex()


        private val FIELD_CALL_PATTERN: Regex =
                ("(#P#C.*):(.*)" +DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR +"(.*)").toRegex()

        private val RETURN_INITIALISED_LOCAL_AND_STATIC_METHOD_CALL_PATTERN: Regex =
                ("(((#M)+)#M#C.*):(.*)" + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + "(.*)").toRegex()


        private val METHOD_CALL_PATTERN: Regex =
                ("(#M#C.*):(.*)"+ DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR +"(.*)").toRegex()

        private val DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN: Regex =
                ("(#M#F.*):(.*)" + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + "(.*)").toRegex()

         val STARTS_WITH_METHOD_CALL_PATTERN : Regex =
                "((#M)+)#C(.*)".toRegex()

    }
}
