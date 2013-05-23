package com.ptby.dynamicreturntypeplugin;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.Collection;

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
        if ( parameters.length < parameterIndex - 1) {
            return null;
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                if ( type.toString().equals( "string" ) ) {
                    return castStringToPhpType( classMethod, element );
                }

                return type;
            }
        }

        return null;
    }


    private PhpType castStringToPhpType( MethodReferenceImpl classMethod, PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" )) {
            return null;
        }

        String classWithoutQuotes = potentialClassName.replaceAll( "(\"|')", "" );
        PhpIndex phpIndex = PhpIndex.getInstance( classMethod.getProject() );
        Collection<PhpClass> phpClass = phpIndex.getClassesByFQN( classWithoutQuotes );
        if ( phpClass.size() == 0  ) {
            return null;
        }
        PhpType phpType = new PhpType();
        phpType.add( classWithoutQuotes );
        return phpType;
    }
}