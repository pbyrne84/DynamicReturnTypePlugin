package com.ptby.dynamicreturntypeplugin.scanner;

import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;

import java.util.List;

public class FunctionCallReturnTypeScanner {

    private final CallReturnTypeCalculator callReturnTypeCalculator;


    public FunctionCallReturnTypeScanner( CallReturnTypeCalculator callReturnTypeCalculator ) {

        this.callReturnTypeCalculator = callReturnTypeCalculator;
    }


    public GetTypeResponse getTypeFromFunctionCall( List<FunctionCallConfig> functionCallConfigs,
                                                    FunctionReferenceImpl functionReference ) {
        for ( FunctionCallConfig functionCallConfig : functionCallConfigs ) {
            if ( functionCallConfig.equalsFunctionReference( functionReference ) ) {
                GetTypeResponse getTypeResponse = callReturnTypeCalculator
                        .calculateTypeFromFunctionParameter( functionReference, functionCallConfig.getParameterIndex()
                        );
                if ( !getTypeResponse.isNull() && functionCallConfig.hasValidStringClassNameMask() ) {
                    String maskReplacedType = String.format(
                            functionCallConfig.getStringClassNameMask(), getTypeResponse.toString()
                    );

                    return new GetTypeResponse( maskReplacedType );
                }

                return getTypeResponse;
            }
        }

        return new GetTypeResponse( null );
    }
}
