package com.ptby.dynamicreturntypeplugin;

import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.List;

public class FunctionCallTypeCalculator {
    public final MethodCallValidator methodCallValidator = new MethodCallValidator();
    private final CallReturnTypeCaster callReturnTypeCaster = new CallReturnTypeCaster();


    public FunctionCallTypeCalculator() {
    }


    public PhpType calculateFromFunctionCall( FunctionCallConfig functionCallConfig, FunctionReferenceImpl functionReference ) {
        if ( functionCallConfig.getFunctionName().equals( functionReference.getName() ) ) {
            return callReturnTypeCaster
                    .calculateTypeFromFunctionParameter( functionReference, functionCallConfig.getParameterIndex() );
        }

        return null;
    }

}