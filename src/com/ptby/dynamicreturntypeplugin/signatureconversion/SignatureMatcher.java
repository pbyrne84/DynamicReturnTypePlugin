package com.ptby.dynamicreturntypeplugin.signatureconversion;

public class SignatureMatcher {
    private static final String CLASS_CONSTANT_CALL_PATTERN = "(#*)K#C(.*)\\.(.*)\\|\\?";
    private static final String FIELD_CALL_PATTERN = "(#P#C.*):(.*):(.*)";
    private static final String RETURN_INITIALISED_LOCAL_METHOD_CALL_PATTERN = "(#M#M#C.*):(.*):(.*)";
    private static final String METHOD_CALL_PATTERN = "(#M#C.*):(.*):(.*)";
    private static final String DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN = "(#M#F.*):(.*):(.*)";


    public boolean verifySignatureIsClassConstantFunctionCall( String signature ) {
        return signature.matches( CLASS_CONSTANT_CALL_PATTERN );
    }


    public boolean verifySignatureIsFieldCall( String signature ) {
        return signature.matches( FIELD_CALL_PATTERN );
    }


    public boolean verifySignatureIsMethodCall( String signature ) {
        return signature.matches( METHOD_CALL_PATTERN );
    }


    /**
     * deferred in the sense that there is no \, this causes signatures to change and have to be further processed
     *
     * @param signature
     * @return
     */
    public boolean verifySignatureIsDeferredGlobalFunctionCall( String signature ) {
        return signature.matches( DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN );
    }


    /**
     * It seems the signature #M#M#C is used when there a variable is declared from a return and a method is then
     * called on it
     *
     * @param signature
     * @return
     */
    public boolean verifySignatureIsFromReturnInitialiasedLocalObject( String signature ) {
        return signature.matches( RETURN_INITIALISED_LOCAL_METHOD_CALL_PATTERN );

    }
}
