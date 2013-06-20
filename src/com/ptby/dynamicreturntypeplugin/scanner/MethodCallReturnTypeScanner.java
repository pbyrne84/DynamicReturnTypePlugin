package com.ptby.dynamicreturntypeplugin.scanner;

import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.typecalculation.MethodCallTypeCalculator;

import java.util.List;

public class MethodCallReturnTypeScanner {

    private final MethodCallTypeCalculator methodCallTypeCalculator;


    public MethodCallReturnTypeScanner(  MethodCallTypeCalculator methodCallTypeCalculator){
        this.methodCallTypeCalculator = methodCallTypeCalculator;
    }

    public String getTypeFromMethodCall( List<ClassMethodConfig> classMethodConfigList, MethodReferenceImpl methodReference ) {
        for ( ClassMethodConfig classMethodConfig : classMethodConfigList ) {
            if ( validateMethodName( methodReference, classMethodConfig ) ) {
                String phpType = methodCallTypeCalculator.calculateFromMethodCall( classMethodConfig, methodReference );
                if ( phpType != null ) {
                    return phpType;
                }
            }
        }

        return null;
    }


    private boolean validateMethodName( MethodReferenceImpl methodReference, ClassMethodConfig classMethodConfig ) {
        return methodReference.getName().equals( classMethodConfig.getMethodName() );
    }
}
