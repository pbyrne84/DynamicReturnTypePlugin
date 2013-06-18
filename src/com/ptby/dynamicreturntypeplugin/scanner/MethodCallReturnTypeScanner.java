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
            String phpType = methodCallTypeCalculator.calculateFromMethodCall( classMethodConfig, methodReference );
            if ( phpType != null ) {
                return phpType;
            }
        }

        return null;
    }
}
