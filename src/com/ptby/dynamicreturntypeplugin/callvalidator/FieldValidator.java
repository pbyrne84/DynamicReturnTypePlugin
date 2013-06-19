package com.ptby.dynamicreturntypeplugin.callvalidator;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;

import java.util.Collection;

public class FieldValidator {



    public boolean validateAgainstFieldReference( MethodReferenceImpl methodReference,
                                                   ClassMethodConfig classMethodConfig,
                                                   FieldReferenceImpl fieldReference ) {

        PhpType fieldReferenceType = fieldReference.getType();
        String rawReference = fieldReferenceType.toString();

        System.out.println( "???? " + fieldReference.getInferredType( 1000 ) );
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

        Project project = methodReference.getProject();
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        for( int i = 0; i < 100; i++  ){
            System.out.println("fieldReference.getDeclaredType( " + i + ") " + fieldReference.getDeclaredType( i ) );

        }
        Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature( "#P#C\\AdvertsOfDirectEmployersView.oTaskData", null, 0 );
        System.out.println("bySignature.size() " + " " + rawReference + " " + bySignature.size() );

        for ( PhpNamedElement phpNamedElement : bySignature ) {
            System.out.println( "aa " + phpNamedElement.getName());
            System.out.println( "aa " + phpNamedElement.getType());
        }


        Collection <PhpClass> classesByFQN = phpIndex.getClassesByFQN( currentClassName );
        for ( PhpClass phpClass : classesByFQN ) {
            Collection<Field> fields = phpClass.getFields();
            for ( Field field : fields ) {
                if ( field.getName().equals( currentField ) &&
                        field.getType().toString().equals( classMethodConfig.getFqnClassName() ) ) {
                    return true;
                }
            }
        }

        return false;
    }

}