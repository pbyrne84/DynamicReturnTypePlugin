package com.ptby.dynamicreturntypeplugin;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

public class MethodCallTypeCalculator {
    public final MethodCallValidator methodCallValidator = new MethodCallValidator();
    private final CallReturnTypeCaster callReturnTypeCaster = new CallReturnTypeCaster();


    public MethodCallTypeCalculator() {
    }


    public PhpType calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl methodReference ) {
        if ( methodCallValidator
                .isValidMethodCall( methodReference, classMethodConfig ) ) {
            return callReturnTypeCaster
                    .calculateTypeFromMethodParameter( methodReference, classMethodConfig.getParameterIndex() );
        }

        return null;
    }

}