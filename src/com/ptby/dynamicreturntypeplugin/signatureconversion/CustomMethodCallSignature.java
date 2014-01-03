package com.ptby.dynamicreturntypeplugin.signatureconversion;

public class CustomMethodCallSignature {

    private final String className;
    private final String method;
    private final String parameter;
    private final String rawStringSignature;


    private CustomMethodCallSignature( String className, String method, String parameter, String rawStringSignature ) {
        this.className = className;
        this.method = method;
        this.parameter = parameter;
        this.rawStringSignature = rawStringSignature;
    }


    public CustomMethodCallSignature( String className, String method, String parameter ) {
        this(
                className,
                method,
                parameter,
                className + ":" + method + ":" + parameter
        );
    }


    static public CustomMethodCallSignature createFromString( String signature ) {
        if ( signature == null ) {
            return null;
        }

        String[] signatureParts = signature.split( ":" );
        if ( signatureParts.length < 2  ) {
            return null;
        }

        String parameter = "";
        if ( signatureParts.length > 2 ) {
            parameter = signatureParts[ signatureParts.length - 1 ];
        }

        return new CustomMethodCallSignature( signatureParts[ 0 ], signatureParts[ 1 ], parameter, signature );
    }


    @Override
    public String toString() {
        return "CustomSignature{" +
                "\nclassName='" + className + '\'' +
                "\n, method='" + method + '\'' +
                "\n, parameter='" + parameter + '\'' +
                '}';
    }


    public String getClassName() {
        return className;
    }


    public String getMethod() {
        return method;
    }


    public String getParameter() {
        return parameter;
    }


    public String getRawStringSignature() {
        return rawStringSignature;
    }
}
