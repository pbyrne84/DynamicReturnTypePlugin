package com.ptby.dynamicreturntypeplugin;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

public class MethodCallTypeCalculator {
    public final MethodCallValidator methodCallValidator = new MethodCallValidator();


    public MethodCallTypeCalculator() {
    }


    public PhpType calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl classMethod ) {
        if ( methodCallValidator
                .isValidMethodCall( classMethod, classMethodConfig ) ) {
            return calculateTypeFromParameter( classMethod, classMethodConfig.getParameterIndex() );
        }

        return null;
    }


    public PhpType calculateTypeFromParameter( MethodReferenceImpl classMethod, int parameterIndex ) {
        PsiElement[] parameters = classMethod.getParameters();
        if ( parameters.length == 0 ) {
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