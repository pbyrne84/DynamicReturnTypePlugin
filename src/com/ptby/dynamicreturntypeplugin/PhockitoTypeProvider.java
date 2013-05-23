package com.ptby.dynamicreturntypeplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider;

import java.util.Collection;

public class PhockitoTypeProvider implements PhpTypeProvider {


    public PhpType getType( PsiElement psiElement ) {
        return createCustomPhockitoMethodType( psiElement );
    }


    private PhpType createCustomPhockitoMethodType( PsiElement psiElement ) {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;
            if ( isValidMethodCall( classMethod, "\\JE\\Test\\Phpunit\\PhockitoTestCase", "verify" ) ) {
                PsiElement[] parameters = classMethod.getParameters();
                if ( parameters.length == 0 )                 {
                    return null;
                }

                PsiElement element = parameters[ 0 ];
                if ( element instanceof PhpTypedElement ) {
                    PhpType type = ( ( PhpTypedElement ) element ).getType();
                    if ( !type.toString().equals( "void" ) ) {
                        return type;
                    }
                }
            }
        }

        return null;
    }


    private boolean isValidMethodCall( MethodReferenceImpl methodReference, String requiredClassName, String requiredMethodName ) {
        boolean isCorrectMethodName = methodReference.getName().equals( requiredMethodName );
        if ( !isCorrectMethodName ) {
            return false;
        }

        PhpType methodPhpType = methodReference.getClassReference().getType();
        if ( methodPhpType.toString().equals( requiredClassName ) ) {
            return true;
        }

        PhpIndex phpIndex = PhpIndex.getInstance( methodReference.getProject() );
        Collection<PhpClass> phpClasses = phpIndex
                .getClassesByFQN( methodPhpType.toString() );
        for ( PhpClass phpClass : phpClasses ) {
            PhpClass currentSuperClass = phpClass.getSuperClass();
            while ( currentSuperClass != null ) {
                String fqn = currentSuperClass.getFQN();
                if ( fqn != null && fqn.equals( requiredClassName ) ) {
                    return true;
                }
                currentSuperClass = currentSuperClass.getSuperClass();
            }
        }

        return false;
    }

}
