package com.ptby.dynamicreturntypeplugin.scanner;

import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;

import java.util.List;

public class FunctionCallReturnTypeScanner {

    private final CallReturnTypeCalculator callReturnTypeCalculator;


    public FunctionCallReturnTypeScanner( CallReturnTypeCalculator callReturnTypeCalculator ){

        this.callReturnTypeCalculator = callReturnTypeCalculator;
    }

    public String getTypeFromFunctionCall( List<FunctionCallConfig> functionCallConfigs,
                                            FunctionReferenceImpl functionReference ) {
        String fullFunctionName = functionReference.getNamespaceName() + functionReference.getName();
        for ( FunctionCallConfig functionCallConfig : functionCallConfigs ) {
            if ( functionCallIsValid( functionCallConfig, fullFunctionName, functionReference ) ) {
                return callReturnTypeCalculator
                        .calculateTypeFromFunctionParameter( functionReference, functionCallConfig.getParameterIndex()
                        );
            }
        }

        return null;
    }


    private boolean functionCallIsValid( FunctionCallConfig functionCallConfig,
                                         String fullFunctionName,
                                         FunctionReferenceImpl functionReference ) {
        return functionCallConfig.getFunctionName().equals( fullFunctionName ) ||
                validateAgainstPossibleGlobalFunction( functionReference, functionCallConfig );
    }


    private boolean validateAgainstPossibleGlobalFunction( FunctionReferenceImpl functionReference, FunctionCallConfig functionCallConfig ) {
        String text = functionReference.getText();
        return !text.contains( "\\" ) &&
                functionCallConfig.getFunctionName().lastIndexOf( "\\" ) != -1 &&
                ( "\\" + functionReference.getName() ).equals( functionCallConfig.getFunctionName() );
    }
}
