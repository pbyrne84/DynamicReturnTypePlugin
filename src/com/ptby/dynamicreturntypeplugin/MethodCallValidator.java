package com.ptby.dynamicreturntypeplugin;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.Collection;

public class MethodCallValidator {
    public MethodCallValidator() {
    }


    public boolean isValidMethodCall( MethodReferenceImpl methodReference, ClassMethodConfig classMethodConfig ) {
        boolean isCorrectMethodName = methodReference.getName().equals( classMethodConfig.getMethodName() );
        if ( !isCorrectMethodName ) {
            return false;
        }

        PhpType methodPhpType = methodReference.getClassReference().getType();
        if ( methodPhpType.toString().equals( classMethodConfig.getFqnClassName() ) ) {
            return true;
        }

        PhpIndex phpIndex = PhpIndex.getInstance( methodReference.getProject() );
        Collection<PhpClass> phpClasses = phpIndex
                .getClassesByFQN( methodPhpType.toString() );
        for ( PhpClass phpClass : phpClasses ) {
            PhpClass currentSuperClass = phpClass.getSuperClass();
            while ( currentSuperClass != null ) {
                String fqn = currentSuperClass.getFQN();
                if ( fqn != null && fqn.equals( classMethodConfig.getFqnClassName() ) ) {
                    return true;
                }
                currentSuperClass = currentSuperClass.getSuperClass();
            }
        }

        return false;
    }
}