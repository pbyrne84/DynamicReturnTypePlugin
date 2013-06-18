package com.ptby.dynamicreturntypeplugin.returntype;

import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;

public class FunctionCallTypeCalculator {
    public final MethodCallValidator methodCallValidator = new MethodCallValidator();
    private final CallReturnTypeCalculator callReturnTypeCalculator = new CallReturnTypeCalculator();


    public FunctionCallTypeCalculator() {
    }


    public String calculateFromFunctionCall( FunctionCallConfig functionCallConfig, FunctionReferenceImpl functionReference ) {
        if ( functionCallConfig.getFunctionName().equals( functionReference.getName() ) ) {
            return callReturnTypeCalculator
                    .calculateTypeFromFunctionParameter( functionReference, functionCallConfig.getParameterIndex() );
        }

        return null;
    }

}