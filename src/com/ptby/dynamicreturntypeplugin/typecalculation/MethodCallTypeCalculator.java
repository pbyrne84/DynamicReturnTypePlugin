package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;

public class MethodCallTypeCalculator {

    private final CallReturnTypeCalculator callReturnTypeCalculator;


    public MethodCallTypeCalculator(  CallReturnTypeCalculator callReturnTypeCalculator ) {
        this.callReturnTypeCalculator = callReturnTypeCalculator;
    }


    public String calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl methodReference ) {
        return callReturnTypeCalculator
                .calculateTypeFromMethodParameter( methodReference, classMethodConfig.getParameterIndex() );
    }

}