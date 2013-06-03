package com.ptby.dynamicreturntypeplugin;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.elements.impl.ClassReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
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

        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof VariableImpl ) {
            return validateAgainstVariableReference( methodReference, classMethodConfig, ( VariableImpl ) classReference );
        } else if ( classReference instanceof FieldReferenceImpl ) {
            return validateAgainstFieldReference( methodReference, classMethodConfig, ( FieldReferenceImpl ) classReference );
        } else if ( classReference instanceof ClassReferenceImpl ) {
            return validateAgainstClassReference( methodReference, classMethodConfig, ( ClassReferenceImpl ) classReference );
        }

        return false;

    }


    private boolean validateAgainstClassReference( MethodReferenceImpl methodReference,
                                                   ClassMethodConfig classMethodConfig,
                                                   ClassReferenceImpl classReference ) {
        if ( classReference.getFQN() == null ) {
            return false;
        }

        return classReference.getFQN().equals( classMethodConfig.getFqnClassName() ) &&
                methodReference.getName()
                               .equals( classMethodConfig
                                       .getMethodName()
                               );

    }


    private boolean validateAgainstVariableReference( MethodReferenceImpl methodReference,
                                                      ClassMethodConfig classMethodConfig,
                                                      VariableImpl variableImpl ) {
        PhpType methodPhpType = variableImpl.getType();
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


    private boolean validateAgainstFieldReference( MethodReferenceImpl methodReference,
                                                   ClassMethodConfig classMethodConfig,
                                                   FieldReferenceImpl fieldReference ) {

        PhpType fieldReferenceType = fieldReference.getType();
        String rawReference = fieldReferenceType.toString();
        if ( rawReference.equals( classMethodConfig.getFqnClassName() ) ) {
            return true;
        }

        int endIndex = rawReference.lastIndexOf( '|' );
        if ( endIndex == -1 ) {
            return false;
        }
        String[] split = rawReference.substring( 0, endIndex ).split( "(#P#C|\\.)" );

        if ( split.length < 3 ) {
            return false;
        }

        String currentClassName = split[ 1 ];
        String currentField = split[ 2 ];

        PhpIndex phpIndex = PhpIndex.getInstance( methodReference.getProject() );
        Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN( currentClassName );
        for ( PhpClass phpClass : classesByFQN ) {
            Collection<Field> fields = phpClass.getFields();
            for ( Field field : fields ) {
                if ( field.getName().equals( currentField ) && field.getType().toString()
                                                                    .equals( classMethodConfig.getFqnClassName() ) ) {
                    return true;
                }
            }
        }

        return false;
    }
}