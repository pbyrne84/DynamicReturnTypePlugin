package com.ptby.dynamicreturntypeplugin.signatureconversion;

public class SignatureMatcher {
    private static final String CLASS_CONSTANT_CALL_PATTERN = "(#*)K#C(.*)\\.(.*)\\|\\?";
    private static final String FIELD_CALL_PATTERN = "(#P#C.*):(.*):(.*)";
    private static final String METHOD_CALL_PATTERN = "(#M#C.*):(.*):(.*)";
    private static final String DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN = "(#M#F.*):(.*):(.*)";


    public boolean verifySignatureIsClassConstantFunctionCall( String signature ) {
        return signature.matches( CLASS_CONSTANT_CALL_PATTERN );
    }


    public boolean verifySignatureIsClassConstantFunctionCall( CustomMethodCallSignature signature ) {
        return verifySignatureIsClassConstantFunctionCall( signature.getRawStringSignature() );
    }


    public boolean verifySignatureIsFieldCall( CustomMethodCallSignature signature ) {
        return signature.getRawStringSignature().matches( FIELD_CALL_PATTERN );
    }


    public boolean verifySignatureIsMethodCall( CustomMethodCallSignature signature ) {
        return signature.getRawStringSignature().matches( METHOD_CALL_PATTERN );
    }


    /**
     * deferred in the sense that there is no \, this causes signatures to change and have to be further processed
     *
     * @param signature
     * @return
     */
    public boolean verifySignatureIsDeferredGlobalFunctionCall( CustomMethodCallSignature signature ) {
        return signature.getRawStringSignature().matches( DEFERRED_GLOBAL_FUNCTON_CALL_PATTERN );
    }
}
