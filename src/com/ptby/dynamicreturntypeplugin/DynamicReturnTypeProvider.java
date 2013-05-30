package com.ptby.dynamicreturntypeplugin;

import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider;

import java.io.IOException;
import java.util.List;

public class DynamicReturnTypeProvider implements PhpTypeProvider {


    private final MethodCallTypeCalculator methodCallTypeCalculator = new MethodCallTypeCalculator();
    private final CallReturnTypeCaster callReturnTypeCaster = new CallReturnTypeCaster();
    private final ConfigAnalyser configAnalyser;


    public DynamicReturnTypeProvider() {
        configAnalyser = new ConfigAnalyser();
    }


    public PhpType getType( PsiElement psiElement ) {
        try {
            try {
                return createDynmamicReturnType( psiElement );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } catch ( IndexNotReadyException e ) {
        }

        return null;
    }


    private DynamicReturnTypeConfig getDynamicReturnTypeConfig( PsiElement psiElement ) throws IOException {
        DynamicReturnTypeConfig dynamicReturnTypeConfig = configAnalyser.analyseConfig( psiElement.getProject() );
        if ( dynamicReturnTypeConfig == null  ) {
            return new DynamicReturnTypeConfig();
        }

        return dynamicReturnTypeConfig;
    }




    private PhpType createDynmamicReturnType( PsiElement psiElement  ) throws IOException {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;

            return getTypeFromMethodCall( getDynamicReturnTypeConfig( psiElement )
                    .getClassMethodConfigs(), classMethod
            );
        } else if ( PlatformPatterns.psiElement( PhpElementTypes.FUNCTION_CALL ).accepts( psiElement ) ) {
            FunctionReferenceImpl functionReference = ( FunctionReferenceImpl ) psiElement;

            return getTypeFromFunctionCall( getDynamicReturnTypeConfig( psiElement).getFunctionCallConfigs(), functionReference );
        }

        return null;
    }


    private PhpType getTypeFromMethodCall( List<ClassMethodConfig> classMethodConfigList, MethodReferenceImpl methodReference ) {
        for ( ClassMethodConfig classMethodConfig : classMethodConfigList ) {
            PhpType phpType = methodCallTypeCalculator.calculateFromMethodCall( classMethodConfig, methodReference );
            if ( phpType != null ) {
                return phpType;
            }
        }

        return null;
    }


    private PhpType getTypeFromFunctionCall( List<FunctionCallConfig> functionCallConfigs, FunctionReferenceImpl functionReference ) {
        String fullFunctionName = functionReference.getNamespaceName() + functionReference.getName();
        for ( FunctionCallConfig functionCallConfig : functionCallConfigs ) {
            if ( functionCallConfig.getFunctionName().equals( fullFunctionName ) ) {
                return callReturnTypeCaster
                        .calculateTypeFromFunctionParameter( functionReference, functionCallConfig.getParameterIndex()
                        );
            } else if ( validateAgainstPossibleGlobalFunction( functionReference, functionCallConfig ) ) {
                return callReturnTypeCaster
                        .calculateTypeFromFunctionParameter( functionReference, functionCallConfig.getParameterIndex()
                        );
            }
        }

        return null;

    }


    private boolean validateAgainstPossibleGlobalFunction( FunctionReferenceImpl functionReference, FunctionCallConfig functionCallConfig ) {
        String text = functionReference.getText();
        return !text.contains( "\\" ) &&
                functionCallConfig.getFunctionName().lastIndexOf( "\\" ) != -1 &&
                ( "\\" + functionReference.getName() ).equals( functionCallConfig.getFunctionName() );
    }
}
