package com.ptby.dynamicreturntypeplugin.config;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy;
import org.apache.commons.lang.StringUtils;

public class ClassMethodConfig {

    private final String fqnClassName;
    private final ValueReplacementStrategy valueReplacementStrategy;
    private final String methodName;
    private final int parameterIndex;


    public ClassMethodConfig( String fqnClassName,
                              String methodName,
                              int parameterIndex,
                              ValueReplacementStrategy valueReplacementStrategy ) {
        this.fqnClassName = fqnClassName;
        this.valueReplacementStrategy = valueReplacementStrategy;
        this.methodName = methodName.toLowerCase();
        this.parameterIndex = parameterIndex;
    }


    @Override
    public String toString() {
        return "ClassMethodConfig{" +
                "\nfqnClassName='" + fqnClassName + '\'' +
                "\n, valueReplacementStrategy=" + valueReplacementStrategy +
                "\n, methodName='" + methodName + '\'' +
                "\n, parameterIndex=" + parameterIndex +
                '}';
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ClassMethodConfig ) ) {
            return false;
        }

        ClassMethodConfig that = ( ClassMethodConfig ) o;

        if ( parameterIndex != that.parameterIndex ) {
            return false;
        }
        if ( fqnClassName != null ? !fqnClassName.equals( that.fqnClassName ) : that.fqnClassName != null ) {
            return false;
        }
        if ( methodName != null ? !methodName.equals( that.methodName ) : that.methodName != null ) {
            return false;
        }
        if ( valueReplacementStrategy != null ? !valueReplacementStrategy
                .equals( that.valueReplacementStrategy ) : that.valueReplacementStrategy != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = fqnClassName != null ? fqnClassName.hashCode() : 0;
        result = 31 * result + ( valueReplacementStrategy != null ? valueReplacementStrategy.hashCode() : 0 );
        result = 31 * result + ( methodName != null ? methodName.hashCode() : 0 );
        result = 31 * result + parameterIndex;
        return result;
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


    public String formatBeforeLookup( String passedType ) {
        return valueReplacementStrategy.replaceCalculatedValue( passedType );
    }


}
