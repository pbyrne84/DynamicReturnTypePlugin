package com.ptby.dynamicreturntypeplugin.signatureconversion;

public class SignatureMatcher {
    private static final String CLASS_CONSTANT_CALL_PATTERN = "(#*)K#C(.*)\\.(.*)\\|\\?";
    private static final String FIELD_CALL_PATTERN = "(#P#C.*):(.*):(.*)";
    private static final String METHOD_CALL_PATTERN = "(#M#C.*):(.*):(.*)";

    public boolean verifySignatureIsClassConstantFunctionCall( String signature ) {
        return signature.matches( CLASS_CONSTANT_CALL_PATTERN );
    }


    public boolean verifySignatureIsFieldCall( String signature ) {
        return signature.matches( FIELD_CALL_PATTERN );
    }


    public boolean verifySignatureIsMethodCall( String signature ) {
        return signature.matches( METHOD_CALL_PATTERN );
    }


}
