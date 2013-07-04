package com.ptby.dynamicreturntypeplugin.scanner;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;

import java.util.List;

public class MethodCallReturnTypeScanner {

    private final CallReturnTypeCalculator callReturnTypeCalculator;


    public MethodCallReturnTypeScanner( CallReturnTypeCalculator callReturnTypeCalculator ) {
        this.callReturnTypeCalculator = callReturnTypeCalculator;
    }


    public GetTypeResponse getTypeFromMethodCall( List<ClassMethodConfig> classMethodConfigList,
                                                  MethodReference  methodReference ) {
        for ( ClassMethodConfig classMethodConfig : classMethodConfigList ) {
            if ( classMethodConfig.equalsMethodReferenceName( methodReference ) ) {
                GetTypeResponse getTypeResponse = callReturnTypeCalculator
                        .calculateTypeFromMethodParameter( methodReference, classMethodConfig.getParameterIndex() );
                if ( !getTypeResponse.isNull() ) {
                    return getTypeResponse;
                }
            }
        }

        return new GetTypeResponse( null );
    }


}
