package com.ptby.dynamicreturntypeplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider;

import java.util.List;

public class DynamicReturnTypeProvider implements PhpTypeProvider {


    private final MethodCallTypeCalculator methodCallTypeCalculator = new MethodCallTypeCalculator();
    private final CallReturnTypeCaster callReturnTypeCaster = new CallReturnTypeCaster();


    public PhpType getType( PsiElement psiElement ) {
        List<ClassMethodConfig> classMethodConfigs = new ClassMethodConfigList(
                new ClassMethodConfig( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "verify", 0 ),
                new ClassMethodConfig( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "getFullMock", 0 ),
                new ClassMethodConfig( "\\TaskData", "getObject", 1 )
        );

        List<FunctionCallConfig> functionCallConfigs = new FunctionCallConfigList(
                new FunctionCallConfig( "\\verify", 0 ),
                new FunctionCallConfig( "\\Funky\\moo", 0 )
        );

        DynamicReturnTypeConfig dynamicReturnTypeConfig = new DynamicReturnTypeConfig( classMethodConfigs, functionCallConfigs );


        return createDynmamicReturnType( psiElement, dynamicReturnTypeConfig );
    }


    private PhpType createDynmamicReturnType( PsiElement psiElement, DynamicReturnTypeConfig dynamicReturnTypeConfig ) {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;

            return getTypeFromMethodCall( dynamicReturnTypeConfig.getClassMethodConfigs(), classMethod );
        } else if ( PlatformPatterns.psiElement( PhpElementTypes.FUNCTION_CALL ).accepts( psiElement ) ) {
            FunctionReferenceImpl functionReference = ( FunctionReferenceImpl ) psiElement;

            return getTypeFromFunctionCall( dynamicReturnTypeConfig.getFunctionCallConfigs(), functionReference );
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
                        .calculateTypeFromFunctionParameter( functionReference,functionCallConfig.getParameterIndex()  );
            }
        }

        return null;

    }
}
