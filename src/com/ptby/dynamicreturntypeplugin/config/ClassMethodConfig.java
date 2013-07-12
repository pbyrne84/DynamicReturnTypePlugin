package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import org.apache.commons.lang.StringUtils;

public class ClassMethodConfig {

    private final String fqnClassName;
    private final String methodName;
    private final String stringClassNameMask;
    private final int parameterIndex;
    private final boolean hasValidStringClassNameMask;


    public ClassMethodConfig( String fqnClassName, String methodName, int parameterIndex, String stringClassNameMask ) {
        this.fqnClassName = fqnClassName;
        this.methodName = methodName.toLowerCase();
        this.stringClassNameMask = stringClassNameMask;
        this.parameterIndex = parameterIndex;
        this.hasValidStringClassNameMask
                = !stringClassNameMask.equals( "" )
                && StringUtils.countMatches( stringClassNameMask, "%s" ) == 1;
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
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ClassMethodConfig that = ( ClassMethodConfig ) o;

        if ( parameterIndex != that.parameterIndex ) {
            return false;
        }
        if ( !fqnClassName.equals( that.fqnClassName ) ) {
            return false;
        }
        if ( !methodName.equals( that.methodName ) ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = fqnClassName.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + parameterIndex;
        return result;
    }


    @Override
    public String toString() {
        return "ClassMethodConfig{" +
                "\nfqnClassName='" + fqnClassName + '\'' +
                "\n, methodName='" + methodName + '\'' +
                "\n, parameterIndex=" + parameterIndex +
                '}';
    }
}
