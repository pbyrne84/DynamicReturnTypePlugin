package com.ptby.dynamicreturntypeplugin.scanner;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;

import java.util.List;

public class MethodCallReturnTypeScanner {

    private final CallReturnTypeCalculator callReturnTypeCalculator;


    public MethodCallReturnTypeScanner(   CallReturnTypeCalculator callReturnTypeCalculator ){
        this.callReturnTypeCalculator = callReturnTypeCalculator;
    }

    public GetTypeResponse getTypeFromMethodCall( List<ClassMethodConfig> classMethodConfigList,
                                                  MethodReferenceImpl methodReference ) {
        for ( ClassMethodConfig classMethodConfig : classMethodConfigList ) {
            if ( validateMethodName( methodReference, classMethodConfig ) ) {
                String phpType = callReturnTypeCalculator
                        .calculateTypeFromMethodParameter( methodReference, classMethodConfig.getParameterIndex() );
                if ( phpType != null ) {
                    return new GetTypeResponse( phpType );
                }
            }
        }

        return new GetTypeResponse( null );
    }


    private boolean validateMethodName( MethodReferenceImpl methodReference, ClassMethodConfig classMethodConfig ) {
        return methodReference.getName().equals( classMethodConfig.getMethodName() );
    }
}
