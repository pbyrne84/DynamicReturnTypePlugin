package com.ptby.dynamicreturntypeplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider;

public class DynamicReturnTypeProvider implements PhpTypeProvider {


    private final MethodCallValidator methodCallValidator = new MethodCallValidator();


    public PhpType getType( PsiElement psiElement ) {
        ClassMethodConfig classMethodConfig
                = new ClassMethodConfig("\\JE\\Test\\Phpunit\\PhockitoTestCase", "verify", 0 );

        return createCustomPhockitoMethodType( psiElement, classMethodConfig );
    }


    private PhpType createCustomPhockitoMethodType( PsiElement psiElement, ClassMethodConfig classMethodConfig ) {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;
            return calculateFromMethodCall( classMethodConfig, classMethod );
        }

        return null;
    }


    private PhpType calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl classMethod ) {
        if ( methodCallValidator
                .isValidMethodCall( classMethod, classMethodConfig ) ) {
            return calculateTypeFromParameter( classMethod, classMethodConfig.getParameterIndex() );
        }

        return null;
    }


    private PhpType calculateTypeFromParameter( MethodReferenceImpl classMethod, int parameterIndex ) {
        PsiElement[] parameters = classMethod.getParameters();
        if ( parameters.length == 0 )                 {
            return null;
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                return type;
            }
        }

        return null;
    }
}
