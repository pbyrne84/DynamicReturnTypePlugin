package com.ptby.dynamicreturntypeplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider;

import java.util.ArrayList;
import java.util.List;

public class DynamicReturnTypeProvider implements PhpTypeProvider {


    private final MethodCallTypeCalculator methodCallTypeCalculator = new MethodCallTypeCalculator();


    public PhpType getType( PsiElement psiElement ) {
        ClassMethodConfig phockitoTestCaseClassMethodConfig
                = new ClassMethodConfig( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "verify", 0 );

        ArrayList<ClassMethodConfig> classMethodConfigs = new ArrayList<ClassMethodConfig>();
        classMethodConfigs.add( phockitoTestCaseClassMethodConfig );

        return createCustomPhockitoMethodType( psiElement, classMethodConfigs );
    }


    private PhpType createCustomPhockitoMethodType( PsiElement psiElement, List<ClassMethodConfig> classMethodConfigList ) {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;

            for ( ClassMethodConfig classMethodConfig : classMethodConfigList ) {
                PhpType phpType = methodCallTypeCalculator.calculateFromMethodCall( classMethodConfig, classMethod );
                if ( phpType != null ) {
                    return phpType;
                }
            }
        }

        return null;
    }
}
