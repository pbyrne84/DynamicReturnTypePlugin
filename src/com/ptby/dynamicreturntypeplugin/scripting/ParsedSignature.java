package com.ptby.dynamicreturntypeplugin.scripting;

public class ParsedSignature {
    private final String prefix;
    private final String namespace;
    private final String returnClassName;


    public ParsedSignature( String prefix, String namespace, String returnClassName ) {

        this.prefix = prefix;
        this.namespace = namespace;
        this.returnClassName = returnClassName;
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ParsedSignature ) ) {
            return false;
        }

        ParsedSignature that = ( ParsedSignature ) o;

        if ( namespace != null ? !namespace.equals( that.namespace ) : that.namespace != null ) {
            return false;
        }
        if ( prefix != null ? !prefix.equals( that.prefix ) : that.prefix != null ) {
            return false;
        }
        if ( returnClassName != null ? !returnClassName
                .equals( that.returnClassName ) : that.returnClassName != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = prefix != null ? prefix.hashCode() : 0;
        result = 31 * result + ( namespace != null ? namespace.hashCode() : 0 );
        result = 31 * result + ( returnClassName != null ? returnClassName.hashCode() : 0 );
        return result;
    }


    public String getPrefix() {
        return prefix;
    }


    @Override
    public String toString() {
        return "ParsedSignature{" +
                "\nprefix='" + prefix + '\'' +
                "\n, namespace='" + namespace + '\'' +
                "\n, returnClassName='" + returnClassName + '\'' +
                '}';
    }


    public String getNamespace() {
        return namespace;
    }


    public String getReturnClassName() {
        return returnClassName;
    }
}
