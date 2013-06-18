package com.ptby.dynamicreturntypeplugin;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

public class MethodCallTypeCalculator {


    private final MethodCallValidator methodCallValidator;
    private final CallReturnTypeCaster callReturnTypeCaster;


    public MethodCallTypeCalculator( MethodCallValidator methodCallValidator, CallReturnTypeCaster callReturnTypeCaster ) {
        this.methodCallValidator = methodCallValidator;
        this.callReturnTypeCaster = callReturnTypeCaster;
    }


    public String calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl methodReference ) {
        if ( methodCallValidator
                .isValidMethodCall( methodReference, classMethodConfig ) ) {

            return callReturnTypeCaster
                    .calculateTypeFromMethodParameter( methodReference, classMethodConfig.getParameterIndex() );
        }

        return null;
    }

}