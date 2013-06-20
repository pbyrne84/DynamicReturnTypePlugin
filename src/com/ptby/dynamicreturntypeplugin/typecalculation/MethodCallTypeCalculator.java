package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.callvalidator.DeprecatedMethodCallValidator;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;

public class MethodCallTypeCalculator {


    private final DeprecatedMethodCallValidator deprecatedMethodCallValidator;
    private final CallReturnTypeCalculator callReturnTypeCalculator;


    public MethodCallTypeCalculator( DeprecatedMethodCallValidator deprecatedMethodCallValidator, CallReturnTypeCalculator callReturnTypeCalculator ) {
        this.deprecatedMethodCallValidator = deprecatedMethodCallValidator;
        this.callReturnTypeCalculator = callReturnTypeCalculator;
    }


    public String calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl methodReference ) {
        return callReturnTypeCalculator
                .calculateTypeFromMethodParameter( methodReference, classMethodConfig.getParameterIndex() );
    }

}