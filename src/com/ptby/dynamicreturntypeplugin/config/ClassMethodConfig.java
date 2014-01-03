package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import org.apache.commons.lang.StringUtils;

public class ClassMethodConfig {

    private final String fqnClassName;
    private final String methodName;
    private final String stringClassNameMask;
    private final int parameterIndex;
    private final boolean hasValidStringClassNameMask;


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ClassMethodConfig that = ( ClassMethodConfig ) o;

        if ( hasValidStringClassNameMask != that.hasValidStringClassNameMask ) {
            return false;
        }
        if ( parameterIndex != that.parameterIndex ) {
            return false;
        }
        if ( fqnClassName != null ? !fqnClassName.equals( that.fqnClassName ) : that.fqnClassName != null ) {
            return false;
        }
        if ( !methodName.equals( that.methodName ) ) {
            return false;
        }
        if ( stringClassNameMask != null ? !stringClassNameMask
                .equals( that.stringClassNameMask ) : that.stringClassNameMask != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = fqnClassName != null ? fqnClassName.hashCode() : 0;
        result = 31 * result + ( methodName != null ? methodName.hashCode() : 0 );
        result = 31 * result + ( stringClassNameMask != null ? stringClassNameMask.hashCode() : 0 );
        result = 31 * result + parameterIndex;
        result = 31 * result + ( hasValidStringClassNameMask ? 1 : 0 );
        return result;
    }


    public ClassMethodConfig( String fqnClassName, String methodName, int parameterIndex, String stringClassNameMask ) {
        this.fqnClassName = fqnClassName;
        this.methodName = methodName.toLowerCase();
        this.stringClassNameMask = stringClassNameMask;
        this.parameterIndex = parameterIndex;
        this.hasValidStringClassNameMask
                = !stringClassNameMask.equals( "" )
                && StringUtils.countMatches( stringClassNameMask, "%" ) == 1;
    }


    public boolean isValid() {
        return !fqnClassName.equals( "" ) &&
                !methodName.equals( "" ) &&
                parameterIndex != -1;
    }


    public boolean methodCallMatches( String actualFqnClassName, String actualMethodName ) {
        return fqnClassName.equals( actualFqnClassName ) && equalsMethodName( actualMethodName );
    }


    public boolean equalsMethodName( String currentMethodName ) {
        String lowerCaseCurrentMethodName = currentMethodName.toLowerCase();
        return lowerCaseCurrentMethodName.equals( methodName );
    }


    public boolean equalsMethodReferenceName( MethodReference methodReference ) {
        String methodName = methodReference.getName();
        return equalsMethodName( methodName );
    }


    public String getFqnClassName() {
        return fqnClassName;
    }


    public int getParameterIndex() {
        return parameterIndex;
    }


    public String formatUsingStringMask( String passedType ) {
        if ( !hasValidStringClassNameMask ) {
            return passedType;
        }

        return String.format( stringClassNameMask, passedType );
    }


    @Override
    public String toString() {
        return "ClassMethodConfig{" +
                "\nfqnClassName='" + fqnClassName + '\'' +
                "\n, methodName='" + methodName + '\'' +
                "\n, stringClassNameMask='" + stringClassNameMask + '\'' +
                "\n, parameterIndex=" + parameterIndex +
                "\n, hasValidStringClassNameMask=" + hasValidStringClassNameMask +
                '}';
    }
}
