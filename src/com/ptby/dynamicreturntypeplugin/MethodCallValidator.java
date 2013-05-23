package com.ptby.dynamicreturntypeplugin;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.Variable;
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

        System.out.println("");
        System.out.println(classMethodConfig);

        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof VariableImpl ) {
            return validateAgainstVariableReference( methodReference, classMethodConfig, ( VariableImpl ) classReference );
        } else if ( classReference instanceof FieldReferenceImpl ) {
            return validateAgainstFieldReference( methodReference, classMethodConfig, ( FieldReferenceImpl ) classReference );
        }

        return false;

    }


    private boolean validateAgainstVariableReference( MethodReferenceImpl methodReference, ClassMethodConfig classMethodConfig, VariableImpl variableImpl ) {
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

    private boolean validateAgainstFieldReference( MethodReferenceImpl methodReference, ClassMethodConfig classMethodConfig, FieldReferenceImpl fieldReference ) {
        System.out.println( "??? getReference " + methodReference.getReference() );
        System.out.println( "??? getElement " + methodReference.getReference().getElement() );
        String rawReference = fieldReference.getType().toString();
        String[] split = rawReference.substring( 0, rawReference.lastIndexOf( '|') ).split( "(#P#C|\\.)" );


        System.out.println( "??? getType" + split.length );
        if (split.length < 3  ) {
            return false;
        }

        String currentClassName = split[1];
        String currentField  = split[2];

        System.out.println("currentClassName "+ currentClassName);
        System.out.println( "currentField "+ currentField );

        PhpIndex phpIndex = PhpIndex.getInstance( methodReference.getProject() );
        Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN( currentClassName );
        for ( PhpClass phpClass : classesByFQN ) {
            Collection<Field> fields = phpClass.getFields();
            for ( Field field : fields ) {
                if ( field.getName().equals( currentField ) && field.getType().toString().equals( classMethodConfig.getFqnClassName() ) ) {
                    return true;
                }
            }

        }

        return false;
    }
}